package com.squid.data.old;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Represent Search Status table object
 */
@Deprecated
@Entity
@Table(name = "search_status")
public class SearchStatusData {

	public enum SearchStatus {
		NoResults("No Results"), InProgress("In Progress"), Complete("Complete"), Error("Error");

		String value;

		SearchStatus(String value) {
			this.value = value;
		}

		// get the string representation of this enums
		public String getString() {
			return value;
		}
	}

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	@Column (name = "id")
	private long id;

	@Column (name = "search_url")
	private String url;

	@Column (name = "node_count")
	private long nodeCount;

	@Column (name = "max_depth")
	private long maxDepth;

	@Column (name = "image_count")
	private long imageCount;

	@Column (name = "status")
	private String status;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getNodeCount() {
		return nodeCount;
	}

	public void setNodeCount(long nodeCount) {
		this.nodeCount = nodeCount;
	}

	public long getMaxDepth() {
		return maxDepth;
	}

	public void setMaxDepth(long maxDepth) {
		this.maxDepth = maxDepth;
	}

	public SearchStatus getStatus() {
		return SearchStatus.valueOf(status);
	}

	public void setStatus(SearchStatus status) {
		this.status = status.toString();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getImageCount() {
		return imageCount;
	}

	public void setImageCount(long count) {
		imageCount = count;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
