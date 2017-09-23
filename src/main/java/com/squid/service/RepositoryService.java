package com.squid.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.squid.data.ImageRepository;
import com.squid.data.ImageTopologyRepository;
import com.squid.data.PageRepository;
import com.squid.data.PageTopologyRepository;
import com.squid.data.QueryRepository;

/**
 * Helper class to provide access to Spring autowired repository objects. This helper class
 * is needed when attempting to access Spring objects from a non-spring thread class
 * @author Datim
 *
 */
@Service
public class RepositoryService {

	@Autowired
	private PageRepository pageRepo;

	@Autowired
	private ImageRepository imageRepo;

	@Autowired
	private PageTopologyRepository pageTopologyRepo;

	@Autowired
	private ImageTopologyRepository imageTopoRepo;

	@Autowired
	private QueryStatusService queryStatus;

	@Autowired
	private QueryRepository queryRepo;

	public PageRepository getPageRepo() {
		return pageRepo;
	}

	public ImageRepository getImageRepo() {
		return imageRepo;
	}

	public PageTopologyRepository getPageTopologyRepo() {
		return pageTopologyRepo;
	}

	public QueryStatusService getQueryStatus() {
		return queryStatus;
	}

	public ImageTopologyRepository getImageTopologyRepo() {
		return imageTopoRepo;
	}

	public QueryRepository getQueryRepo() {
		return queryRepo;
	}
}
