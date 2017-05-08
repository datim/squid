package com.squid.data;

import org.springframework.data.repository.CrudRepository;

/**
 * Database access for page topology records
 * @author Datim
 *
 */
public interface PageTopologyRepository extends CrudRepository<PageTopology, Long> {

	public PageTopology findById(long id);

	public PageTopology findByQueryAndPage(long queryId, long pageId);
}
