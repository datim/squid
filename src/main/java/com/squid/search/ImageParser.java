package com.squid.search;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squid.data.PhotoData;
import com.squid.data.PhotoDataRepository;

/**
 * Responsible for parsing images from pages
 * @author Datim
 *
 */
public class ImageParser {

	static Logger log = LoggerFactory.getLogger(ImageParser.class);

	private final List<String> invalidSuffixes;
	private final List<String> imageBoundaryTags;
	private final List<String> validImageExtensions;
	private final PhotoDataRepository photoRepo;

	/**
	 * Constructor
	 * @param inPhotoRepo The data repository for photos
	 */
	public ImageParser(final PhotoDataRepository inPhotoRepo) {
		photoRepo = inPhotoRepo;
		invalidSuffixes = SearchConstants.getInvalidImageSuffixs();
		imageBoundaryTags = SearchConstants.getImageBoundaryKeyWords();
		validImageExtensions = SearchConstants.getValidImageExtensions();

	}

	/**
	 * Parse photos from an HTML document
	 * @param doc
	 * @return
	 */
	public void discoverImages(final Document doc, String baseUrl, URL nodeURL) {

		baseUrl = baseUrl.toLowerCase();

		// find images from HTML <img> tags
		final Set<String> imageUrls = discoverHTMLImages(doc, baseUrl);

		// find additional images using custom algorithms
		final Set<String> extraImageUrls = customImageSearchAlgorithms(doc, baseUrl);

		log.debug("Discovered " + imageUrls.size() + " img urls and " + extraImageUrls.size() + " extra img urls" );

		// combine the results
		imageUrls.addAll(extraImageUrls);

		// create records out of each image URL
		for (final String imgUrlString: imageUrls) {

			// validate if the image actually exists by requesting the URL header
			if (!validateUrl(imgUrlString)) {
				continue;
			}

			URL imgUrl = null;

    		try {
    			// attempt to generate a URL for the image record
				imgUrl = new URL(imgUrlString);

			} catch (final MalformedURLException e) {
				log.warn("Unable to construct URL for image " + imgUrlString + ". Skipping");
				continue;
			}

        	// get the name of the image
        	final String imageName = imgUrlString.substring(imgUrlString.lastIndexOf("/") + 1);

        	// name and base URL must be unique
        	if (photoRepo.findByNameAndBaseUrl(imageName, baseUrl) != null) {
        		log.debug("Photo " + imageName + " already discovered for url " + baseUrl + ". Will not save");
        		continue;
        	}

        	// URL must be unique
        	if (photoRepo.findByUrl(imgUrl) != null) {
        		log.debug("Image with url " + imgUrl + " already discovered. Will not save");
        		continue;
        	}

        	//
        	// Save the image
        	//

        	final PhotoData photo = new PhotoData();
        	photo.setName(imageName);
        	photo.setNodeUrl(nodeURL);
        	photo.setBaseUrl(baseUrl);

			// attempt to generate a URL for the image record
			photo.setUrl(imgUrl);

        	// save it
    		log.info("Saving new photo: " + photo);
        	photoRepo.save(photo);
		}
	}

	/**
	 * Validate whether a URL exists. Returns true if the URL can be validated
	 * @param url the URL to be validated
	 * @return True if the URL is valid, False if it is not
	 */
	private Boolean validateUrl(String url) {

		Boolean valid = false;

		HttpURLConnection.setFollowRedirects(false);

		HttpURLConnection con;
		try {
			con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod("HEAD");

			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				valid = true;

			} else {
				log.info("Unable to validate that image url " + url + " exists. Skipping");
			}

		} catch (final IOException e) {
			log.warn("Error occurred attempting to validate image " + url);
		}

