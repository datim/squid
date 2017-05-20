package com.squid.data;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

/**
 * Topology record to map queries to discovered images
 * @author Datim
 *
 */
@Entity
@Table(name = "image_topology")
public class ImageTopology {
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	@Column (name = "id")
	private long id;

	@Column (name = "query_id")
	private long query;

	@Column (name = "image_id")
	private long imageId;

	@Column (name = "parent_page_id")
	private long parentPage;

	@Column (name = "create_time")
	private Date createTime;

	// generate create time stamp
	@PrePersist
	void createdAt() {
		createTime = new Date();
	}

	// default constructor
	public ImageTopology() {}

	// constructor
	public ImageTopology(long queryId, long imageId, long parentPageId) {
		this.imageId = imageId;
		query = queryId;
		parentPage = parentPageId;
	}

	public long getQuery() {
		return query;
	}

	public void setQuery(long query) {
		this.query = query;
	}

	public long getImageId() {
		return imageId;
	}

	public void setImageId(long imageId) {
		this.imageId = imageId;
	}

	public long getParentPage() {
		return parentPage;
	}

	public void setParentPage(long parentPage) {
		this.parentPage = parentPage;
	}

	public long getId() {
		return id;
	}

	public Date getCreateTime() {
		return createTime;
	}
}
