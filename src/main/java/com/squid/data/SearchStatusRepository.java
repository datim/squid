package com.squid.data;

import org.springframework.data.repository.CrudRepository;

import com.squid.data.old.SearchStatusData;

/**
 * Repository for accessing current search status
 */
@Deprecated
public interface SearchStatusRepository extends CrudRepository<SearchStatusData, Long>{

	public SearchStatusData findByUrl(String url);
}
