package com.squid.parser;

import java.io.IOException;
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

		try {
			// parse the page
			pageNode = Jsoup.connect(page.getUrl().toString()).get();

		} catch (final IOException e) {
			// failed to parse page. Stop all further operations
			log.error("Unable to parse page '{}'. Exception: {}", page.getUrl(), e.getMessage());
			return;
		}

		findSubPages(pageNode, query, page);

	}

	/**
	 * Find all sub pages for a requested page document
	 * @param pageNode The page to search for sub page references
	 * @param query The query that this node belongs to
	 * @param page The parent page of these sub pages
	 */
	private void findSubPages(Document pageNode, final Query query, final Page page) {

		final Elements urlElements = pageNode.select("a[href]");
		final Set<URL> discoveredSubPages = new HashSet<>();

		for (final Element urlElement: urlElements) {

			final String urlString = removeBoundaryTags(urlElement.attr("abs:href"));

			URL subPageUrl = null;

			// FIXME add more pruning checks to page

			try {
				subPageUrl = new URL(urlString);

			} catch (final MalformedURLException e) {
				// unable to create URL. Log a warning and move on
				log.warn("Unable to create page for url '{}' discovered in page '{}'. Exception: {}", urlString, page.getUrl(), e.getMessage());
				continue;
			}

			// don't search child pages that point back to parent page
			if (page.getUrl().equals(subPageUrl)) {
				continue;
			}

			// don't create the same page twice
			if (discoveredSubPages.contains(subPageUrl)) {
				continue;
			}

			addPageToSearch(subPageUrl, query, page);

			// make sure we don't add the same page twice
			discoveredSubPages.add(subPageUrl);
		}
	}

	/**
	 * FIXME Add Comments
	 * @param subPageUrl
	 * @param query
	 * @param page
	 * @throws InterruptedException
	 */
	private void addPageToSearch(final URL subPageUrl, final Query query, final Page page)  {

		// if page does not yet exist, Create a new page request and add it to the page request queue
		if (repoService.getPageRepo().findByUrl(subPageUrl) == null) {
			log.debug("Adding subpage '{}' of '{}' to be searched", subPageUrl, page.getUrl());

			try {
				requestQueue.put(new PageRequestMsg(query, subPageUrl, page));

			} catch (final InterruptedException e) {
				// catch the message but don't throw it. Move on.
				log.error("unable to save page request for url '{}'. Exception: {}", subPageUrl, e.getMessage());
			}
		}
	}

	/**
	 * Strip boundary tags from a URL
	 * @param urlElement
	 */
	private String removeBoundaryTags(String urlString) {

		for (final String boundary: pageBoundaries) {
			if (urlString.contains(boundary)) {
				return urlString.substring(0, urlString.indexOf(boundary));
			}
		}
		return null;
	}

}
