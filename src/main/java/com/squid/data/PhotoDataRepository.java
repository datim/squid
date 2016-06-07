package com.squid.data;

import java.net.URL;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

//public interface PhotoDataRepository extends PagingAndSortingRepository<PhotoData, Long> {
public interface PhotoDataRepository extends CrudRepository<PhotoData, Long> {


	PhotoData findByUrl(URL url);
}
