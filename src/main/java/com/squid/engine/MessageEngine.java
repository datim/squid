package com.squid.engine;

import com.squid.engine.requests.ImageRequestMsg;
import com.squid.engine.requests.PageRequestMsg;
import com.squid.engine.requests.RequestMsg;
import com.squid.service.RepositoryService;

/**
 * Engine implementation for searching pages and images
 * @author Datim
 *
 */
public class MessageEngine extends EngineBase {

    private final RepositoryService repoService;

	// constructor
	public MessageEngine(String searchName, int threadPoolSize, final RepositoryService repoService) {
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
	 * Create a runnable object to parse a page or image
	 * @param requestMessage The request message to process
	 * @return the executable handler to process the message
	 */
	@Override
	protected Runnable getMessageHandler(final RequestMsg requestMessage) {

		if (requestMessage instanceof PageRequestMsg) {
			// handle page request
			return new PageEngineThread((PageRequestMsg) requestMessage, repoService, requestQueue);

		} else if (requestMessage instanceof ImageRequestMsg) {
			// handle image request
			return new ImageEngineThread((ImageRequestMsg) requestMessage, repoService);

		} else {
			log.error("Unexpected request message of type {}", requestMessage.getClass().getName());
			return null;
		}
	}
}
