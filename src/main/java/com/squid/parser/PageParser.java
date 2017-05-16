package com.squid.parser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squid.data.Page;
import com.squid.data.Query;
import com.squid.engine.RepositoryService;
import com.squid.engine.requests.PageRequestMsg;
import com.squid.engine.requests.RequestMsg;

/**
 * Logic for parsing a web page
 * @author Datim
 *
 */
public class PageParser {

    protected static final Logger log = LoggerFactory.getLogger(PageParser.class);
	private final BlockingQueue<RequestMsg> requestQueue;
	private final RepositoryService repoService;

	private static final List<String> pageBoundaries = Arrays.asList("#");
	private static final List<String> invalidExtensions = Arrays.asList("css", "pdf");

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
	public void parsePage(final Page page, final Query query) {

		Document pageNode = null;

		// render the page before parsing it
		// FIXME add time outs
		final HtmlUnitDriver driver = new HtmlUnitDriver();
		driver.get(page.getUrl().toString());

		// parse the page
		pageNode = Jsoup.parse(driver.getPageSource());
		//pageNode = Jsoup.connect(page.getUrl().toString()).get();

		findSubPages(pageNode, query, page);
		findSubImages(pageNode, query, page);
	}


	/**
	 * Find all sub pages for a requested page document
	 * @param pageNode The page to search for sub page references
	 * @param query The query that this node belongs to
	 * @param parentPage The parent page of these sub pages
	 */
	private void findSubPages(Document pageNode, final Query query, final Page parentPage) {

		final Elements urlElements = pageNode.select("a[href]");
		final Set<URL> discoveredSubPages = new HashSet<>();

		for (final Element urlElement: urlElements) {

			final String urlString = stripAnchorTags(urlElement.attr("abs:href"));

			if ((urlString == null) || urlString.isEmpty()) {
				// ignore invalid URLs
				continue;
			}

			if (containsInvalidExtension(urlString)) {
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
	private void markPageForSearch(final URL searchUrl, final Query query, final Page parentPage)  {

		boolean searchPage = false;
		final Page existingPage = repoService.getPageRepo().findByUrl(searchUrl);

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
	private boolean containsInvalidExtension(final String urlString) {

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
	private void findSubImages(final Document pageNode, final Query query, final Page parentPage) {

		// find all image tags in this document
		final Elements images = pageNode.select("img");

	}
}
