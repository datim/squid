package com.squid.data;

import org.springframework.data.repository.CrudRepository;

/**
 * Repository for UserParameters
 * @author roecks
 *
 */
public interface UserParameterRepository extends CrudRepository<UserParameterData, Long>{

	public UserParameterData findByUserId(long userId);
}
