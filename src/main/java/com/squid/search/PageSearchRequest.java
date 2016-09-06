package com.squid.search;

import java.net.URL;

/**
 * Object for defining search page requests
 * @author Datim
 *
 */
public class PageSearchRequest {
	
	private URL url;
	private URL parentUrl;
	private URL rootUrl;
	
	/**
	 * Constructor with url and its base for pages derived from a root search page
	 */
	public PageSearchRequest(final URL url, final URL rootUrl, final URL parentURL) {
		this.url = url;
		this.parentUrl = parentURL;
		this.rootUrl = rootUrl;
	}
	
	/**
	 * Search request for a root search page
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