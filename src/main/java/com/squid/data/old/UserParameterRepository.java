package com.squid.data.old;

import org.springframework.data.repository.CrudRepository;

/**
 * Repository for UserParameters
 * @author roecks
 *
 */
@Deprecated
public interface UserParameterRepository extends CrudRepository<UserParameterData, Long>{

	public UserParameterData findByUserId(long userId);
}
