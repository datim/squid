package com.squid.parser;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.squid.data.Page;
import com.squid.engine.RepositoryService;
import com.squid.engine.requests.PageRequestMsg;
import com.squid.engine.requests.RequestMsg;

/**
 * Reference: https://www.mkyong.com/java/how-to-get-http-response-header-in-java/
 * Parse a page
 * @author Datim
 *
 */
public class PageSearchParser {

	/**
	 * Parse a page
	 * @param requestMessage
	 * @param requestQueue
	 * @param repoService
	 * @throws IOException
	 */
	public static void parse(final PageRequestMsg requestMessage,
							 final BlockingQueue<RequestMsg> requestQueue,
							 final RepositoryService repoService) throws IOException {

		final URL pageUrl = requestMessage.getUrl();

		// get the latest etag for the page
		final String etag = getEtag(pageUrl);

		// check if this page has already been searched
		final Page page = repoService.getPageRepo().findByUrl(pageUrl);

	}

	/**
	 * Fetch an existing page object. May return null if page doesn't exist
	 * @param url
	 * @param service
	 * @return
	 * @throws IOException
	 */
	private static String getEtag(final URL url) throws IOException {

		final URLConnection conn = url.openConnection();
		 Map<String, List<String>> etag = conn.getRequestProperties();

		final long z = conn.getLastModified();

		etag = conn.getHeaderFields();


		return "";
	}


}
