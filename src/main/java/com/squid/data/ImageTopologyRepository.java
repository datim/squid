package com.squid.data;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Database access for image topology records
 * @author Datim
 *
 */
public interface ImageTopologyRepository extends CrudRepository<ImageTopology, Long> {

	public ImageTopology findById(long id);
	public ImageTopology findByQueryAndImageId(long queryId, long imageId);
	public ImageTopology findByQueryAndParentPage(long queryId, long parentPageId);
	public List<ImageTopology> findByQuery(long queryId);


	@Modifying
	@Transactional
	public long deleteByQuery(long queryId);
}

