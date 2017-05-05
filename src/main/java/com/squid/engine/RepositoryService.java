package com.squid.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.squid.data.ImageRepository;
import com.squid.data.PageRepository;

/**
 * Helper class to provide access to Spring autowired objects
 * @author Datim
 *
 */
@Service
public class RepositoryService {

	@Autowired
	private PageRepository pageRepo;

	@Autowired
	private ImageRepository imageRepo;


	public PageRepository getPageRepo() {
		return pageRepo;
	}

	public void setPageRepo(PageRepository pageRepo) {
		this.pageRepo = pageRepo;
	}
}
