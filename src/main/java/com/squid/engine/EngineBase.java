package com.squid.engine;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squid.engine.requests.RequestMsg;

/**
 * Abstract class for both Page and Image Search implementations. Implements a thread pool s
 * @author Datim
 *
 */
public abstract class EngineBase extends Thread {

    private static final Logger log = LoggerFactory.getLogger(EngineBase.class);

	protected BlockingQueue<RequestMsg> requestQueue;
	private final ThreadPoolExecutor executor;
	protected final String searchName;

	/**
	 * Constructor
	 *
	 * @param searchName The name of the search activity implementing this class
	 * @param threadPoolSize The desired thread pool size
	 */
	protected EngineBase(String searchName, int threadPoolSize) {

		this.searchName = searchName;
		executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		executor.setMaximumPoolSize(threadPoolSize);
	}

	/**
	 * Main thread loop. Monitor queue for new events
	 */
	@Override
	public void run() {
		monitorRequests();
	}

	/**
	 * Monitor the requestQueue for new request events
	 */
	private void monitorRequests() {

		// loop until we're interrupted
		while(!Thread.currentThread().isInterrupted()) {

			// block until we get a message
			try {

				// block until we get a message, then hand it to a thread pool
				final RequestMsg request = requestQueue.take();
				executor.execute(generateRequest(request));

				// generate runnable search for this message

			} catch (final InterruptedException e) {
				// unable to fetch message
				log.error("Unable to fetch message from {} queue. Exception: {}", searchName, e.getMessage());
			}
		}

		// Thread interrupted. Shut down pool
		executor.shutdown();
	}

	/**
	 * Handle requests. Will be implemented by implementing classes
	 * @param requestMessage The message to process
	 * @return
	 */
	protected abstract Runnable generateRequest(final RequestMsg requestMessage);

	/**
	 * Push a new request on the queue
	 * @param requestMessage
	 * @throws InterruptedException
	 */
	public void addRequest(final RequestMsg requestMessage) throws InterruptedException {
		requestQueue.put(requestMessage);
	}
}
