package com.squid.controller;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.squid.data.old.SearchStatusData;
import com.squid.data.old.UserParameterData;
import com.squid.engine.old.UserParameterService;
import com.squid.service.SearchService;

/**
 * Controller for controlling and monitoring searches
 * @author roecks
 *
 */
@RestController
@RequestMapping("/crawl/search")
public class SearchController {

    private static final Logger log = LoggerFactory.getLogger(SearchController.class);

	@Autowired
	private SearchService sService;

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
    public ResponseEntity<Object>  search(@RequestParam(value="discoverUrl", defaultValue = "") String inUrl)
    		throws IOException {

    	if ((inUrl == null) || inUrl.isEmpty()) {

    		// search URL not provided.  Throw not found exception
    		log.error("url not provided");
    		final Map<String,String> responseBody = new HashMap<>();
    		responseBody.put("message","URL to search not provided.");
    		return new ResponseEntity<Object>(responseBody, HttpStatus.BAD_REQUEST);
    	}

    	// initialize with default
		final URL searchUrl = new URL(inUrl);
    	log.info("Performing search on url '{}'", searchUrl);

    	// start the search
    	sService.startSearch(searchUrl);

    	return new ResponseEntity<Object>(HttpStatus.OK);
    }

    /**
     * Get search status for a specific URL
     */
    @Deprecated
    @RequestMapping(path = "/status", method = RequestMethod.GET)
    @ExceptionHandler({ResourceNotFoundException.class})
    public SearchStatusDTO getSearchStatus(@RequestParam(value="searchURL", defaultValue = "") String inUrl) {

    	String searchURL = inUrl;

    	if (inUrl.isEmpty()) {
        	//FIXME TODO remove default status during search
    		searchURL = userParamService.getDefaultUserParameters().getSearchURL();
    		log.warn("We're getting status based on default URL!");
    		//throw new ResourceNotFoundException("searchURL");
    	}

    	final SearchStatusData dao = sService.getSearchStatus(searchURL);

    	if (dao == null) {
    		log.info("No status to report yet");
    		// no status yet, report no status
    		final SearchStatusDTO dto = new SearchStatusDTO();
    		dto.setStatus("no status");
    		return dto;
    	} else {
    		log.info("Request status: page count: " + dao.getNodeCount() + ", image count: " + dao.getImageCount());
    	}

    	return dataMapper.daoToDto(dao);
    }

    /**
     * Fetch user parameters for a particular user id
     * @param id
     * @return
     */
    @Deprecated
    @RequestMapping(path = "/parameters/{id}", method = RequestMethod.GET)
    public UserParameterDTO getUserParameters(@PathVariable("id") long userId) {

    	log.info("Fetching parameters for user " + userId);
    	final UserParameterData dao = userParamService.getUserParameters(userId);

    	final UserParameterDTO dto = dataMapper.daoToDto(dao);

    	return dto;
    }
}
