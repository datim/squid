package com.squid.engine.requests;

import java.net.URL;

import com.squid.data.Page;
import com.squid.data.Query;

/**
 * Image request message for search queue
 * @author Datim
 *
 */
public class ImageRequest extends RequestMsg {

	// constructor
	public ImageRequest(final Query searchQuery, final URL imageUrl, final Page hostPage) {
		super(searchQuery, imageUrl, hostPage);
	}

	// Host page that contains this image
	public Page getHostPage() {
		return page;
	}
}
