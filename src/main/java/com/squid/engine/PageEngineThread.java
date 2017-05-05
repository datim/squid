package com.squid.engine;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squid.engine.requests.PageRequestMsg;
import com.squid.engine.requests.RequestMsg;
import com.squid.parser.PageSearchParser;

/**
 * Implement parsing of a page
 * @author Datim
 *
 */
public class PageEngineThread extends EngineThread {

    protected static final Logger log = LoggerFactory.getLogger(PageEngineThread.class);
	private final BlockingQueue<RequestMsg> requestQueue;

	// constructor
	public PageEngineThread(final PageRequestMsg message, final RepositoryService repoService,
						    final BlockingQueue<RequestMsg> requestQueue) {
		super(message, repoService);
		this.requestQueue = requestQueue;
	}

	/**
	 * Define the algorithm for parsing pages
	 */
	@Override
	protected void execute() {
		// parse a page
		try {
			PageSearchParser.parse((PageRequestMsg)requestMessage, requestQueue, repoService);
		} catch (final IOException e) {
			log.error("Unable to parse page {} for query {}. Exception {}", requestMessage.getUrl().toString(), requestMessage.getSearchQuery().getId(), e.getMessage());
		}
	}
}
