package com.squid.engine.requests;

import java.net.URL;

import com.squid.data.Page;
import com.squid.data.Query;

/**
 * Base class for page and image requests
 * @author Datim
 *
 */
public abstract class RequestMsg {

	protected final Query searchQuery;
	protected final URL url;
	protected final Page page;

	// constructor
	protected RequestMsg(final Query searchQuery, final URL url, final Page page) {
		this.searchQuery = searchQuery;
		this.url = url;
		this.page = page;
	}

	public Query getSearchQuery() {
		return searchQuery;
	}

	public URL getUrl() {
		return url;
	}

	public Page getPage() {
		return page;
	}
}
