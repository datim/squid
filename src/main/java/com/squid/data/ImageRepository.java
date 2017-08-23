package com.squid.data;

import java.net.URL;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

/**
 * Data Access for Image table
 * @author Datim
 *
 */
public interface ImageRepository extends CrudRepository<Image, Long>{

	public Image findById(long id);
	public Image findByUrl(URL url);
	public List<Image> findAll();
}
