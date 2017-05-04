package com.squid.engine.old;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squid.data.old.NodeDataRepository;
import com.squid.data.old.PhotoDataRepository;
import com.squid.data.old.SearchStatusRepository;

// main thread to constantly pull work off of a thread
/**
 * Single thread to pull page search requests off a of a thread safe queue
 * and delegate them to threads to crawl.
 */
public class SearchExecutor extends Thread {

    private static final Logger log = LoggerFactory.getLogger(SearchExecutor.class);

	static final int THREAD_POOL_SIZE = 10;

	private final long maxNodes;
	private final long maxImages;
	private final PhotoDataRepository photoRepo;
	private final NodeDataRepository nodeRepo;
	private final SearchStatusRepository searchStatusRepo;
	private BlockingQueue<PageSearchRequest> pageRequestsQueue = null;
	ThreadPoolExecutor executor;

	/**
	 * Constructor
	 */
	public SearchExecutor(final PhotoDataRepository photoRepoIn, final NodeDataRepository nodeRepoIn,
			   			  final SearchStatusRepository inSearchRepo, long maxImages, long maxNodes) {
		photoRepo = photoRepoIn;
		nodeRepo = nodeRepoIn;
		searchStatusRepo = inSearchRepo;
		this.maxImages = maxImages;
		this.maxNodes = maxNodes;
		pageRequestsQueue = new LinkedBlockingQueue<>();

		// create new thread pool
		executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		executor.setMaximumPoolSize(THREAD_POOL_SIZE);
	}

	/**
	 * Execute in a thread
	 */
	@Override
	public void run() {
		monitorPageRequests();
	}

	/**
	 * Thread based method to continually check for new page requests
	 * @throws InterruptedException
	 */
	private void monitorPageRequests()  {

		// looping forever is not usually good practice.  In this case,
		// this thread should never go away.  It blocks until
		// a new page request is available
		while(true) {

			try {
				// block waiting for page requests
				final PageSearchRequest request = pageRequestsQueue.take();

				log.info("Size of search queue is " + pageRequestsQueue.size());

				// push request onto a new processing thread
				final SearchNodes searchTask = new SearchNodes(request.url, request.parentUrl, request.rootUrl, photoRepo, nodeRepo,
														 searchStatusRepo, maxImages, maxNodes, pageRequestsQueue);
				executor.execute(searchTask);

			} catch (final InterruptedException e) {
				// interrupt exception occurred.  Quit requests
				log.error("Exception occured blocking on thread queue: {}", e);
				break;
			}
		}

		// we're done. Shutdown thread pool
		executor.shutdown();
	}

	public BlockingQueue<PageSearchRequest> getPageRequestsQueue() {
		return pageRequestsQueue;
	}

	public void setPageRequestsQueue(BlockingQueue<PageSearchRequest> pageRequestsQueue) {
		this.pageRequestsQueue = pageRequestsQueue;
	}
}