		return valid;
	}
	/**
	 * Discover URLs for images embedded directly in HTML
	 *
	 * @param doc The document representing a page
	 * @return
	 */
	private Set<String> discoverHTMLImages(final Document doc, String baseUrl) {

		final Set<String> imageResults = new HashSet<>();

		// find all image tags
		final Elements images = doc.select("img");

		for (final Element image: images) {

			// get the source URL
        	String source = image.attr("src").toLowerCase();

        	// ignore empty strings
        	if (source.isEmpty()) {
        		continue;
        	}

    		// ignore images with invalid suffixes
        	if (checkInvalidSuffixName(source)) {
        		continue;
        	}

        	// remove invalid
        	source = stripBoundaryTags(source);

        	// validate whether the image extension is valid
        	if (!isValidImageExtension(source)) {
        		continue;
        	}

        	// if URL is not well-formed, add the base URL
        	if (!source.startsWith("http")) {

        		// add a leading slash if it is missing
        		if (!source.startsWith("/")) {
        			source = "/" + source;
        		}
        		source = baseUrl + source;
        	}

        	// add the results
    		imageResults.add(source);
		}

		return imageResults;
	}

	/**
	 * Return true if the image name has an invalid suffix
	 */
	private boolean checkInvalidSuffixName(String imageName) {

    	for (final String suffix: invalidSuffixes) {

    		if (imageName.endsWith(suffix)) {
    			return true;
    		}
    	}

    	return false;
	}
	/**
	 * Custom search algorithms for finding images on specific web sites
	 *
	 * @param doc The document representing a page
	 * @param baseUrl the base URL of the document
	 */
	private Set<String> customImageSearchAlgorithms(final Document doc, String baseUrl) {

		final Set<String> imageResults = new HashSet<>();

		// search for images from stampin-up URLs
		stampinupImageSearchAlgorithm(doc, baseUrl, imageResults);

		return imageResults;
	}

	/**
	 * Algorithm for finding images on stampin-up pages embedded in Java Script
	 *
	 * @param doc The document representing a page
	 * @param baseUrl the root URL pertaining to the document
	 * @param imageResults the result set to place all discovered images
	 */
	private void stampinupImageSearchAlgorithm(final Document doc, String baseUrl, Set<String> imageResults) {

		// Look for images embedded in Java Script on 'Stampin-Up.com'.  Images URLs are embedded with
		// the following syntax:
		//
		//   	imgList[0] = ["/images/EC/139315S.jpg", "/images/EC/139315G.jpg", "800"];
		//
		// Regular expression that can be used to discover these images:
		// 		'imgList\[[0-9]\] = \["([a-zA-Z0-9\./]+)", "([a-zA-Z0-9\./]+)",'
		//

		// construct a regular expression for discovering images embedded in java script.
		final String imageRegex =     "imgList\\[[0-9]\\] = \\[\"([a-zA-Z0-9\\./]+)\", \"([a-zA-Z0-9\\./]+)\",";

		final List<Element> scripts = doc.select("script");	// find all Java Script references in the document

		for (final Element script: scripts) {
			// perform regular expression on java script elements
			final Pattern regexPattern = Pattern.compile(imageRegex);
			final Matcher regexMatch = regexPattern.matcher(script.html());

			while (regexMatch.find()) {
				// construct the matching image URL and add it to the result list
				final String imageUrl1 = baseUrl + regexMatch.group(1);
				final String imageUrl2 = baseUrl + regexMatch.group(2);

				log.info("Found stampin-up image " + imageUrl1 + ", " + imageUrl2);

				imageResults.add(imageUrl1);
				imageResults.add(imageUrl2);
			}
		}
	}

	/**
	 * Validate whether an image as a valid extension
	 */
	private boolean isValidImageExtension(String imageName) {
		boolean valid = false;

		for(final String extension: validImageExtensions) {
			if (imageName.endsWith(extension)) {
				valid = true;
				break;
			}
		}

		return valid;
	}

	/**
	 * Remove sub strings from an image name after
	 * a boundary tag such as '#' or '?'
	 */
	private String stripBoundaryTags(String imageName) {

		String strippedName = imageName;

		for (final String boundaryTag: imageBoundaryTags) {

			if (imageName.contains(boundaryTag)) {
				// image contains a boundary tag. Strip off sub-string
				strippedName = imageName.substring(0, imageName.indexOf(boundaryTag));
				break;
			}
		}

		return strippedName;
	}
}
