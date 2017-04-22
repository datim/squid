package com.squid.data;

import java.net.URL;

import org.springframework.data.repository.CrudRepository;

/**
 * Repository for accessing BoxData entities
 */
@Deprecated
public interface NodeDataRepository extends CrudRepository<NodeData, Long> {

	public NodeData findByUrl(URL url);
}
