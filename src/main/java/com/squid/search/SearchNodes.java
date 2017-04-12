package com.squid.search;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.squid.data.NodeData;
import com.squid.data.NodeDataRepository;
import com.squid.data.PhotoData;
import com.squid.data.PhotoDataRepository;
import com.squid.data.SearchStatusData;
import com.squid.data.SearchStatusRepository;

/**
 * Search nodes in a new thread
 *
 */
public class SearchNodes implements Runnable {

	static Logger log = Logger.getLogger(SearchNodes.class.getName());

	private final URL searchUrl;
	private final URL parentUrl;
	private final URL rootUrl;
	private final long maxNodes;
	private final long maxImages;
	private final PhotoDataRepository photoRepo;
	private final NodeDataRepository nodeRepo;
	private final SearchStatusRepository searchStatusRepo;
	private final List<String> imageBoundaryTags;
	private final List<String> invalidSuffixes;
	private final List<String> pageBoundaryTags;
	private final List<String> validImageExtensions;
	private List<String> nodeSuffixExclusions = null;
	private final BlockingQueue<PageSearchRequest> requestQueue;

	/**
	 * Constructor
	 * @param huntUrl
	 * @param pageRequestsQueue
	 * @param searchStatusRepo
	 * @param nodeRepo
	 * @param photoRepo
	 */
	public SearchNodes(final URL huntUrl, final URL parentUrl, final URL rootUrl, final PhotoDataRepository photoRepoIn, final NodeDataRepository nodeRepoIn,
					   final SearchStatusRepository inSearchRepo, long maxImages, long maxNodes,
					   final BlockingQueue<PageSearchRequest> pageRequestsQueue) {
		searchUrl = huntUrl;
		this.rootUrl = rootUrl;
		this.parentUrl = parentUrl;
		photoRepo = photoRepoIn;
		nodeRepo = nodeRepoIn;
		searchStatusRepo = inSearchRepo;
		this.maxImages = maxImages;
		this.maxNodes = maxNodes;
		requestQueue = pageRequestsQueue;

		// define constants
		imageBoundaryTags = SearchConstants.getImageBoundaryKeyWords();
		invalidSuffixes = SearchConstants.getInvalidImageSuffixs();
		pageBoundaryTags = SearchConstants.getPageURLBoundaryKeyWords();
		validImageExtensions = SearchConstants.getValidImageExtensions();
		nodeSuffixExclusions = SearchConstants.getInvalidPageExtensions();
	}

	/**
	 * Execute a new thread
	 */
	@Override
	public void run() {
		try {
			startSearch();

		} catch (final IOException e) {
			log.severe("An error occurred while searching page: " + searchUrl + ". Exception: " + e);
		}
	}

	/**
	 * Recursively find all photos in a series of links. Perform a breadth-first search
	 * @param huntUrl
	 * @return
	 * @throws IOException
	 */
	private void startSearch() throws IOException {

		log.info("Starting search for page " + searchUrl);

		if (nodeRepo.count() >= maxNodes) {

			// max number of nodes reached. Update status and quit
			log.info("Reached maximum visited pages: " + maxNodes + ". Ending search");

			if (!searchStatusRepo.findByUrl(searchUrl.toString()).getStatus().equals(SearchStatusData.SearchStatus.Complete)) {
				// status not yet set to complete. Set it
				SearchConstants.setSearchStatus(rootUrl, nodeRepo.count(), photoRepo.count(), new Long(maxNodes), SearchStatusData.SearchStatus.Complete, searchStatusRepo);
			}

			return;
		}

		// update the search status
		SearchConstants.setSearchStatus(rootUrl, nodeRepo.count(), photoRepo.count(), new Long(maxNodes), SearchStatusData.SearchStatus.InProgress, searchStatusRepo);

		log.fine("Discovered content loop count" + nodeRepo.count());

		//
		// Use JSoup to identify the URL and its sub pages
		//

		// only search this page if it has not yet been searched
		if (nodeRepo.findByUrl(searchUrl) == null) {

			Document currentPageDoc = null;

			log.info("Searching page " + searchUrl.toString());

			try {
				currentPageDoc = Jsoup.connect(searchUrl.toString()).get();

			} catch (SocketTimeoutException | HttpStatusException e) {
				// catch the exception and exist the search for this page
				log.severe ("Unable to fetch URL: " + searchUrl.toString() + ". Exception: " + e);
				return;
			}

			final String baseUrl = searchUrl.getProtocol() + "://" + searchUrl.getHost();

			// save a record of this page
			NodeData pageRecord = new NodeData();
			pageRecord.setUrl(searchUrl);
			pageRecord.setVisited((currentPageDoc != null));
			pageRecord.setParentUrl((parentUrl != null) ? parentUrl : null); // set parent node if it exists

			log.fine("saving page, url: " + pageRecord.getUrl() + ", parent: " + pageRecord.getParent());

			// save the node if it doesn't exist
			if (nodeRepo.findByUrl(pageRecord.getUrl()) == null) {
				pageRecord = nodeRepo.save(pageRecord);
			}

			if (currentPageDoc != null) {
				// find all photos associated with this URL
				discoverImages(currentPageDoc, baseUrl, searchUrl);

				// find all links referenced by this URL
				discoverSubPages(currentPageDoc, searchUrl);
			}

		} else {
			log.info("Node " + searchUrl.toString() + " has previously been visited. Skipping");
		}
	}


