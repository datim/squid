package com.squid.search;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squid.data.NodeData;
import com.squid.data.NodeDataRepository;
import com.squid.data.PhotoData;
import com.squid.data.PhotoDataRepository;
import com.squid.data.SearchStatusData;
import com.squid.data.SearchStatusRepository;

// TODO: Refactor and split into multiple classes

/**
 * Parse a page. Search for all images and sub pages. Sub pages parsing
 * will be delegated to threads on the thread queue.
 */
public class PageParser extends Thread {

	static Logger log = LoggerFactory.getLogger(PageParser.class);

	static String URL_INLINE_TAG = "#";

	private List<String> nodeSuffixExclusions = null;
	private int vistedNodes = 0;
	private URL searchUrl;
	private long maxNodes;
	private long maxImages;
	private PhotoDataRepository photoRepo;
	private NodeDataRepository nodeRepo;
	private SearchStatusRepository searchStatusRepo;
	private SearchStatusData searchStatus;
	private List<String> imageBoundaryTags;
	private List<String> invalidSuffixes;
	private List<String> pageBoundaryTags;
	private List<String> validImageExtensions;
	private BlockingQueue<PageSearchRequest> newPageRequestQueue;

	/**
	 * Private class to store information for nodes that
	 * have not yet been traversed
	 */
	private class PageDetails {

		// constructor
		public PageDetails(final URL url, final URL parentURL) {
			this.url = url;
			parentUrl = parentURL;
		}

		public URL url;
		public URL parentUrl;
	}

	/**
	 * Constructor
	 * @param huntUrl
	 * @param pageRequestsQueue
	 * @param searchStatusRepo
	 * @param nodeRepo
	 * @param photoRepo
	 */
	public PageParser(final URL huntUrl, final PhotoDataRepository photoRepoIn, final NodeDataRepository nodeRepoIn,
					       final SearchStatusRepository inSearchRepo, long maxImages, long maxNodes,
					        BlockingQueue<PageSearchRequest> pageRequestsQueue) {

		searchUrl = huntUrl;
		vistedNodes = 0;
		photoRepo = photoRepoIn;
		nodeRepo = nodeRepoIn;
		searchStatusRepo = inSearchRepo;
		this.maxImages = maxImages;
		this.maxNodes = maxNodes;
		imageBoundaryTags = SearchConstants.getImageBoundaryKeyWords();
		invalidSuffixes = SearchConstants.getInvalidImageSuffixs();
		pageBoundaryTags = SearchConstants.getPageURLBoundaryKeyWords();
		validImageExtensions = SearchConstants.getValidImageExtensions();
		newPageRequestQueue = pageRequestsQueue;

		// create exclusions
		nodeSuffixExclusions = new ArrayList<>();
		pageSuffixExclusions();
	}

	/**
	 * Define extensions to pages that we want to avoid
	 */
	private void pageSuffixExclusions() {
		nodeSuffixExclusions.add("css");
		nodeSuffixExclusions.add("pdf");
	}

	/**
	 * Execute a new thread
	 */
	@Override
	public void run() {
		try {
			startSearch(searchUrl);
		} catch (IOException e) {
			log.error("An error occurred while searching sub-pages: " + e);
		}
	}

	/**
	 * Start a recursive, breadth-first search
	 * @param huntUrl
	 * @return
	 * @throws IOException
	 */
	private void startSearch(final URL huntUrl) throws IOException {

		// don't exceed the max number of nodes
		if (isPageOrImageLimitReached()) {
			markSearchComplete();
			return;
		}

		log.info("Search page " + huntUrl);

		final long imageCount = 0;
		final Set<URL> vistedURLs = new HashSet<>();
		final Queue<PageDetails> toVisitUrls = new LinkedList<>();

		final PageDetails currentPage = new PageDetails(huntUrl, null);

		// begin the search
		discoverPages(currentPage, imageCount, toVisitUrls, vistedURLs);
	}


