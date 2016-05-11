package com.squid.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Data Access object for a traversed URL node
 */
@Entity
@Table(name = "url_node")
public class NodeData {

	@Id
	@Column (name = "url")
	private String url;
	
	@Column (name = "parent_node")
	private String parentUrl;
	
	@Column (name = "visited")
	private boolean visited;
	
	public boolean isVisited() {
		return visited;
	}
	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public String getParent() {
		return parentUrl;
	}
	public void setParentUrl(String parent) {
		this.parentUrl = parent;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
