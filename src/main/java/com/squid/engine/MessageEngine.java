package com.squid.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squid.data.Query;
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

    private static final Logger log = LoggerFactory.getLogger(MessageEngine.class);
    private final RepositoryService repoService;
    private final Monitor monitorQueueThread;
    final int MONITOR_TIME_MS = 5000;

	// constructor
	public MessageEngine(String searchName, int threadPoolSize, final RepositoryService repoService) {
		super(searchName, threadPoolSize);
		this.repoService = repoService;
		monitorQueueThread = new Monitor();
		monitorQueueThread.start();
	}

	/**
	 * Private class to monitor status of queue
	 * @author Datim
	 *
	 */
	private class Monitor extends Thread {

		/**
		 * Check the request Queue to see if it is empty.  If it is empty,
		 * stop all queries that have finished page parsing.
		 */
	    public void run() {

	    	while (!Thread.currentThread().isInterrupted()) {

	    		if (requestQueue.isEmpty()) {

		    		// queue is empty. Stop every queue that has finished processing pages
					for(final Query query: repoService.getQueryRepo().findAll()) {
						if(repoService.getQueryStatus().isStopProcessingPages(query)) {
							log.info("Stopping query [id:{}]{}. All images found", query.getId(), query.getUrl());
							repoService.getQueryStatus().setStop(query);
						}
					}
		    	}

		    	try {
					Thread.sleep(MONITOR_TIME_MS);

				} catch (final InterruptedException e) {
					// log exceptions and continue
					log.error("Error interrupting monitor thread. Error: {}", e.getMessage());
				}
	    	}

	    }
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
