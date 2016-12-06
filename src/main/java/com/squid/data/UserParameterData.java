package com.squid.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity representing all user configured parameters
 * @author roecks
 *
 */
@Entity
@Table(name = "user_parameters")
public class UserParameterData {
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	@Column (name = "id")
	private long id;
	
	@Column (name = "user_id")
	private long userId;
	
	@Column (name = "search_url")
	String searchURL;
	
	@Column (name = "search_filter")
	String searchFilter;
	
	@Column (name = "max_node_count")
	long maxPageCount;
	
	@Column (name = "max_image_count")
	long maxImageCount;
	
	@Column (name = "save_path")
	String savePath;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSearchURL() {
		return searchURL;
	}

	public void setSearchURL(String searchURL) {
		this.searchURL = searchURL;
	}

	public String getSearchFilter() {
		return searchFilter;
	}

	public void setSearchFilter(String searchFilter) {
		this.searchFilter = searchFilter;
	}

	public long getMaxPageCount() {
		return maxPageCount;
	}

	public void setMaxPageCount(long maxPageCount) {
		this.maxPageCount = maxPageCount;
	}

	public long getMaxImageCount() {
		return maxImageCount;
	}

	public void setMaxImageCount(long maxImageCount) {
		this.maxImageCount = maxImageCount;
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}
}
