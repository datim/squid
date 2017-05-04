package com.squid.engine;

import com.squid.data.PageRepository;
import com.squid.engine.requests.PageRequestMsg;

/**
 * Implement parsing of a page
 * @author Datim
 *
 */
public class PageEngineThread extends EngineThread {

	private final PageRepository pageRepo;

	// constructor
	public PageEngineThread(PageRepository pageRepo, final PageRequestMsg message) {
		super(message);
		this.pageRepo = pageRepo;
	}

	/**
	 * Define the algorithm for parsing pages
	 */
	@Override
	protected void execute() {
		// TODO Auto-generated method stub
	}
}
