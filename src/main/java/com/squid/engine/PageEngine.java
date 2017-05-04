package com.squid.engine;

import com.squid.data.PageRepository;
import com.squid.engine.requests.PageRequestMsg;
import com.squid.engine.requests.RequestMsg;

/**
 * Engine implementation for searching pages
 * @author Datim
 *
 */
public class PageEngine extends EngineBase {

    private final PageRepository pageRepo;

	// constructor
	public PageEngine(String searchName, int threadPoolSize, final PageRepository pageRepo) {
		super(searchName, threadPoolSize);
		this.pageRepo = pageRepo;
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
	protected Runnable generateRequest(final RequestMsg requestMessage) {
		final PageRequestMsg pageMsg = (PageRequestMsg) requestMessage;
		return new PageEngineThread(pageRepo, pageMsg);
	}
}
