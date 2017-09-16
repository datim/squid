package com.squid.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.squid.controller.rest.ImageDTO;
import com.squid.controller.rest.PageDTO;
import com.squid.controller.rest.QueryDTO;
import com.squid.controller.rest.QueryStatusDTO;
import com.squid.data.Image;
import com.squid.data.Page;
import com.squid.data.Query;
import com.squid.service.QueryService;
import com.squid.service.SearchService;

import javassist.NotFoundException;

/**
 * Query related status
 * @author Datim
 *
 */
@RestController
@RequestMapping("/query")
public class QueryController {

    private static final Logger log = LoggerFactory.getLogger(QueryController.class);

	@Autowired
	private SearchService sService;

	@Autowired
	private DataMapper dataMapper;

	@Autowired
	private QueryService queryService;


	/**
	 * Get a Query object by id
	 * @param queryId
	 * @return The query object that matches the requested ID
	 */
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<Object> getQuery(@PathVariable("id") long queryId) {

		Query dao;

		try {
			dao = queryService.getQuery(queryId);

		} catch (final NotFoundException e) {
			final String errorMsg = "query with id '" + queryId + "' does not exist. Exception: " + e.getMessage();
			log.error(errorMsg);
    		return new ResponseEntity<Object>(errorMsg, HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.accepted().body(dataMapper.convert(dao));
	}

    /**
     * Delete an existing query
     * @param queryId
     * @return OK if query deleted
     */
    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteQuery(@PathVariable("id") long queryId) {

		try {
			queryService.deleteQuery(queryId);

		} catch (final NotFoundException e) {
			final String errorMsg = "query with id '" + queryId + "' does not exist. Exception: " + e.getMessage();
			log.error(errorMsg);
    		return new ResponseEntity<Object>(errorMsg, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<Object>(queryId + " deleted", HttpStatus.OK);
	}

    /**
     * Return a list of pages associated with the query
     * @param queryId The query to search for
     * @return A list of pages associated with the query
     */
    @RequestMapping(path = "/{id}/page", method = RequestMethod.GET)
    public ResponseEntity<Object> getQueryPages(@PathVariable("id") long queryId) {

    	final List<Page> pageDaos = sService.getQueryPages(queryId);
    	final List<PageDTO> pageDtos = new ArrayList<>(pageDaos.size());

    	for (final Page page: pageDaos) {
    		pageDtos.add(dataMapper.convert(page));
    	}

		return ResponseEntity.accepted().body(pageDtos);
    }

    /**
     * Return a list of pages associated with the query
     * @param queryId The query to search for
     * @return A list of pages associated with the query
     */
    @RequestMapping(path = "/{id}/image", method = RequestMethod.GET)
    public ResponseEntity<Object> getQueryImages(@PathVariable("id") long queryId) {

    	final List<Image> imageDaos = sService.getQueryImages(queryId);
    	final List<ImageDTO> imageDtos = new ArrayList<>(imageDaos.size());

    	for (final Image image: imageDaos) {
    		imageDtos.add(dataMapper.convert(image));
    	}

		return ResponseEntity.accepted().body(imageDtos);
    }

	/**
	 * Status of a query
	 * @param queryId
	 * @return
	 */
    @RequestMapping(path = "/{id}/status", method = RequestMethod.GET)
    public ResponseEntity<Object> getQueryStatus(@PathVariable("id") long queryId) {

    	QueryStatusDTO status = null;

    	try {
    		// construct a status object for the query
        	final String queryStatus = sService.getQueryStatus(queryId);
           	final long imageCount = sService.getQueryImageCount(queryId);
        	final long pageCount = sService.getQueryPageCount(queryId);
        	status = new QueryStatusDTO(queryId, pageCount, imageCount, queryStatus);

        	log.info("Found {} pages and {} images for query id {}. Status is {}", pageCount, imageCount, queryId, queryStatus);

    	} catch (final Exception e) {
    		status = null;
    		log.error("Exception '{}' occured fetching status for id '{}'", e.getMessage(), queryId);
    	}

    	if (status == null) {
    		final String errorMsg = "Unable to find query status for id " + queryId;
    		log.error(errorMsg);
    		return new ResponseEntity<Object>(errorMsg, HttpStatus.BAD_REQUEST);
    	}

		return ResponseEntity.accepted().body(status);
    }

	/**
	 * Return list of all search queries
	 * @return
	 */
	@RequestMapping(path = "/all", method = RequestMethod.GET)
	public ResponseEntity<Object> getQueries() {

		final List<Query> queries = sService.getQueries();
		final List<QueryDTO> dtos = new ArrayList<>(queries.size());

		for (final Query dao: queries) {
			dtos.add(dataMapper.convert(dao));
		}

		return ResponseEntity.accepted().body(dtos);
	}
}
