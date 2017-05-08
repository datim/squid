package com.squid.parser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.codec.digest.DigestUtils;
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
	 * @throws URISyntaxException
	 */
	public void executeMsg(final PageRequestMsg requestMessage) throws IOException {

		final URL pageUrl = requestMessage.getUrl();
		final String checksum = calcPageChecksum(pageUrl);

		// check if this page has already been searched
		Page page = repoService.getPageRepo().findByUrl(pageUrl);

		if (page != null) {
			// Page already seen. If the checksum matches, do not continue to parse
			if (checksum.equals(page.getChecksum())) {
				return;
			}

		} else {
			// this is a new page. Attempt to parse it
			page = createNewPage(pageUrl, checksum);

			// if a page record was unable to be created, an error was already logged. Just exit
			if (page == null) {
				log.debug("Unable to create page record for url '{}'. Record exists.  Not searching", requestMessage.getUrl());
				return;
			}
		}

		// Add page to topology if it has not done so yet
		setPageTopology(requestMessage.getSearchQuery(), page, requestMessage.getParentPage());

		// search for the page
		parsePage(page);
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

	/**
	 *  Calculate the checksum for a URL
	 *
	 * Reference: http://stackoverflow.com/questions/6881029/how-to-check-whether-a-website-has-been-updated-and-send-a-email
	 * @param pageUrl the URL to calculate checksum for
	 * @return The calculated checksum for a page
	 * @throws IOException
	 */
	private String calcPageChecksum(final URL pageUrl) throws IOException  {

		String checksum = null;

		try {
			final HttpURLConnection pageConnection = (HttpURLConnection) pageUrl.openConnection();
			pageConnection.setRequestMethod("GET");
			pageConnection.setDoOutput(true);
		    pageConnection.connect();

	        // Use MD5 because where just hashing a string and its faster, no security here
		    checksum = DigestUtils.md5Hex(pageConnection.getInputStream());

		} catch (final IOException e) {
			log.error("Unable to calculate checksum for page '{}'. Exception: {}", pageUrl, e.getMessage());
			throw e;
		}

        return checksum;
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
