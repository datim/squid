package com.squid.engine.requests;

import java.net.URL;

import com.squid.data.FoundPage;
import com.squid.data.Query;

/**
 * Image request message for search queue
 * @author Datim
 *
 */
public class ImageRequestMsg extends RequestMsg {

	// constructor
	public ImageRequestMsg(final Query searchQuery, final URL imageUrl, final FoundPage hostPage) {
		super(searchQuery, imageUrl, hostPage);
	}

	// Host page that contains this image
	public FoundPage getHostPage() {
		return page;
	}
}
