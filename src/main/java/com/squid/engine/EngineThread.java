package com.squid.engine;

import com.squid.engine.requests.RequestMsg;

/**
 * Abstract class that defines the template for parsing threads
 * @author Datim
 *
 */
public abstract class EngineThread implements Runnable {

	protected final RequestMsg requestMessage;

	// constructor
	protected EngineThread(final RequestMsg msg) {
		requestMessage = msg;
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
