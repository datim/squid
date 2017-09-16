package com.squid.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.squid.service.SearchService;

/**
 * Controller for controlling and monitoring searches
 * @author roecks
 *
 */
@RestController
@RequestMapping("/api/search")
public class SearchController {

    private static final Logger log = LoggerFactory.getLogger(SearchController.class);

	@Autowired
	private SearchService sService;

	/**
	 * Perform search on a URL
	 * @param inUrl
	 * @throws IOException
	 */
    @PostMapping(path = "/start")
    public ResponseEntity<Object>  search(@RequestBody String inBodyJSON)
    		throws IOException {

    	final Map<String, Object> parsedData = new JacksonJsonParser().parseMap(inBodyJSON);

    	if (!parsedData.containsKey("url")) {
    		log.error("'discoveryUrl' key not provided in POST form");
    		final Map<String,String> responseBody = new HashMap<>();
    		responseBody.put("message","Form parameter 'discoveryUrl' missing.");
    		return new ResponseEntity<Object>(responseBody, HttpStatus.BAD_REQUEST);
    	}

    	final String inUrl = (String) parsedData.get("url");

    	if ((inUrl == null) || inUrl.isEmpty()) {

    		// search URL not provided.  Throw not found exception
    		log.error("A Valid URL not provided. Provided URL is '{}'", inUrl);
    		final Map<String,String> responseBody = new HashMap<>();
    		responseBody.put("message","URL to search not provided.");
    		return new ResponseEntity<Object>(responseBody, HttpStatus.BAD_REQUEST);
    	}

    	// initialize with default
    	URL searchUrl = null;

    	try {
    		searchUrl = new URL(inUrl);

    	} catch (final MalformedURLException e) {
    		log.error("Search attempted with malformed URL '{}' ", inUrl);
    		return ResponseEntity.badRequest().body("Malformed URL '" + inUrl + "'");
    	}

    	// start the search. Fetch the resulting ID
    	final long id = sService.startSearch(searchUrl);

    	log.info("Performing search on url '{}'. ID is: {}", searchUrl, id);

    	// return ID
    	return ResponseEntity.ok().body(id);
    }

}
