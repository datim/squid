package com.squid.data.old;

import org.springframework.data.repository.CrudRepository;

/**
 * Repository for accessing current search status
 */
@Deprecated
public interface SearchStatusRepository extends CrudRepository<SearchStatusData, Long>{

	public SearchStatusData findByUrl(String url);
}
