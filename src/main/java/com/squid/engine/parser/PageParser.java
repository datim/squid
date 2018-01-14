package com.squid.engine.parser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squid.data.FoundPage;
import com.squid.data.Query;
import com.squid.engine.requests.ImageRequestMsg;
import com.squid.engine.requests.PageRequestMsg;
import com.squid.engine.requests.RequestMsg;
import com.squid.service.RepositoryService;

/**
 * Parse a page, looking for sub pages and images
 * FIXME - improve image search by removing specific algorithms and loading page with javascript instead of static HTML
 * @author Datim
 *
 */
public class PageParser {

    protected static final Logger log = LoggerFactory.getLogger(PageParser.class);
	private final BlockingQueue<RequestMsg> requestQueue;
	private final RepositoryService repoService;

	private static final List<String> pageBoundaries = Arrays.asList("#");
	private static final List<String> invalidExtensions = Arrays.asList("css", "pdf");
	private static final List<String> validImageExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");
	private static final List<String> filterFileList = Arrays.asList("spacer.gif");

	// constructor
	public PageParser(RepositoryService repoService, BlockingQueue<RequestMsg> requestQueue) {
		this.repoService = repoService;
		this.requestQueue = requestQueue;
	}

	/**
	 * Parse a page
	 * @param page The page to parse
	 * @param query The query that this page is being parsed for
	 */
	public void parsePage(final FoundPage page, final Query query) {

		Document pageNode = null;

		// render the page before parsing it
		// FIXME add time outs
		//final HtmlUnitDriver driver = new HtmlUnitDriver();
		//driver.get(page.getUrl().toString());

		// parse the page
		//pageNode = Jsoup.parse(driver.getPageSource());
		try {
			pageNode = Jsoup.connect(page.getUrl().toString()).get();
		} catch (final IOException e) {
			// handle message and return
			log.error("Unable to parse url '{}'. Exception: {}", page.getUrl(), e.getMessage());
			return;
		}

		findSubPages(pageNode, query, page);
		findSubImages(pageNode, query, page);
	}


	/**
	 * Find all sub pages for a requested page document
	 * @param pageNode The page to search for sub page references
	 * @param query The query that this node belongs to
	 * @param parentPage The parent page of these sub pages
	 */
	private void findSubPages(Document pageNode, final Query query, final FoundPage parentPage) {

		final Set<URL> discoveredSubPages = new HashSet<>();

		for (final Element urlElement: pageNode.select("a[href]")) {

			final String urlString = stripAnchorTags(urlElement.attr("abs:href"));

			if ((urlString == null) || urlString.isEmpty()) {
				// ignore invalid URLs
				continue;
			}

			if (hasInvalidExtensions(urlString)) {
				// URL contains an invalid suffix
				continue;
			}

			URL subPageUrl = null;

			try {
				subPageUrl = new URL(urlString);

			} catch (final MalformedURLException e) {
				// unable to create URL. Log a warning and move on
				log.warn("Unable to create page for url '{}' discovered in page '{}'. Exception: {}", urlString, parentPage.getUrl(), e.getMessage());
				continue;
			}

			// don't search child pages that point back to parent page
			if (parentPage.getUrl().equals(subPageUrl)) {
				continue;
			}

			// don't create the same page twice
			if (discoveredSubPages.contains(subPageUrl)) {
				continue;
			}

			markPageForSearch(subPageUrl, query, parentPage);

			// make sure we don't add the same page twice
			discoveredSubPages.add(subPageUrl);
		}
	}

	/**
	 * Push a new page URL to the search queue if the page has not yet been visited as
	 * part of this page topology.  If the page object exists and has already been added
	 * to the search topology, don't search again.
	 * @param searchUrl The URL to search
	 * @param query Query associated with the URL
	 * @param parentPage parent page of this URL
	 * @throws InterruptedException
	 */
	private void markPageForSearch(final URL searchUrl, final Query query, final FoundPage parentPage)  {

		boolean searchPage = false;
		final FoundPage existingPage = repoService.getPageRepo().findByUrl(searchUrl);

		if (existingPage == null) {
			// page does not yet exist, add it the search queue
			log.debug("Adding newly discovered page URL '{}' of '{}' to be searched", searchUrl, parentPage.getUrl());
			searchPage = true;

		} else if (repoService.getPageTopologyRepo().findByQueryAndPage(query.getId(), existingPage.getId()) == null) {
			// This URL has previously been seen, but not by this search
			log.debug("Adding existing page  URL '{}' of '{}' to be searched", searchUrl, parentPage.getUrl());
			searchPage = true;
		}

		// If the page is a candidate for search, add it to the queue
		if (searchPage) {
			try {
				requestQueue.put(new PageRequestMsg(query, searchUrl, parentPage));

			} catch (final InterruptedException e) {
				// catch the message but don't throw it. Move on.
				log.error("unable to save page request for url '{}'. Exception: {}", searchUrl, e.getMessage());
			}
		}
	}

	/**
	 * Strip URL anchor tags from a URL
	 * @param urlString the URL to strip
	 * @return The stripped URL or the original URL if no tags can be found.
	 */
	private String stripAnchorTags(final String urlString) {

		for (final String boundary: pageBoundaries) {
			if (urlString.contains(boundary)) {
				return urlString.substring(0, urlString.indexOf(boundary));
			}
		}
		// no change to string
		return urlString;
	}

