package com.squid.data;

import java.net.URL;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

/**
 * Database access for query records
 * @author Datim
 *
 */
public interface QueryRepository extends CrudRepository<Query, Long> {

	List<Query> findAll();
	Query findById(long id);
	Query findByUrl(URL url);
}
