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
import com.squid.data.PageTopology;
import com.squid.data.Query;
import com.squid.engine.RepositoryService;
import com.squid.engine.requests.PageRequestMsg;
import com.squid.engine.requests.RequestMsg;

/**
 * Reference: https://www.mkyong.com/java/how-to-get-http-response-header-in-java/
 * Handle the search of a page
 * @author Datim
 *
 */
public class SearchPage {

    protected static final Logger log = LoggerFactory.getLogger(SearchPage.class);
	private final RepositoryService repoService;
	private final BlockingQueue<RequestMsg> requestQueue;
	private static final List<String> pageBoundaries = Arrays.asList("#");

	// constructor
    public SearchPage(final BlockingQueue<RequestMsg> requestQueue,
							final RepositoryService repoService) {
    	this.requestQueue = requestQueue;
    	this.repoService = repoService;
    }

	/**
	 * Parse a page based on a page request
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
			// FIXME check if page has changed before re-parsing
			log.debug("Page {} previously visited. Not parsing", pageUrl);

		} else {
			// this is a new page. Attempt to parse it
			page = createNewPage(pageUrl);

			// if a page record was unable to be created, an error was already logged. Just exit
			if (page == null) {
				log.debug("Unable to create page record for url '{}'. Record exists.  Not searching", requestMessage.getUrl());
				return;
			}
		}

		// Add page to topology if it has not done so yet
		final PageTopology topology = setPageTopology(requestMessage.getSearchQuery(), page, requestMessage.getParentPage());

		// search for the page
		parsePage(page);
	}

	/**
	 * Create a new topology mapping for a page against a query. Create it if it does not yet exist.
	 * already exists, just return it.
	 * @param query The query to map to a topology tree
	 * @param page The page to map to a toplogy tree.
	 * @return An existing page topology mapping, or a new one if it has not yet been created
	 */
	private PageTopology setPageTopology(final Query query, final Page page, final Page parentPage) {

		PageTopology pageMapping = repoService.getPageTopologyRepo().findByQueryAndPage(query.getId(), page.getId());

		if (pageMapping == null) {
			// topology record does not exist. Create it.
			log.debug("Creating a new page topology for page '{}' to query '{}'", page.getUrl(), query.getUrl());
			if (parentPage != null) {
				pageMapping = new PageTopology(query.getId(), page.getId(), parentPage.getId());
			} else {
				pageMapping = new PageTopology(query.getId(), page.getId());
			}

			// save record
			pageMapping = repoService.getPageTopologyRepo().save(pageMapping);


			log.debug("Topology created with id {}", pageMapping.getId());
		}

		return pageMapping;
	}

	/**
	 * Create a new page record. If record cannot successfully be created, return null.
	 * @param pageUrl The url to create a page record from
	 * @return A new page record, or null if record cannot be created
	 */
	private Page createNewPage(final URL pageUrl) {

		Page pageToParse = null;

		try {
			// Create a new record for the page
			pageToParse = repoService.getPageRepo().save(new Page(pageUrl));

		} catch (final org.hibernate.exception.ConstraintViolationException | org.springframework.dao.DataIntegrityViolationException e) {
			// create a new Page record.  Record already created, fail
			log.info("Failed to create a new page record for url '{}'. Record already exists. Exception: '{}'", pageUrl, e.getMessage());
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