	/**
	 * Discover all nodes associated with the current node
	 * @param Document
	 * @return
	 */
	private void discoverSubPages(final Document doc, URL parentURL) {

		final Elements urlElements = doc.select("a[href]");
		final String parentHost = parentURL.getHost();

		// find all the URL nodes on this page.
		for (final Element urlElement: urlElements) {

			String urlString = urlElement.attr("abs:href");

			// Strip off in-page tags from URL
			urlString = stripBoundaryTags(urlString, pageBoundaryTags);

			// if node has an invalid suffix, skip it
			if (checkInvalidSuffixName(urlString, nodeSuffixExclusions)) {
				continue;
			}

			URL childUrl = null;

			try {
				childUrl = new URL(urlString);

			} catch (final MalformedURLException e) {
				System.out.println("Malformed url for " + urlString);
				continue;
			}

			// ignore links that do not the same root page as the parent
			// This allows us to skip links that reference third party pages
			if (!childUrl.getHost().equals(parentHost)) {
				continue;
			}

			// don't visit a URL that has already been seen
			if (nodeRepo.findByUrl(childUrl) != null) {
				continue;
			}

			// add this URL to the queue to be visited
			log.info("Found new URL: " + urlString);
			System.out.println("found new URL: " + urlString);

			// submit this page for search
			try {
				requestQueue.put(new PageSearchRequest(childUrl, rootUrl, parentUrl));

			} catch (final InterruptedException e) {
				// catch the exception and move on
				log.severe("Unable to submit url " + childUrl + " for search. Exception: " + e);
			}
		}
	}

	/**
	 * Parse photos from an HTML document
	 * @param doc
	 * @return
	 */
	private void discoverImages(final Document doc, String baseUrl, URL nodeURL) {

		baseUrl = baseUrl.toLowerCase();

		// find images from HTML <img> tags
		final Set<String> imageUrls = discoverHTMLImages(doc, baseUrl);

		// find additional images using custom algorithms
		final Set<String> extraImageUrls = customAlgorithms(doc, baseUrl);

		log.fine("Discovered " + imageUrls.size() + " img urls and " + extraImageUrls.size() + " extra img urls" );

		// combine the results
		imageUrls.addAll(extraImageUrls);

		// create records out of each image URL
		for (final String imgUrl: imageUrls) {

			// validate if the image actually exists by requesting the URL header
			if (!validateUrl(imgUrl)) {
				continue;
			}

        	// get the name of the image
        	final String imageName = imgUrl.substring(imgUrl.lastIndexOf("/") + 1);

        	// don't save the photo if it has already been saved for this URL
        	if (photoRepo.findByNameAndBaseUrl(imageName, baseUrl) != null) {
        		log.fine("Photo " + imageName + " already discovered for url " + baseUrl + ". Will not save");
        		continue;
        	}

        	if (photoRepo.count() >= maxImages) {
        		// don't exceed the maximum image count
        		log.info("Max image count of " + maxImages + " reached");
        		return;
        	}

        	//
        	// Save the image
        	//

        	final PhotoData photo = new PhotoData();
        	photo.setName(imageName);
        	photo.setNodeUrl(nodeURL);
        	photo.setBaseUrl(baseUrl);

    		try {
    			// attempt to generate a URL for the image record
				photo.setUrl(new URL(imgUrl));

			} catch (final MalformedURLException e) {
				log.warning("Unable to construct URL for image " + imgUrl + ". Skipping");
				continue;
			}

        	// save it
        	photoRepo.save(photo);
		}
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
        	if (checkInvalidSuffixName(source, invalidSuffixes)) {
        		continue;
        	}

        	// remove invalid
        	source = stripBoundaryTags(source, imageBoundaryTags);

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
	 * Custom search algorithms for finding images on specific web sites
	 *
	 * @param doc The document representing a page
	 * @param baseUrl the base URL of the document
	 */
	private Set<String> customAlgorithms(final Document doc, String baseUrl) {

		final Set<String> imageResults = new HashSet<>();

		// search for images from stampin-up URLs
		stampinupAlgorithm(doc, baseUrl, imageResults);

		return imageResults;
	}

	/**
	 * Algorithm for finding images on stampin-up pages embedded in Java Script
	 *
	 * @param doc The document representing a page
	 * @param baseUrl the root URL pertaining to the document
	 * @param imageResults the result set to place all discovered images
	 */
	private void stampinupAlgorithm(final Document doc, String baseUrl, Set<String> imageResults) {

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
			log.warning("Error occurred attempting to validate image " + url);
		}

		return valid;
	}

	/**
	 * Return true if a name contains an invalid suffix
	 */
	private boolean checkInvalidSuffixName(String name, final List<String> suffixList) {

    	for (final String suffix: suffixList) {

    		if (name.endsWith(suffix)) {
    			return true;
    		}
    	}

    	return false;
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
	private String stripBoundaryTags(String name, List<String> boundaryTags) {

		String strippedName = name;

		for (final String boundaryTag: boundaryTags) {

			if (name.contains(boundaryTag)) {
				// remove trailing substring off name
				strippedName = name.substring(0, name.indexOf(boundaryTag));
				break;
			}
		}

		return strippedName;
	}


}
