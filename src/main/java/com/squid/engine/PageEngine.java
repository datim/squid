package com.squid.engine;

import com.squid.engine.requests.PageRequestMsg;
import com.squid.engine.requests.RequestMsg;

/**
 * Engine implementation for searching pages
 * @author Datim
 *
 */
public class PageEngine extends EngineBase {

    private final RepositoryService repoService;


	// constructor
	public PageEngine(String searchName, int threadPoolSize, final RepositoryService repoService) {
		super(searchName, threadPoolSize);
		this.repoService = repoService;
	}

	/**
	 * Add a page request
	 * @param request
	 * @throws InterruptedException
	 */
	public void addRequest(final PageRequestMsg request) throws InterruptedException {
		super.addRequest(request);
	}

	/**
	 * Create a runnable object to parse a page
	 * @param requestMessage The request message to process
	 */
	@Override
	protected Runnable getMessageHandler(final RequestMsg requestMessage) {
		final PageRequestMsg pageMsg = (PageRequestMsg) requestMessage;

		if (requestMessage instanceof PageRequestMsg) {
			// handle page request
			return new PageEngineThread(pageMsg, repoService, requestQueue);

		} else {
			log.error("Unexpected request message of type {}", requestMessage.getClass().getName());
			return null;
		}
	}
}
