package com.squid.parser.old;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.squid.config.SquidProperties;
import com.squid.data.UserParameterData;
import com.squid.data.UserParameterRepository;

/**
 * Service for interfacing with user parameter data
 * @author roecks
 *
 */
@Service
public class UserParameterService {

	// we only have one user id right now
	public static final long DEFAULT_USER_ID = 1;
	
	@Autowired
	private UserParameterRepository userParamRepo;	
	
	@Autowired
	private SquidProperties squidProps;
	
	/**
	 * get the user parameters for the default user
	 * @return
	 */
	public UserParameterData getDefaultUserParameters() {
		return getUserParameters(DEFAULT_USER_ID);
	}
	
	/**
	 * Fetch user parameter data by id. Create it if it doesn't exist
	 * @param userId
	 * @return
	 */
	public UserParameterData getUserParameters(long userId) {
		
		UserParameterData userData = userParamRepo.findByUserId(userId);
		
		if (userData == null) {
			userData = createAndSaveDefaultParameters(userId);
		}
		
		return userData;
	}
	
	/**
	 * Generate default user parameters for a particular user
	 * @param userId
	 * @return
	 */
	private UserParameterData createAndSaveDefaultParameters(long userId) {
		final UserParameterData userData = new UserParameterData();
		
		// construct default parameters for the user parameter
		userData.setSearchURL(squidProps.getBaseUrl());
		userData.setMaxImageCount(squidProps.getMaxImages());
		userData.setMaxPageCount(squidProps.getMaxNodes());
		userData.setUserId(userId);
		userData.setSavePath(squidProps.getImageSavePath());
		userData.setSearchFilter("");
		
		return userParamRepo.save(userData);		
	}
	/**
	 * Set the filter for a particular user
	 * @param userId
	 * @param filter
	 * @return
	 */
	public UserParameterData setUserFilter(long userId, String filter) {
		
		final UserParameterData data = this.getUserParameters(userId);
		
		// update the parameters and return
		data.setSearchFilter(filter);
		
		return userParamRepo.save(data);		
	}
	
	/**
	 * Set the search string for a user
	 * @param userId
	 * @param searchString
	 * @return
	 */
	public UserParameterData setUserSearchString(long userId, String searchString) {
		
		final UserParameterData data = this.getUserParameters(userId);
		
		data.setSearchURL(searchString);
		
		return userParamRepo.save(data);		
	}
}
