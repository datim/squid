package com.squid.data;

import java.net.URL;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Data access for image table. Represent image discovered during search.
 * @author Datim
 *
 */
@Entity
@Table(name = "image")
public class Image {

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	@Column (name = "id")
	private long id;

	@Column (name = "url")
	private URL url;

	@Column (name = "name")
	private String name;

	@Column (name = "width")
	private int width;

	@Column (name = "height")
	private int height;

	@Column (name = "checksum")
	private String checkSum;

    @Column(name="discovered", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Timestamp discovered;

	@Column (name = "b_size")
	private int byteSize;

	@Column (name = "tshirt_size")
	private String size;

	// default constructor
	public Image() {}

	// constructor
	public Image(final URL url, final String checkSum, final String name) {
		this.url = url;
		this.checkSum = checkSum;
		this.name = name;
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

	public String getCheckSum() {
		return checkSum;
	}

	public void setCheckSum(String checkSum) {
		this.checkSum = checkSum;
	}

	public long getId() {
		return id;
	}
}
