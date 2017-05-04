package com.squid.engine.requests;

import java.net.URL;

import com.squid.data.Page;
import com.squid.data.Query;

/**
 * Page request message for search queue
 * @author Datim
 *
 */
public class PageRequestMsg extends RequestMsg {

	// constructor
	public PageRequestMsg(final Query searchQuery, final URL url, final Page parentPage) {
		super(searchQuery, url, parentPage);
	}

	// constructor, no parent page
	public PageRequestMsg(final Query searchQuery, final URL url) {
		this(searchQuery, url, null);
	}

	public Page getParentPage() {
		return page;
	}
}
