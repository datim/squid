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

	@Column (name = "checksum")
	private String checksum;

	// default constructor
	public Page() {}

	// constructor
	public Page(final URL url, final String checksum) {
		this.url = url;
		this.checksum = checksum;
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

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
}
