package com.squid.controller.rest;

/**
 * Status of a query
 */
public class QueryStatusDTO {
	public long queryId;
	public long pageCount;
	public long imageCount;
	private final String status;

	// constructor
	public QueryStatusDTO(long queryId, long pageCount, long imageCount, final String status) {
		this.queryId = queryId;
		this.pageCount = pageCount;
		this.imageCount = imageCount;
		this.status = status;
	}

	public long getQueryId() {
		return queryId;
	}

	public long getPageCount() {
		return pageCount;
	}

	public long getImageCount() {
		return imageCount;
	}

	public String getStatus() {
		return status;
	}
}
