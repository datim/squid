package com.squid.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Represent Search Status table object
 */
@Entity
@Table(name = "search_status")
public class SearchStatusData {
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	@Column (name = "id")
	private long id;
	
	@Column (name = "search_url")
	private String url; 

	@Column (name = "node_count")
	private long nodeCount;
	
	@Column (name = "max_depth")
	private long maxDepth;
	
	@Column (name = "status")
	private String status;

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
