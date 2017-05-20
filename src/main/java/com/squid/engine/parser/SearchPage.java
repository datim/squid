package com.squid.engine.parser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squid.data.Page;
import com.squid.data.PageTopology;
import com.squid.data.Query;
import com.squid.engine.requests.PageRequestMsg;
import com.squid.engine.requests.RequestMsg;
import com.squid.service.RepositoryService;

/**
 * Handle the page management of searching for a page.  Call the page parser if page
 * needs to be parsed
 * @author Datim
 *
 */
public class SearchPage {

    protected static final Logger log = LoggerFactory.getLogger(SearchPage.class);
	private final RepositoryService repoService;
	private final PageParser parser;

	// constructor
    public SearchPage(final BlockingQueue<RequestMsg> requestQueue,
							final RepositoryService repoService) {
    	this.repoService = repoService;
    	parser = new PageParser(repoService, requestQueue);
    }

	/**
	 * Parse a page based on a page request
	 * @param requestMessage The message containing the page to process
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public void executeMsg(final PageRequestMsg requestMessage) throws IOException {

		final Query query = requestMessage.getSearchQuery();

		// check whether the upper bounds of the search page has been reached
		if (canSearchContinue(query) == false) {
			log.info("Maximum number of pages discovred for query '{}'. Stopping search", query);
			return;
		}

		final URL pageUrl = requestMessage.getUrl();
		String checksum = null;

		try {
			// calculate the checksum for the page
			checksum = SearchHelperSingleton.getInstance().calcPageChecksum(pageUrl);

		} catch (final IOException e) {
			log.error("Unable to calculate checksum for page '{}'. Exception: {}", pageUrl, e.getMessage());
			throw e;
		}

		// check if this page has already been searched
		Page page = repoService.getPageRepo().findByUrl(pageUrl);

		if (page == null) {
			// this is a new page. create record
			page = createNewPage(pageUrl, checksum);

			// if a page record was unable to be created, an error was already logged. Just exit
			if (page == null) {
				log.debug("Unable to create page record for url '{}'. Record exists.  Not searching", pageUrl);
				return;
			}

			// Add the page to the page topology map
			setPageTopology(query, page, requestMessage.getParentPage());

		} else {
			// Existing page. check whether this page has been visited before in this query
			final PageTopology pageMapping = repoService.getPageTopologyRepo().findByQueryAndPage(
					query.getId(), page.getId());

			if (pageMapping != null) {
				log.debug("Page '{}' has already been visited in this query", pageUrl);
				return;
			}

			// set the page topology
			setPageTopology(query, page, requestMessage.getParentPage());

			// Check whether page has been updated since the last time it was visited
			if (checksum.equals(page.getChecksum())) {

				// FIXME TODO - Instead of just skipping this page, add all child pages and images to
				// the search queue and don't parse this page.
				return;
			}
		}

		// search for the page
		parser.parsePage(page, query);
	}

	/**
	 * Check whether query has reached the maximum number of pages, or whether the query
	 * status has been changed to stopped
	 * @param searchQuery
	 * @return true if search status page threshold has been reached, false if search should be stopped
	 */
	private boolean canSearchContinue(final Query searchQuery) {

		boolean continueSearch = false;

		if (repoService.getQueryStatus().isRunning(searchQuery)) {

			// check whether maximum pages have been visited
			final long pageCount = repoService.getPageTopologyRepo().findByQuery(searchQuery.getId()).size();

			// if the maximum number of pages have been visited, then mark the query as stopped and finish
			if ( pageCount < searchQuery.getMaxPages()) {
				continueSearch = true; // search can continue, all checks pass

			} else {
				// maximum search pages have been visted. Mark the query as done
				repoService.getQueryStatus().setStop(searchQuery);
				log.debug("Maximum number of pages discovered for query {}. Will not continue", searchQuery.getId());
			}
		} else {
			log.debug("Query status is '{}'. Will not continue search for this thread", repoService.getQueryStatus());
		}

		return continueSearch;
	}

	/**
	 * Create a new topology mapping for a page against a query. Create it if it does not yet exist.
	 * already exists, just return it.
	 * @param query The query to map to a topology tree
	 * @param page The page to map to a topology tree.
	 * @return An existing page topology mapping, or a new one if it has not yet been created
	 */
	private PageTopology setPageTopology(final Query query, final Page page, final Page parentPage) {

		PageTopology pageMapping = repoService.getPageTopologyRepo().findByQueryAndPage(query.getId(), page.getId());

		if (pageMapping == null) {
			// topology record does not exist. Create it.
			log.debug("Creating a new page topology for page '{}' to query '{}'", page.getUrl(), query.getUrl());

			// create a new topology record and save it
			pageMapping = new PageTopology(query.getId(), page.getId(), (parentPage == null) ? page.getId() : parentPage.getId());
			pageMapping = repoService.getPageTopologyRepo().save(pageMapping);

			log.debug("Topology created for page '{}' with id {}", page.getUrl(), pageMapping.getId());
		}

		return pageMapping;
	}

	/**
	 * Create a new page record. If record cannot successfully be created, return null.
	 * @param pageUrl The URL to create a page record from
	 * @param checksum The hash checksum of the URL of the page
	 * @return A new page record, or null if record cannot be created
	 */
	private Page createNewPage(final URL pageUrl, final String checksum) {

		Page newPage = null;

		try {
			// Create a new record for the page
			newPage = repoService.getPageRepo().save(new Page(pageUrl, checksum));
			log.debug("New page created for '{}' with id {}", pageUrl, newPage.getId());

		} catch (final org.hibernate.exception.ConstraintViolationException | org.springframework.dao.DataIntegrityViolationException e) {
			// create a new Page record.  Record already created, fail
			log.warn("Failed to create a new page record for url '{}'. Record already exists. Exception: '{}'", pageUrl, e.getMessage());
			return null;
		}

		return newPage;
	}
}
