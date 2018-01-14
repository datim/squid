package com.squid.data;

import java.net.URL;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Data Access for Image table
 * @author Datim
 *
 */
public interface ImageRepository extends PagingAndSortingRepository<Image, Long>{

	public List<Image> findById(long id);
	public Image findByUrl(URL url);
	public List<Image> findAll();
	public Page<Image> findByIdIn(List<Long> imgQueryIds, Pageable pageable);
}
