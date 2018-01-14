package com.squid.data;

import java.net.URL;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

/**
 * Database access for page records
 * @author Datim
 *
 */
public interface PageRepository extends CrudRepository<FoundPage, Long> {

	public FoundPage findById(long id);
	public FoundPage findByUrl(URL url);
	public List<FoundPage> findAll();
}
