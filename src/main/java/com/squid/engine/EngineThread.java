package com.squid.engine;

import com.squid.engine.requests.RequestMsg;

/**
 * Abstract class that defines the template for parsing threads
 * @author Datim
 *
 */
public abstract class EngineThread implements Runnable {

	protected final RequestMsg requestMessage;
	protected final RepositoryService repoService;

	// constructor
	protected EngineThread(final RequestMsg msg, final RepositoryService repoService) {
		requestMessage = msg;
		this.repoService = repoService;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		execute();
	}

	/**
	 * Implementation of execution
	 */
	protected abstract void execute();
}
