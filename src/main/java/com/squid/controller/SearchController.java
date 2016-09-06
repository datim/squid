package com.squid.controller;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.squid.config.SquidProperties;
import com.squid.controller.exceptions.ResourceNotFoundException;
import com.squid.controller.rest.SearchStatusDTO;
import com.squid.controller.rest.UserParameterDTO;
import com.squid.data.SearchStatusData;
import com.squid.data.UserParameterData;
import com.squid.service.UserParameterService;
import com.squid.service.WebCrawler;

/**
 * Controller for search related functionality
 * @author roecks
 *
 */
@RestController
@RequestMapping("/crawl/search")
public class SearchController {
	
	static Logger log = Logger.getLogger(SearchController.class.getName());

	@Autowired 
	private WebCrawler crawler;
	
	@Autowired
	private DataMapper dataMapper;
	
	@Autowired
	private SquidProperties squidProps;
	
	@Autowired
	private UserParameterService userParamService;
	
	/**
	 * Perform search on a URL
	 * @param inUrl
	 * @throws IOException
	 */
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    
    public void search(@RequestParam(value="discoverUrl", defaultValue = "") String inUrl) throws IOException {
    	
    	// initialize with default
		URL huntUrl = new URL(inUrl);

    	if (inUrl == null || inUrl.isEmpty()) {
    		// search URL not provided.  Use default
    		log.info("No URL specified. Using default URL: " + squidProps.getBaseUrl());
    		huntUrl = new URL(squidProps.getBaseUrl());
    	}
    	
    	log.info("Performing search on url: " + huntUrl);

    	crawler.startCrawl(huntUrl);
    }
    
    /**
     * Get search status for a specific URL
     */
    @RequestMapping(path = "/status", method = RequestMethod.GET)
    @ExceptionHandler({ResourceNotFoundException.class})
    public SearchStatusDTO getSearchStatus(@RequestParam(value="searchURL", defaultValue = "") String inUrl) {
    	
    	String searchURL = inUrl;
    	
    	if (inUrl.isEmpty()) {
        	//FIXME TODO remove default status during search
    		searchURL = userParamService.getDefaultUserParameters().getSearchURL();
    		log.warning("We're getting status based on default URL!");
    		//throw new ResourceNotFoundException("searchURL");
    	}
    	
    	final SearchStatusData dao = crawler.getSearchStatus(searchURL);
    	
    	if (dao == null) {
    		log.info("No status to report yet");
    		// no status yet, report no status
    		SearchStatusDTO dto = new SearchStatusDTO();
    		dto.setStatus("no status");
    		return dto;
    	} else {
    		log.fine("Request status: page count: " + dao.getNodeCount() + ", image count: " + dao.getImageCount());
    	}
    	
    	return dataMapper.daoToDto(dao);    	
    }
    
    /**
     * Fetch user parameters for a particular user id
     * @param id
     * @return
     */
    @RequestMapping(path = "/parameters/{id}", method = RequestMethod.GET)
    public UserParameterDTO getUserParameters(@PathVariable("id") long userId) {
    	
    	log.info("Fetching parameters for user " + userId);
    	UserParameterData dao = userParamService.getUserParameters(userId);
    	
    	UserParameterDTO dto = dataMapper.daoToDto(dao);
    	
    	return dto;    	
    }
}
