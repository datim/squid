package com.squid.search;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.squid.data.NodeDataRepository;
import com.squid.data.PhotoDataRepository;
import com.squid.data.SearchStatusRepository;

// TODO: Externalize thread count THREAD_POOL_SIZE

/**
 * Poll for new search requests from an incoming request queue. Delegate requests to a thread pool.
 * @author Datim
 */
public class SearchExecutor extends Thread {
	
	static Logger log = Logger.getLogger(SearchExecutor.class.getName());

	static final int THREAD_POOL_SIZE = 5;
	
	private long maxPages;
	private long maxImages;
	private PhotoDataRepository photoRepo;
	private NodeDataRepository nodeRepo;
	private SearchStatusRepository searchStatusRepo;
	private BlockingQueue<PageSearchRequest> pageRequestsQueue = null;
	ThreadPoolExecutor executor;

	/**
	 * Constructor
	 */
	public SearchExecutor(final PhotoDataRepository photoRepoIn, 
						  final NodeDataRepository nodeRepoIn, 
						  final SearchStatusRepository inSearchRepo, 
						  long maxImages, long maxNodes) {
		
		this.photoRepo = photoRepoIn;
		this.nodeRepo = nodeRepoIn;
		this.searchStatusRepo = inSearchRepo;
		this.maxImages = maxImages;
		this.maxPages = maxNodes;
		
		// create the queue for all page requests
		this.pageRequestsQueue = new LinkedBlockingQueue<>();
		
		// create new thread pool
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_POOL_SIZE);
	}
	
	/**
	 * Execute in a thread
	 */
	@Override
	public void run() {
		monitorPageRequests();
	}
	
	/**
	 * Simple thread to check for new requests.  Block on thread queue until a 
	 * a new request has been added.  Delegate request to new parse thread.
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
				
				if (photoRepo.count() >= maxImages || nodeRepo.count() >= maxPages) {
					
					// reached the search limit. Remove all remaining items to be processed. Do not process any more
					log.info("Maximum threshold for images or pages reached. Clearing queue");
					pageRequestsQueue.clear();
					continue;
				}					
				
				log.info("New request for url " + request.getUrl().toString() + ". Queue size is " + pageRequestsQueue.size());
				
				final PageParser searchTask = new PageParser(request.getUrl(), this.photoRepo, this.nodeRepo, 
													         this.searchStatusRepo, maxImages, maxPages, pageRequestsQueue);
				
				executor.execute(searchTask);
								
			} catch (InterruptedException e) {
				// interrupt exception occurred.  Quit requests
				log.severe("The thread was interrupted. Quiting all future searches. " + e);
				break;
			}
		}
		
		try {
			// shutdown the thread pool.  This should never be reached unless there was an interruption exception
			executor.shutdown();
			executor.awaitTermination(5, TimeUnit.SECONDS);
			
		} catch (InterruptedException e) {
			log.severe("Unable to shutdown thread executor pool: " + e);
		}
	}
	
	public BlockingQueue<PageSearchRequest> getPageRequestsQueue() {
		return pageRequestsQueue;
	}

	public void setPageRequestsQueue(BlockingQueue<PageSearchRequest> pageRequestsQueue) {
		this.pageRequestsQueue = pageRequestsQueue;
	}
}
