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
@Table(name = "nodePhoto")
public class PhotoData {
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	@Column (name = "name")
	private String name;
	
	@Column (name = "url")
	private URL url;
	
	@Column (name = "size")
	private int size;
	
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

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}


}
