package com.squid.controller.rest;

/**
 * DTO for search status
 */
public class SearchStatusDTO {

	public String url;
	public long nodeCount;
	public long maxDepth;
	public String status;
	public long id;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public long getNodeCount() {
		return nodeCount;
	}
	public void setNodeCount(long nodeCount) {
		this.nodeCount = nodeCount;
	}
	public long getMaxDepth() {
		return maxDepth;
	}
	public void setMaxDepth(long maxDepth) {
		this.maxDepth = maxDepth;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
}
