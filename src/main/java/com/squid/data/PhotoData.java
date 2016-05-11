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
	private int id;

	@Column (name = "name")
	private String name;
	
	@Column (name = "url")
	private URL url;
	
	@Column (name = "width")
	private int width;
	
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

	@Column (name = "heigth")
	private int heigth;
	
	@Column (name = "saved")
	private boolean saved;
	
	@Column (name = "pinned")
	private boolean pinned;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
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



}
