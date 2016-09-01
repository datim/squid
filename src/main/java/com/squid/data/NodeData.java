package com.squid.data;

import java.net.URL;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Data Access object for a traversed URL node
 */
@Entity
@Table(name = "node")
public class NodeData {

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	@Column (name = "id")
	private long id;
	
	@Column (name = "url")
	private URL url;
	
	@Column (name = "parent_url")
	private URL parentUrl;
	
	@Column (name = "visited")
	private boolean visited;
	
	@Column (name = "etag")
	private String etag;
	
	public String toString() {
		return "Page, url: " + this.url + ", parent: " + this.parentUrl;
	}
	
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
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public URL getParentUrl() {
		return parentUrl;
	}
	public String getEtag() {
		return etag;
	}
	public void setEtag(String etag) {
		this.etag = etag;
	}
}
