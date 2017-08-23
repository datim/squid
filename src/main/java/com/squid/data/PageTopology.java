package com.squid.data;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

/*
 * Tree mapping of all pages discovered during a query.
 */
@Entity
@Table(name = "page_topology")
public class PageTopology {

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	@Column (name = "id")
	private long id;

	@Column (name = "query_id")
	private long query;

	/*
	@OneToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinColumn(name = "page_id")
	private Page page;
	*/

	@Column (name = "page_id")
	private long page;


	@Column (name = "parent_page_id")
	private long parentPage;

	@Column (name = "create_time")
	private Date createTime;

	// generate create time stamp
	@PrePersist
	void createdAt() {
		createTime = new Date();
	}

/*
	@PreUpdate
	  void updatedAt() {
	    this.updateTime = new Date();
	  }
*/
	// default constructor
	public PageTopology() {}

	// constructor
	public PageTopology(long queryId, long pageId, long parentPageId) {
		page = pageId;
		query = queryId;
		parentPage = parentPageId;
	}

	//constructor
	// no parent page. Parent is itself
	public PageTopology(long queryId, long pageId) {
		page = pageId;
		query = queryId;
		parentPage = pageId;
	}

	public long getQuery() {
		return query;
	}

	public void setQuery(long query) {
		this.query = query;
	}

	public long getPage() {
		return page;
	}

	public void setPage(long page) {
		this.page = page;
	}


	public long getParentPage() {
		return parentPage;
	}

	public void setParentPage(long parentPage) {
		this.parentPage = parentPage;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public long getId() {
		return id;
	}
}