	/**
	 * Update the search status
	 *
	 * @param huntUrl The url being searched
	 * @param nodeCount The number of pages that have been discovered
	 * @param imageCount The number of images that have been discovered
	 * @param status The status of the search
	 */
	private void updateSearchStatus(Long nodeCount, Long imageCount, SearchStatusData.SearchStatus status) {

		// maintain one record for status. If the record already exists, update it
		searchStatus = searchStatusRepo.findByUrl(searchUrl.toString());

		// FIXME TODO - don't update the counts, just the status

		if (searchStatus == null) {
			// record doesn't exist, create a new one
			searchStatus = new SearchStatusData();

		} else if (searchStatus.getStatus() == status) {
			// this status is already up to date. Don't continue
			return;
		}

		// update the status results
		searchStatus.setUrl(searchUrl.toString());
		searchStatus.setMaxDepth(maxNodes);
		//searchStatus.setNodeCount(nodeCount);  FIXME TODO DELETE
		//searchStatus.setImageCount(imageCount); FIXME TODO DELETE
		searchStatus.setStatus(status);

		// save the status
		searchStatusRepo.save(searchStatus);
	}

	/**
	 * Check whether the maximum pages or images have been reached
	 * @return
	 */
	private boolean isPageOrImageLimitReached() {

		boolean limitReached = false;

		long numImages = photoRepo.count();
		long numPages = nodeRepo.count();
		if ((numImages >= maxImages) || (numPages >= maxNodes)) {
			log.info("We've reached the maximum threashold for photos(" + numImages + ") or pages(" + numPages + ")");
			limitReached = true;
		}

		return limitReached;
	}

	/**
	 * update the search status to mark the search complete
	 * @return
	 */
	private void markSearchComplete() {
		// update the search status with complete results
		updateSearchStatus(nodeRepo.count(), photoRepo.count(), SearchStatusData.SearchStatus.Complete);
	}

	/**
	 * Recursively find all photos in a series of links. Perform a breadth-first search
	 * @param huntUrl
	 * @return
	 * @throws IOException
	 */
	private void discoverPages(final PageDetails currentPage, Long imageCount, Queue<PageDetails> toVisitUrls, Set<URL> vistedURLs) throws IOException {

		// update status
		updateSearchStatus(nodeRepo.count(), photoRepo.count(), SearchStatusData.SearchStatus.InProgress);
		log.info("page count " + nodeRepo.count() + ", image count " + photoRepo.count());

		// don't exceed the max number of nodes
		/*
		 * FIXME DELETE
		if (vistedNodes >= maxNodes) {
			log.info("Reached maximum visited pages: " + maxNodes + ". Ending search");
			return;
		}
		*/

		//
		// Use JSoup to identify the URL and its sub pages
		//

		final URL currentPageUrl = currentPage.url;

		// only search this page if it has not yet been searched
		if (nodeRepo.findByUrl(currentPageUrl) == null) {

			Document currentPageDoc = null;

			log.info("Searching page " + currentPageUrl.toString());

			try {
				currentPageDoc = Jsoup.connect(currentPageUrl.toString()).get();

			} catch (SocketTimeoutException | HttpStatusException e) {
				// catch the exception and exist the search for this page
				log.error("Unable to fetch URL: " + currentPageUrl.toString() + ". Exception: " + e);
				return;
			}

			final String baseUrl = currentPageUrl.getProtocol() + "://" + currentPageUrl.getHost();

			// save a record of this page
			NodeData pageRecord = new NodeData();
			pageRecord.setUrl(currentPageUrl);
			pageRecord.setVisited((currentPageDoc != null));
			pageRecord.setParentUrl((currentPage.parentUrl != null) ? currentPage.parentUrl : null); // set parent node if it exists

			log.debug("Saving page " + pageRecord);

			// save the node
			pageRecord = nodeRepo.save(pageRecord);

			if (currentPageDoc != null) {
				// find all photos associated with this URL
				discoverImages(currentPageDoc, imageCount, baseUrl, currentPageUrl);

				// find all links referenced by this URL
				discoverSubNodes(currentPageDoc, currentPageUrl, toVisitUrls, vistedURLs);
			}

		} else {
			log.info("Node " + currentPageUrl.toString() + " has previously been visited. Skipping");
		}



/*
        FIXME DELETE;
		while (!toVisitUrls.isEmpty()) {

			final PageDetails childNode = toVisitUrls.remove();

			discoverPages(childNode, imageCount, toVisitUrls, vistedURLs);

			// check gate parameters
			if (imageCount > maxImages || vistedNodes >= maxNodes) {
				break;
			}
		}
*/
	}


