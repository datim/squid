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

	@Column (name = "md5")
	private String md5;

	// default constructor
	public Page() {}

	// constructor
	public Page(final URL url, final String md5) {
		this.url = url;
		this.md5 = md5;
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

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public long getId() {
		return id;
	}
}