	/**
	 * Return true if the string contains an undesirable extension
	 * @param urlString The string to check
	 * @return True if string has an invalid extension, false if it does not
	 */
	private boolean hasInvalidExtensions(final String urlString) {

		for(final String suffix: invalidExtensions) {
			if (urlString.endsWith(suffix)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Find all sub images for the requested page document
	 * @param pageNode The page document to search for all image references
	 * @param query The query object associated with this search
	 * @param parentPage The parent page of these sub images
	 */
	private void findSubImages(final Document pageNode, final Query query, final FoundPage parentPage) {

		// Determine whether max images have already been discovered for this query
		// FIXME - enable search for sub pages

		// find all image tags in this document
		for (final Element image: pageNode.select("img")) {

			// get image source URL
        	String imageSourceURL = image.attr("src").toLowerCase();

        	if ((imageSourceURL == null) || imageSourceURL.isEmpty()) {
        		log.warn("Could not extract source from image field '{}'", imageSourceURL);
        		continue;
        	}

        	imageSourceURL = stripAnchorTags(imageSourceURL);

        	if (hasInvalidImageExtension(imageSourceURL)) {
        		continue;
        	}

        	// correct URL if it is not properly formed
        	if (!imageSourceURL.startsWith("http")) {

        		String prependURL = parentPage.getUrl().toString();

        		if (!imageSourceURL.startsWith("/")) {
        			prependURL += "/";
        		}

        		// construct the full URL from the parent page
    			imageSourceURL = prependURL + imageSourceURL;
        	}

        	try {
        		// add a search request for the Image URL
				final URL imageURL = new URL(imageSourceURL);
				markImageForSearch(imageURL, query, parentPage);

			} catch (final MalformedURLException e) {
				// unable to create image. Log an error and continue.
				log.error("Unable to construct URL from string '{}'. Exception: '{}'", imageSourceURL, e.getMessage());
				continue;
			}
		}
		// FIXME DELETE

		// now find all stampinup images
		final Set<URL> discoveredImages = new HashSet<>();
		stampinupAlgorithm(pageNode, parentPage.getUrl(), discoveredImages);

		// Search all these URLS
		for (final URL imageURL: discoveredImages) {
			markImageForSearch(imageURL, query, parentPage);
		}

		// FIXME END DELETE
	}

	/**
	 * Add an Image URL to the search engine
	 * @param imageURL the URL to search
	 * @param query The query asking for the search
	 * @param parentPage The page the image belongs to
	 */
	private void markImageForSearch(final URL imageURL, final Query query, final FoundPage parentPage)  {

		// first verify that the URL does not contain any banned phrases
		if (doesNotContainFilteredValues(imageURL)) {
			try {
				requestQueue.put(new ImageRequestMsg(query, imageURL, parentPage));

			} catch (final InterruptedException e) {
				// catch the message but don't throw it. Move on.
				log.error("unable to save image request for url '{}'. Exception: {}", imageURL, e.getMessage());
			}
		}
	}

	/**
	 * Return false if an image contains a string that is not allowed
	 * @param imageURL The image URL to check
	 * @return true if the image passes the filter check, false if it does not.
	 */
	public boolean doesNotContainFilteredValues(final URL imageURL) {

		for (final String filteredValue: filterFileList) {
			// if URL contains a value that should be filtered, return true
			if (imageURL.toString().contains(filteredValue)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Return true if image url ends with a valid extension
	 * @param imageSourceURL the url string to check for valid extensions
	 * @return true if no valid extension found or false if valid extension found
	 */
	private boolean hasInvalidImageExtension(String imageSourceURL) {

		for (final String validExtension: validImageExtensions) {
			if (imageSourceURL.endsWith(validExtension)) {
				return false;
			}
		}
		return true;		// no valid extension found
	}

	/**
	 * Algorithm for finding images on stampin-up pages embedded in Java Script
	 *
	 * @param doc The document representing a page
	 * @param baseUrl the root URL pertaining to the document
	 * @param imageResults the result set to place all discovered images
	 */
	private void stampinupAlgorithm(final Document doc, URL baseUrl, Set<URL> imageResults) {

		// Look for images embedded in Java Script on 'Stampin-Up.com'.  Images URLs are embedded with
		// the following syntax:
		//
		//   	imgList[0] = ["/images/EC/139315S.jpg", "/images/EC/139315G.jpg", "800"];
		//
		// Regular expression that can be used to discover these images:
		// 		'imgList\[[0-9]\] = \["([a-zA-Z0-9\./]+)", "([a-zA-Z0-9\./]+)",'
		//

		// construct a regular expression for discovering images embedded in java script.
		final String imageRegex = "imgList\\[[0-9]\\] = \\[\"([a-zA-Z0-9\\./]+)\", \"([a-zA-Z0-9\\./]+)\",";

		final List<Element> scripts = doc.select("script");	// find all Java Script references in the document

		for (final Element script: scripts) {
			// perform regular expression on java script elements
			final Pattern regexPattern = Pattern.compile(imageRegex);
			final Matcher regexMatch = regexPattern.matcher(script.html());

			while (regexMatch.find()) {
				// construct the matching image URL and add it to the result list
				final String imageUrl1 = baseUrl.toString() + regexMatch.group(1);
				final String imageUrl2 = baseUrl.toString() + regexMatch.group(2);

				log.info("Found stampin-up image " + imageUrl1 + ", " + imageUrl2);

				try {
					imageResults.add(new URL(imageUrl1));
					imageResults.add(new URL(imageUrl2));
				} catch (final MalformedURLException e) {
					log.error("unable to generate url out of either '{}' or '{}'", imageUrl1, imageUrl2);
					continue;
				}
			}
		}
	}
}
