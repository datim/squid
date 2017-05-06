package com.squid.parser;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squid.data.Page;
import com.squid.data.Query;
import com.squid.engine.RepositoryService;
import com.squid.engine.requests.PageRequestMsg;
import com.squid.engine.requests.RequestMsg;

/**
 * Reference: https://www.mkyong.com/java/how-to-get-http-response-header-in-java/
 * Parse a page
 * @author Datim
 *
 */
public class SearchPageParser {

    protected static final Logger log = LoggerFactory.getLogger(SearchPageParser.class);
	private final RepositoryService repoService;
	private final BlockingQueue<RequestMsg> requestQueue;
	private static final List<String> pageBoundaries = Arrays.asList("#");

	// constructor
    public SearchPageParser(final BlockingQueue<RequestMsg> requestQueue,
							final RepositoryService repoService) {
    	this.requestQueue = requestQueue;
    	this.repoService = repoService;
    }

	/**
	 * Parse a page from a page message
	 * @param requestMessage
	 * @param requestQueue
	 * @param repoService
	 * @throws IOException
	 */
	public void executeMsg(final PageRequestMsg requestMessage) throws IOException {

		final URL pageUrl = requestMessage.getUrl();

		// check if this page has already been searched
		Page page = repoService.getPageRepo().findByUrl(pageUrl);

		if (page != null) {
			// this page has a record
			// FIXME this is where we add re-parsing of a page
			log.debug("Page {} previously visited. Not parsing", pageUrl);
			return;

		} else {
			// this is a new page. Attempt to parse it
			page = createNewPage(pageUrl);

			// if a page record was unable to be created, an error was already logged. Just exit
			if (page == null) {
				return;
			}
		}

		if (page != null) {

			// FIXME TODO create a page record for this query
			final Query query = requestMessage.getSearchQuery();
			parsePage(page);
		}
	}

	/**
	 * Create a new page record
	 * @param pageUrl
	 */
	private Page createNewPage(final URL pageUrl) {

		Page pageToParse = null;

		try {
			// Create a new record for the page
			pageToParse = repoService.getPageRepo().save(new Page(pageUrl));

		} catch (final org.hibernate.exception.ConstraintViolationException | org.springframework.dao.DataIntegrityViolationException e) {
			// create a new Page record.  Record already created, fail
			log.warn("Failed to create a new page record for url '{}'. Record already exists. Exception: {}", pageUrl, e.getMessage());
			return null;
		}

		return pageToParse;
	}

	/**
	 *
	 * @param page
	 */
	private void parsePage(final Page page) {

		Document pageNode = null;

		try {
			// parse the page
			pageNode = Jsoup.connect(page.getUrl().toString()).get();

		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final List<URL> subPages = findSubPages(pageNode);
	}

	private List<URL> findSubPages(final Document pageNode) {

		return null;
	}

	private List<URL> findImageUrls(final Document pageNode) {

		return null;
	}
}
