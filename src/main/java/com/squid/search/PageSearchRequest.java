package com.squid.search;

import java.net.URL;

/**
 * A request object containing information about a new search page to search
 * @author Datim
 *
 */
public class PageSearchRequest {
	
	URL url;
	URL parentUrl;
	URL rootUrl;
	
	/**
	 * Constructor with url and its base
	 */
	public PageSearchRequest(final URL url, final URL rootUrl, final URL parentURL) {
		this.url = url;
		this.parentUrl = parentURL;
		this.rootUrl = rootUrl;
	}
	
	/**
	 * Constructor with just the root url
	 * @return
	 */
	public PageSearchRequest(final URL url) {
		this(url, url, null);
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