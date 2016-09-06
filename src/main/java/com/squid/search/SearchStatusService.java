package com.squid.search;

import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.squid.config.SquidProperties;
import com.squid.data.SearchStatusData;
import com.squid.data.SearchStatusRepository;

// TODO - make the search a loop that polls the status every 5 seconds.   It should be commanded
// to start and stop by the beginning and end of the search thread
/**
 * @author roecks
 *
 */
@Service
public class SearchStatusService {
	
	@Autowired
	private SearchStatusRepository searchStatusRepo;
	
	/**
	 * Begin searching
	 */
	public synchronized void startSearch() {
		
	}
	
	public synchronized void stopSearch() {
		
	}
	
	/**
	 * Update search status object.
	 * 
	 * @param pageCount the current number of pages searched
	 * @param imageCount the current number of images searched
	 * @param rootUrl the URL on which the search was initiated
	 * @param status
	 */
	public  void updateSearchStatus(Long pageCount, Long imageCount, long maxSearchPages, final URL rootUrl, 
									SearchStatusData.SearchStatus status) {
		
		// maintain one record for status. If the record already exists, update it
		SearchStatusData searchStatus = searchStatusRepo.findByUrl(rootUrl.toString());
		
		if (searchStatus == null) {
			// record doesn't exist, create a new one
			searchStatus = new SearchStatusData();
		}
		
		// update the status results
		searchStatus.setUrl(rootUrl.toString());
		searchStatus.setMaxDepth(maxSearchPages);
		searchStatus.setNodeCount(pageCount);
		searchStatus.setImageCount(imageCount);
		searchStatus.setStatus(status);
		
		// save the status
		searchStatusRepo.save(searchStatus);
	}
}
