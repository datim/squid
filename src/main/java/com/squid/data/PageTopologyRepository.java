package com.squid.data;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Database access for page topology records
 * @author Datim
 *
 */
public interface PageTopologyRepository extends CrudRepository<PageTopology, Long> {

	public PageTopology findById(long id);

	public PageTopology findByQueryAndPage(long queryId, long pageId);

	public List<PageTopology> findByQuery(long queryId);

	@Modifying
	@Transactional
	public long deleteByQuery(long queryId);
}