	/**
	 * Discover all nodes associated with the current node
	 * @param Document
	 * @return
	 */
	private void discoverSubNodes(final Document doc, URL parentURL, Queue<PageDetails> toVistUrls, Set<URL> discoveredUrls) {

		final Elements urlElements = doc.select("a[href]");
		final String parentHost = parentURL.getHost();

		// find all the URL nodes on this page.
		for (Element urlElement: urlElements) {

			String urlString = urlElement.attr("abs:href");

			// Strip off in-page tags from URL
			if (urlString.contains(URL_INLINE_TAG)) {
				urlString = urlString.substring(0, urlString.indexOf(URL_INLINE_TAG));
			}
			boolean notHtmlPage = false;

			// Exclude URLs with particular suffixes
			for (String suffix: nodeSuffixExclusions) {

				if (urlString.endsWith(suffix)) {
					notHtmlPage = true;
					continue;
				}
			}

			if (notHtmlPage) {
				continue;
			}

			URL childUrl = null;

			try {
				childUrl = new URL(urlString);

			} catch (MalformedURLException e) {
				log.warn("Cannot process malformed url " + urlString);
				continue;
			}

			// ignore links that don't have the same base as the parent
			if (!childUrl.getHost().equals(parentHost)) {
				continue;
			}

			/*
			// don't visit a URL that has already been seen
			if (discoveredUrls.contains(childUrl)) {
				continue;
			}
			*/

			// don't visit a URL that has already been seen
			if (nodeRepo.findByUrl(childUrl) != null) {
				continue;
			}

			log.info("Discovered new URL: " + urlString);

			// Add a new search request for this page
			try {
				newPageRequestQueue.put(new PageSearchRequest(childUrl, parentURL, searchUrl));

			} catch (InterruptedException e) {
				// log an error and continue
				log.error("Unable to place new search record on queue " + e);
			}

			// FIXME DELETE
			//toVistUrls.add(new PageDetails(childUrl, parentURL));

			// record this URL as having been seen
			//discoveredUrls.add(childUrl);
		}
	}

	/**
	 * Parse photos from an HTML document
	 * @param doc
	 * @return
	 */
	private void discoverImages(final Document doc, Long imageCount, String baseUrl, URL nodeURL) {

		baseUrl = baseUrl.toLowerCase();

		// find images from HTML <img> tags
		final Set<String> imageUrls = discoverHTMLImages(doc, baseUrl);

		// find additional images using custom algorithms
		final Set<String> extraImageUrls = customAlgorithms(doc, baseUrl);

		log.debug("Discovered " + imageUrls.size() + " img urls and " + extraImageUrls.size() + " extra img urls" );

		// combine the results
		imageUrls.addAll(extraImageUrls);

		// create records out of each image URL
		for (String imgUrlString: imageUrls) {

			// validate if the image actually exists by requesting the URL header
			if (!validateUrl(imgUrlString)) {
				continue;
			}

			URL imgUrl = null;

    		try {
    			// attempt to generate a URL for the image record
				imgUrl = new URL(imgUrlString);

			} catch (MalformedURLException e) {
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

        	imageCount++; // increment image count

        	PhotoData photo = new PhotoData();
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

		} catch (IOException e) {
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

		for (Element image: images) {

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

    	for (String suffix: invalidSuffixes) {

    		if (imageName.endsWith(suffix)) {
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

		for(String extension: validImageExtensions) {
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

		for (String boundaryTag: imageBoundaryTags) {

			if (imageName.contains(boundaryTag)) {
				// image contains a boundar tag. Strip off sub-string
				strippedName = imageName.substring(0, imageName.indexOf(boundaryTag));
				break;
			}
		}

		return strippedName;
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

		for (Element script: scripts) {
			// perform regular expression on java script elements
			final Pattern regexPattern = Pattern.compile(imageRegex);
			Matcher regexMatch = regexPattern.matcher(script.html());

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
}
