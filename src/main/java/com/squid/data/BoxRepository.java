package com.squid.data;

import org.springframework.data.repository.CrudRepository;

public interface BoxRepository  extends CrudRepository<BoxData, Long>{
	
	public BoxData findById(long id);

}
