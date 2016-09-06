package com.squid.data;

import java.net.URL;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PhotoDataRepository extends PagingAndSortingRepository<PhotoData, Long> {

	PhotoData findByUrl(URL url);
	
	PhotoData findById(long id);
	
	List<PhotoData> findAll(Sort sort);
	
	PhotoData findByNameAndBaseUrl(String name, String baseUrl);
		
	@Query(value = "Select p from PhotoData p where p.name like %?1%")
	List<PhotoData> findFilteredPhotos(String filter);
}
