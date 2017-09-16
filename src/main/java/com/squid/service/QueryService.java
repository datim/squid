package com.squid.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.squid.data.Query;
import com.squid.data.QueryRepository;

import javassist.NotFoundException;

/**
 * Manage contents and lifecycle of query objects
 * @author Datim
 *
 */
@Service
public class QueryService {

    private static final Logger log = LoggerFactory.getLogger(QueryService.class);

	@Autowired
	private QueryRepository queryRepo;

	/**
	 * Delete an existing query
	 * @param queryId
	 * @throws NotFoundException
	 */
	public void deleteQuery(long queryId) throws NotFoundException {

		final Query queryToDelete = getQuery(queryId);

		// make sure query exists before deleting it
		if (queryToDelete == null) {
			throw new NotFoundException("Query " + queryId + " does not exist");
		}

		queryRepo.delete(queryToDelete);
	}

	/**
	 * Find a query by id
	 * @param queryId The query to search for
	 * @throws NotFoundException
	 */
	public Query getQuery(long queryId) throws NotFoundException {
		final Query query = queryRepo.findById(queryId);

		if (query == null) {
			throw new NotFoundException("Query " + queryId + " does not exist");
		}

		return query;
	}

}
