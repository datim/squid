package com.squid.controller.rest;

/**
 * DTO class for transferring photo objects
 */
public class PhotoDTO {

	public String url;
	public String name;
	public int id;
	public String nodeUrl;
	public int height;
	public int width;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNodeUrl() {
		return nodeUrl;
	}
	public void setNodeUrl(String nodeUrl) {
		this.nodeUrl = nodeUrl;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
}
