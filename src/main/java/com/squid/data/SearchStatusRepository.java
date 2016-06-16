package com.squid.data;

import org.springframework.data.repository.CrudRepository;

/**
 * Repository for accessing current search status
 */
public interface SearchStatusRepository extends CrudRepository<SearchStatusData, Long>{
	
	public SearchStatusData findByUrl(String url);
}
