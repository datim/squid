package com.squid.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.squid.data.ImageRepository;
import com.squid.data.PageRepository;
import com.squid.data.PageTopologyRepository;

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


	public PageRepository getPageRepo() {
		return pageRepo;
	}

	public void setPageRepo(PageRepository pageRepo) {
		this.pageRepo = pageRepo;
	}

	public ImageRepository getImageRepo() {
		return imageRepo;
	}

	public void setImageRepo(ImageRepository imageRepo) {
		this.imageRepo = imageRepo;
	}

	public PageTopologyRepository getPageTopologyRepo() {
		return pageTopologyRepo;
	}

	public void setPageTopologyRepo(PageTopologyRepository pageTopologyRepo) {
		this.pageTopologyRepo = pageTopologyRepo;
	}
}
