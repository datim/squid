package com.squid.controller.rest;

import java.net.URL;

/**
 * DTO for Query objects
 * @author Datim
 *
 */
public class QueryDTO {
	private long id;
	private String name;
	private URL url;
	private int maxPages;
	private int maxImages;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public URL getUrl() {
		return url;
	}
	public void setUrl(URL url) {
		this.url = url;
	}
	public int getMaxPages() {
		return maxPages;
	}
	public void setMaxPages(int maxPages) {
		this.maxPages = maxPages;
	}
	public int getMaxImages() {
		return maxImages;
	}
	public void setMaxImages(int maxImages) {
		this.maxImages = maxImages;
	}
}
