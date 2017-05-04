package com.squid.engine;

import java.net.URL;

import com.squid.data.Page;
import com.squid.data.Query;

/**
 * Image request message for search queue
 * @author Datim
 *
 */
public class ImageRequest {

	private final Query searchQuery;
	private final URL imageUrl;
	private final Page hostPage;

	// constructor
	public ImageRequest(final Query searchQuery, final URL imageUrl, final Page hostPage) {
		this.searchQuery = searchQuery;
		this.imageUrl = imageUrl;
		this.hostPage = hostPage;
	}

	public Query getSearchQuery() {
		return searchQuery;
	}

	public URL getImageUrl() {
		return imageUrl;
	}

	public Page getHostPage() {
		return hostPage;
	}
}
