package com.squid.data;

import java.net.URL;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Data Access for page table. Represent URL pages discovered during search.
 *
 * @author Datim
 *
 */
@Entity
@Table(name = "page")
public class Page {

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	@Column (name = "id")
	private long id;

	@Column (name = "url")
	private URL url;

	@Column (name = "etag")
	private String etag;

	// constructor
	public Page() {
		this(null, null);
	}

	// constructor
	public Page(final URL url, final String etag) {
		this.url = url;
		this.etag = etag;
	}

	// constructor
	public Page(final URL url) {
		this(url, null);
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public String getEtag() {
		return etag;
	}

	public void setEtag(String etag) {
		this.etag = etag;
	}
}
