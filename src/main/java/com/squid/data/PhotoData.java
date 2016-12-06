package com.squid.data;

import java.net.URL;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Data Access class for photos
 */
@Entity
@Table(name = "node_photo")
public class PhotoData {
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	@Column (name = "id")
	private long id;
	
	@Column (name = "url")
	private URL url;
	
	@Column (name = "node_url")
	private URL nodeUrl;
	
	@Column (name = "base_url")
	private String baseUrl;
	
	@Column (name = "name")
	private String name;
	
	@Column (name = "width")
	private int width;
	
	@Column (name = "heigth")
	private int heigth;
	
	@Column (name = "saved")
	private boolean saved;
	
	@Column (name = "etag")
	private String etag;
	
	@Column (name = "pinned")
	private boolean pinned;
	
	public String toString() {
		return "id " + this.id + ", img url: " + this.url + ", page url " + this.nodeUrl + ", base url: " + this.baseUrl +
				", name: " + this.name + ", saved: " + this.saved + ", etag: " + this.etag;
	}
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeigth() {
		return heigth;
	}

	public void setHeigth(int heigth) {
		this.heigth = heigth;
	}

	public boolean isSaved() {
		return saved;
	}

	public void setSaved(boolean saved) {
		this.saved = saved;
	}

	public boolean isPinned() {
		return pinned;
	}

	public void setPinned(boolean pinned) {
		this.pinned = pinned;
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

	public URL getNodeUrl() {
		return nodeUrl;
	}

	public void setNodeUrl(URL nodeUrl) {
		this.nodeUrl = nodeUrl;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getEtag() {
		return etag;
	}

	public void setEtag(String etag) {
		this.etag = etag;
	}
}
