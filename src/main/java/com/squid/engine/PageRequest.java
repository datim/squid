package com.squid.engine;

import java.net.URL;

import com.squid.data.Page;
import com.squid.data.Query;

/**
 * Page request message for search queue
 * @author Datim
 *
 */
public class PageRequest {

	private final Query searchQuery;
	private final URL url;
	private final Page parentPage;

	// constructor
	public PageRequest(final Query searchQuery, final URL url, final Page parentPage) {
		this.searchQuery = searchQuery;
		this.url = url;
		this.parentPage = parentPage;
	}

	// constructor, no parent page
	public PageRequest(final Query searchQuery, final URL url) {
		this(searchQuery, url, null);
	}

	public Query getSearchQuery() {
		return searchQuery;
	}

	public URL getUrl() {
		return url;
	}

	public Page getParentPage() {
		return parentPage;
	}
}
