package com.squid.search;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squid.data.NodeData;
import com.squid.data.NodeDataRepository;
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

	private List<String> invalidPageFileNameExtensions = null;
	private final URL searchUrl;
	private final URL parentUrl;
	private final URL rootUrl;
	private final long maxNodes;
	private final long maxImages;
	private final NodeDataRepository nodeRepo;
	private final SearchStatusRepository searchStatusRepo;
	private final List<String> pageBoundaryTags;
	private final PhotoDataRepository photoRepo;


	private SearchStatusData searchStatus;
	private final ImageParser imgParser;
	private final BlockingQueue<PageSearchRequest> newPageRequestQueue;

	/**
	 * Constructor
	 * @param huntUrl
	 * @param url2
	 * @param url
	 * @param photoRepoIn
	 * @param nodeRepoIn
	 * @param inSearchRepo
	 * @param maxImages
	 * @param maxNodes
	 * @param pageRequestsQueue
	 */
	public PageParser(final URL huntUrl, final URL inParentUrl, final URL inRootUrl, final PhotoDataRepository photoRepoIn,
			          final NodeDataRepository nodeRepoIn, final SearchStatusRepository inSearchRepo,
			          long maxImages, long maxNodes,
					  final BlockingQueue<PageSearchRequest> pageRequestsQueue) {

		imgParser = new ImageParser(photoRepoIn);
		photoRepo = photoRepoIn;
		searchUrl = huntUrl;
		parentUrl = inParentUrl;
		rootUrl = inRootUrl;
		nodeRepo = nodeRepoIn;
		searchStatusRepo = inSearchRepo;
		this.maxImages = maxImages;
		this.maxNodes = maxNodes;
		invalidPageFileNameExtensions = SearchConstants.getInvalidPageExtensions();
		pageBoundaryTags = SearchConstants.getPageURLBoundaryKeyWords();

		newPageRequestQueue = pageRequestsQueue;
	}

	/**
	 * Execute a new thread
	 */
	@Override
	public void run() {
		try {
			startSearch();

		} catch (final IOException e) {
			log.error("An error occurred while searching sub-pages: " + e);
		}
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

		final long numImages = photoRepo.count();
		final long numPages = nodeRepo.count();
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
	private void startSearch() throws IOException {

		// don't exceed the max number of nodes
		if (isPageOrImageLimitReached()) {
			markSearchComplete();
			return;
		}

		log.info("Search page " + searchUrl);

		// update status
		updateSearchStatus(nodeRepo.count(), photoRepo.count(), SearchStatusData.SearchStatus.InProgress);
		log.info("page count " + nodeRepo.count() + ", image count " + photoRepo.count());

		//
		// Use JSoup to identify the URL and its sub pages
		//

		final URL currentPageUrl = searchUrl;

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
			pageRecord.setParentUrl(parentUrl); // set parent. May be null

			log.debug("Saving page " + pageRecord);

			// save the node
			pageRecord = nodeRepo.save(pageRecord);

			if (currentPageDoc != null) {
				// find all photos associated with this URL
				imgParser.discoverImages(currentPageDoc, baseUrl, currentPageUrl);

				// find all links referenced by this URL
				discoverSubPages(currentPageDoc, currentPageUrl);
			}

		} else {
			log.info("Node " + currentPageUrl.toString() + " has previously been visited. Skipping");
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
			if (urlString.contains(URL_INLINE_TAG)) {
				urlString = urlString.substring(0, urlString.indexOf(URL_INLINE_TAG));
			}
			boolean notHtmlPage = false;

			// Exclude URLs with particular suffixes
			for (final String suffix: invalidPageFileNameExtensions) {

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

			} catch (final MalformedURLException e) {
				log.warn("Cannot process malformed url " + urlString);
				continue;
			}

			// ignore links that don't have the same base as the parent
			if (!childUrl.getHost().equals(parentHost)) {
				continue;
			}

			// don't visit a URL that has already been seen
			if (nodeRepo.findByUrl(childUrl) != null) {
				continue;
			}

			log.info("Discovered new URL: " + urlString);

			// Add a new search request for this page
			try {
				newPageRequestQueue.put(new PageSearchRequest(childUrl, searchUrl, rootUrl));

			} catch (final InterruptedException e) {
				// log an error and continue
				log.error("Unable to place new search record on queue " + e);
			}
		}
	}





}
