package com.squid.controller.rest;

import java.net.URL;
import java.sql.Timestamp;

/**
 * Image DTO for Images
 * @author Datim
 */
public class ImageDTO {

	public long id;
	public URL url;
	public String name;
	public int width;
	public int height;
	public String checkSum;
	public Timestamp discovered;
	public int byteSize;
	public String size;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public URL getUrl() {
		return url;
	}
	public void setUrl(URL url) {
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public String getCheckSum() {
		return checkSum;
	}
	public void setCheckSum(String checkSum) {
		this.checkSum = checkSum;
	}
	public Timestamp getDiscovered() {
		return discovered;
	}
	public void setDiscovered(Timestamp discovered) {
		this.discovered = discovered;
	}
	public int getByteSize() {
		return byteSize;
	}
	public void setByteSize(int byteSize) {
		this.byteSize = byteSize;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}

}
