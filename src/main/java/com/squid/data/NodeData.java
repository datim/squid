package com.squid.data;

import java.net.URL;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Data Access object for a traversed URL node
 */
@Entity
@Table(name = "url_node")
public class NodeData {

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
		
	@ManyToOne(cascade={CascadeType.ALL})
	@JoinColumn(name = "parent_node")
	private NodeData parent;
	
	@Column (name = "url")
	private URL url;
	
	@Column (name = "visited")
	private boolean visited;
	
	public boolean isVisited() {
		return visited;
	}
	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public NodeData getParent() {
		return parent;
	}
	public void setParent(NodeData parent) {
		this.parent = parent;
	}
	public URL getUrl() {
		return url;
	}
	public void setUrl(URL url) {
		this.url = url;
	}
}
