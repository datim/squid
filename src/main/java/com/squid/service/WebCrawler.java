package com.squid.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.squid.config.SquidProperties;
import com.squid.data.NodeData;
import com.squid.data.NodeDataRepository;
import com.squid.data.PhotoData;
import com.squid.data.PhotoDataRepository;
import com.squid.data.SearchStatusData;
import com.squid.data.SearchStatusRepository;
import com.squid.search.PageSearchRequest;
import com.squid.search.SearchExecutor;
import com.squid.search.SearchStatusService;

import javassist.NotFoundException;

/**
 * TODO: Breadth first search instead of depth-first search
 */
@Service
public class WebCrawler {
	
	static Logger log = Logger.getLogger(WebCrawler.class.getName());
	
	@Autowired
	private SquidProperties squidProps;

	@Autowired
	private PhotoDataRepository photoRepo;
	
	@Autowired 
	private NodeDataRepository nodeRepo;
	
	@Autowired
	private SearchStatusRepository searchStatusRepo;

	@Autowired
	private UserParameterService userParamService;
	
	@Autowired
	private SearchStatusService searchStatus;
	
	private SearchExecutor delgator;
	
	/**
	 * After the service starts, launch the listening thread
	 * that will execute page searches
	 */
	@PostConstruct
	public void startThreadPolling() {
		
		// create a new search delgator
		delgator = new SearchExecutor(photoRepo, nodeRepo, searchStatusRepo, squidProps.getMaxImages(), squidProps.getMaxNodes());
		delgator.start();
	}
	
	/**
	 * Start the crawl through a tree of pages starting with the base url
	 * @param huntUrl
	 * @throws IOException
	 */
	public void startCrawl(final URL baseUrl) throws IOException {
		
		// save search parameter for UI feedback
		userParamService.setUserSearchString(UserParameterService.DEFAULT_USER_ID, baseUrl.toString());
		
		// initialize the search status
		searchStatus.updateSearchStatus(new Long(0), new Long(0), squidProps.getMaxNodes(), baseUrl, 
										SearchStatusData.SearchStatus.NoResults);
		
		try {
			
			// push a new search request onto the processing queue
			delgator.getPageRequestsQueue().put(new PageSearchRequest(baseUrl));
			
		} catch (InterruptedException e) {
			log.severe("Unable to invoke a search for page: " + baseUrl + ". Exception: " + e);
		}

		/*
		// begin a search in a new thread and return
		final SearchNodes searchThread = new ParseNodeThread(baseUrl, photoRepo, nodeRepo, searchStatusRepo, squidProps.getMaxImages(), squidProps.getMaxNodes());
		searchThread.start();
		*/
	}

	/**
	 * Retrieve stored list of photos. Provide an optional filter term, which can be an empty string
	 */
	public List<PhotoData> getPhotos(String filter) {
		if (filter.isEmpty()) {
			return getAllPhotos();
			
		} else {
			// save filter and return photos
			return getPhotosWithFilter(filter);
		}
	}
	
	/**
	 * Query all photos
	 */
	public List<PhotoData> getAllPhotos() {
		return photoRepo.findAll(new Sort(Sort.Direction.ASC, "id"));
	}
	
	/**
	 * Query photos with a filter
	 */
	public List<PhotoData> getPhotosWithFilter(String filter) {
		log.info("Requesting photos with filter '" + filter + "'");
		
		
		List<PhotoData> results = photoRepo.findFilteredPhotos(filter.toLowerCase());
		log.info("found " + results.size() + " results");
		
		return photoRepo.findFilteredPhotos(filter.toLowerCase());
	}

	/**
	 * Retrieve the number of discovered photos
	 */
	public long getPhotosCount() {
		return photoRepo.count();
	}
	
	/**
	 * Retrieve all discovered nodes
	 */
	public List<NodeData> getNodes() {
		return (List<NodeData>) nodeRepo.findAll();
	}

	/**
	 * Retrieve the number of discovered nodes
	 */
	public long getNodeCount() {
		return nodeRepo.count();
	}

	/**
	 * Delete all photos
	 */
	public void deletePhotos() {
		log.info("Erasing all photos");
		
		List<PhotoData> photos = (List<PhotoData>) photoRepo.findAll();
		
		for (PhotoData p: photos) {
			photoRepo.delete(p);
		}
	}
	
	/**
	 *  Delete All nodes
	 */
	public void deleteNodes() {
		log.info("Erasing all nodes");
		
		List<NodeData> nodes = (List<NodeData>) nodeRepo.findAll();
		for (NodeData n: nodes) {
			nodeRepo.delete(n);
		}
	}
	
	/**
	 * Download a Photo to the default directory. Overwrite photo if it exists
	 * @param Download a photo to the default directory. Save the updated photo
	 * @throws IOException
	 * @throws NotFoundException 
	 */
	public PhotoData savePhoto(long photoId) throws IOException, NotFoundException {
		
		// get download path
		Path downloadDirPath = squidProps.getDownloadDirectory();
		
		// check the download path and download if needed
		checkAndCreateDownloadDirectory(downloadDirPath);

		// get photo by id
		final PhotoData photo = photoRepo.findById(photoId);
		
		if (photo == null) {
			throw new NotFoundException(Long.toString(photoId));
		}
		
		// construct the path to the file
		final Path downloadFilePath = Paths.get(downloadDirPath.toString(), photo.getName()); 
		
		log.info("Downloading photo " + photo.getUrl() + " from url: " + photo.getNodeUrl());
		
		// download the picture
		try (InputStream in = photo.getUrl().openStream()) {
		    Files.copy(in, downloadFilePath, StandardCopyOption.REPLACE_EXISTING);
		}
		
		// photo was saved, update its status in the database
		photo.setSaved(true);
		
		// return the updated record
		return photoRepo.save(photo);
	}
	
	/**
	 * Create the download directory if it doesn't exist
	 */
	private void checkAndCreateDownloadDirectory(final Path downloadDirPath) {
		
		// get download directory
		
		// create it if it doesn't exist
		final File downloadDir = new File(downloadDirPath.toString());
		
		if (!downloadDir.exists()) {
			downloadDir.mkdirs();
		}
	}
	
	/**
	 * Return the last search status
	 * @return
	 */
	public SearchStatusData getSearchStatus(String url) {
		// it is expected that there will only be one record
		
		// TODO - remove counts from status
		SearchStatusData data = searchStatusRepo.findByUrl(url);
		
		if (data != null) {
			data.setImageCount(photoRepo.count());
			data.setNodeCount(nodeRepo.count());
		}

		return searchStatusRepo.findByUrl(url);
	}
}
