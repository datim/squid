package com.squid.engine.requests;

import java.net.URL;

import com.squid.data.Page;
import com.squid.data.Query;

/**
 * Image request message for search queue
 * @author Datim
 *
 */
public class ImageRequestMsg extends RequestMsg {

	// constructor
	public ImageRequestMsg(final Query searchQuery, final URL imageUrl, final Page hostPage) {
		super(searchQuery, imageUrl, hostPage);
	}

	// Host page that contains this image
	public Page getHostPage() {
		return page;
	}
}
