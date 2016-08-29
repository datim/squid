package com.squid.search;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import java.util.logging.Logger;

import com.squid.data.NodeDataRepository;
import com.squid.data.PhotoDataRepository;
import com.squid.data.SearchStatusRepository;

/**
 * Execute searches using a thread pool.
 * Responsible for handling search requests from a queue.  Delgate work traverse 
 * @author Datim
 *
 */
public class SearchExecutor extends Thread {
	
	static Logger log = Logger.getLogger(SearchExecutor.class.getName());

	static final int THREAD_POOL_SIZE = 1;
	
	private long maxNodes;
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
		this.maxNodes = maxNodes;
		this.pageRequestsQueue = new LinkedBlockingQueue<>();
		
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
				
				log.info("Size of search queue is " + pageRequestsQueue.size());
				
				ParseNodeThread searchTask = new ParseNodeThread(request.getUrl(), this.photoRepo, this.nodeRepo, 
																 this.searchStatusRepo, maxImages, maxNodes);
				
				executor.execute(searchTask);
				
				/*
				 * TODO: Refactor this
				// push request onto a new processing thread
				SearchNodes searchTask = new SearchNodes(request.url, request.parentUrl, request.rootUrl, photoRepo, nodeRepo, 
														 searchStatusRepo, maxImages, maxNodes, pageRequestsQueue);
				*/
			} catch (InterruptedException e) {
				// interrupt exception occurred.  Quit requests
				log.severe("Exception occured blocking on thread queue: " + e);
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
