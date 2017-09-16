package com.squid.data;

import java.net.URL;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

/**
 * Database access for page records
 * @author Datim
 *
 */
public interface PageRepository extends CrudRepository<Page, Long> {

	public Page findById(long id);
	public Page findByUrl(URL url);
	public List<Page> findAll();
}
