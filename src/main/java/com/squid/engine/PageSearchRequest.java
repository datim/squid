package com.squid.engine;

import java.net.URL;

/**
 * Class representing a new page to search
 * @author Datim
 *
 */
public class PageSearchRequest {
	
	URL url;
	URL parentUrl;
	URL rootUrl;
	
	/**
	 * Constructor
	 */
	public PageSearchRequest(final URL url, final URL rootUrl, final URL parentURL) {
		this.url = url;
		this.parentUrl = parentURL;
		this.rootUrl = rootUrl;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public URL getParentUrl() {
		return parentUrl;
	}

	public void setParentUrl(URL parentUrl) {
		this.parentUrl = parentUrl;
	}

	public URL getRootUrl() {
		return rootUrl;
	}

	public void setRootUrl(URL rootUrl) {
		this.rootUrl = rootUrl;
	}
}
