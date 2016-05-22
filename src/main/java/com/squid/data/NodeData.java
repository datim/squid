package com.squid.data;

import java.net.URL;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Data Access object for a traversed URL node
 */
@Entity
@Table(name = "node")
public class NodeData {

	@Id
	@Column (name = "url")
	private URL url;
	
	@Column (name = "parent_url")
	private URL parentUrl;
	
	@Column (name = "visited")
	private boolean visited;
	
	public boolean isVisited() {
		return visited;
	}
	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public URL getParent() {
		return parentUrl;
	}
	public void setParentUrl(URL parent) {
		this.parentUrl = parent;
	}
	public URL getUrl() {
		return url;
	}
	public void setUrl(URL url) {
		this.url = url;
	}
}
