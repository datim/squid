package com.squid.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.squid.controller.rest.ImageDTO;
import com.squid.controller.rest.PageDTO;
import com.squid.controller.rest.QueryDTO;
import com.squid.controller.rest.QueryStatusDTO;
import com.squid.data.FoundPage;
import com.squid.data.Image;
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
@RequestMapping("/api/query")
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
	@PostMapping(path = "/{id}")
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
    @DeleteMapping(path = "/{id}")
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
    @GetMapping(path = "/{id}/page")
    public ResponseEntity<Object> getQueryPages(@PathVariable("id") long queryId) {

    	final List<FoundPage> pageDaos = sService.getQueryPages(queryId);

    	// convert DAO to DTO
    	final List<PageDTO> pageDtos = pageDaos.stream()
    			.map(page -> dataMapper.convert(page))
    			.collect(Collectors.toList());

		return ResponseEntity.accepted().body(pageDtos);
    }

    /**
     * Return a list of images associated with the query
     * @param queryId The query to search for
     * @return A list of pages associated with the query
     */
    @GetMapping(path = "/{id}/image")
    public Page<ImageDTO> getQueryImages(@PathVariable("id") long queryId, Pageable pageRequest) {

    	Page<ImageDTO> pageDTOs = null;

		try {
			final Page<Image> imagePage = sService.getQueryImages(queryId, pageRequest);

			// convert to page DTO requests
			pageDTOs = imagePage.map(DataMapper::convert);

		} catch (final NotFoundException e) {
			ResponseEntity.badRequest().body("Query id '" + queryId + " not found");
		}

		return pageDTOs;
    }

	/**
	 * Status of a query
	 * @param queryId
	 * @return
	 */
    @GetMapping(path = "/{id}/status")
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
	 * @return List of all query objects
	 */
    @GetMapping
	public ResponseEntity<Object> getQueries() {

		final List<QueryDTO> dtos = sService.getQueries().stream()
				.map(dao -> dataMapper.convert(dao))
				.collect(Collectors.toList());

		return ResponseEntity.accepted().body(dtos);
	}
}
