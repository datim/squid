package com.squid.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.squid.data.Query;

/**
 * Service to track individual query status
 * @author Datim
 *
 */
@Service
public class QueryStatusService {

	private enum QueryStatusEnum { RUNNING, STOPPED };
	private final Map<Long, QueryStatusEnum> queryStatus = new HashMap<>();

	// check if a query is stopped
	public boolean isStopped(final Query query) {
		return queryStatus.get(query.getId()).equals(QueryStatusEnum.STOPPED);
	}

	// check if a query is running
	public boolean isRunning(final Query query) {
		return queryStatus.get(query.getId()).equals(QueryStatusEnum.RUNNING);
	}

	// thread safe, mark a query as started
	public synchronized void setQueryStatusRunning(final Query query) {
		queryStatus.put(query.getId(), QueryStatusEnum.RUNNING);
	}

	// thread safe, mark a query as stopped
	public void setStop(final Query query) {
		queryStatus.put(query.getId(),  QueryStatusEnum.STOPPED);
	}

	// return the query status
	public String getStatus(final Query query) {
		return queryStatus.get(query.getId()).toString();
	}

	// thread safe, remove a query
	public synchronized void purge(final Query query) {
		if (queryStatus.containsKey(query)) {
			queryStatus.remove(query);
		}
	}

	// thread safe, remove all queries
	public synchronized void purgeAll() {
		queryStatus.clear();
	}
}
