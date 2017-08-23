package com.squid.controller.rest;

/**
 * DTO representing user parameters
 * @author roecks
 *
 */
public class UserParameterDTO {
	
	public long id;
	public long userId;
	public String searchURL;
	public String searchFilter;
	public long maxPageCount;
	public long maxImageCount;
	public String savePath;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getSearchURL() {
		return searchURL;
	}
	public void setSearchURL(String searchURL) {
		this.searchURL = searchURL;
	}
	public String getSearchFilter() {
		return searchFilter;
	}
	public void setSearchFilter(String searchFilter) {
		this.searchFilter = searchFilter;
	}
	public long getMaxPageCount() {
		return maxPageCount;
	}
	public void setMaxPageCount(long maxPageCount) {
		this.maxPageCount = maxPageCount;
	}
	public long getMaxImageCount() {
		return maxImageCount;
	}
	public void setMaxImageCount(long maxImageCount) {
		this.maxImageCount = maxImageCount;
	}

	public String getSavePath() {
		return savePath;
	}
	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}
	
	
}
