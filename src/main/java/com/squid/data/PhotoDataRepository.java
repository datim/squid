package com.squid.data;

import java.net.URL;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PhotoDataRepository extends PagingAndSortingRepository<PhotoData, Long> {

	PhotoData findByUrl(URL url);
	
	List<PhotoData> findAll(Sort sort);
}
