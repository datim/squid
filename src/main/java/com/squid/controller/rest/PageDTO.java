package com.squid.controller.rest;

import java.net.URL;

/**
 * DTO for page objects
 * @author Datim
 *
 */
public class PageDTO {

	public long id;
	public URL url;
	public String checksum;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public URL getUrl() {
		return url;
	}
	public void setUrl(URL url) {
		this.url = url;
	}
	public String getChecksum() {
		return checksum;
	}
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

}
