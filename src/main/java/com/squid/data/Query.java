package com.squid.data;

import java.net.URL;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/*
 * Queries initiated by the user. Represents the root node of a page crawl tree.
 */
@Entity
@Table(name = "query")
public class Query {

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	@Column (name = "id")
	private long id;

	@Column (name = "name")
	private String name;

	@Column (name = "url")
	private URL url;

	@Column (name = "max_pages")
	private int maxPages;

	@Column (name = "max_images")
	private int maxImages;

	// default constructor
	public Query() {}

	// constructor
	public Query(final URL url, int maxPages, int maxImages) {
		this.url = url;
		this.maxPages = maxPages;
		this.maxImages = maxImages;
	}

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
