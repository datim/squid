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

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.squid.config.SquidProperties;
import com.squid.data.Query;
import com.squid.data.QueryRepository;
import com.squid.data.old.NodeData;
import com.squid.data.old.NodeDataRepository;
import com.squid.data.old.PhotoData;
import com.squid.data.old.PhotoDataRepository;
import com.squid.data.old.SearchStatusData;
import com.squid.data.old.SearchStatusRepository;
import com.squid.engine.PageEngine;
import com.squid.engine.RepositoryService;
import com.squid.engine.old.PageSearchRequest;
import com.squid.engine.old.SearchConstants;
import com.squid.engine.old.SearchExecutor;
import com.squid.engine.requests.PageRequestMsg;
import com.squid.parser.old.UserParameterService;

import javassist.NotFoundException;

/**
 * TODO: Breadth first search instead of depth-first search
 */
@Service
public class SearchService {


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

	private SearchExecutor searchListener;

	// -- START of properties to keep

    private static final Logger log = LoggerFactory.getLogger(SearchService.class);

	@Autowired
	private QueryRepository queryRepo;

	@Autowired
	private RepositoryService repoService;

	private PageEngine pEngine;


	/**
	 * After the service starts, launch the listening thread
	 * that will execute page searches
	 */
	@PostConstruct
	public void startThreadPolling() {

		// - FIXME DELETE
		// create a new search listener
		searchListener = new SearchExecutor(photoRepo, nodeRepo, searchStatusRepo, squidProps.getMaxImages(), squidProps.getMaxNodes());
		searchListener.start();

		// - FIXME DELETE


		final int THREADPOOLSIZE = 10;	// FIXME, make configurable
		pEngine = new PageEngine("PageSearch", THREADPOOLSIZE, repoService);
		pEngine.start();
	}

	/**
	 * Get an existing query object or create a new query object
	 * @param url The URL to crawl
	 * @param maxPages The max number of sub pages to crawl for this URL
	 * @param maxImages The max number of images to crawl for this URL
	 * @return The new or existing Query object
	 */
	private Query getOrCreateQuery(final URL url, int maxPages, int maxImages) {

		Query query = queryRepo.findByUrl(url);

		if (query == null) {
			// create new query
			log.debug("Creating new query for URL {}", url);

			// FIXME Use user requested parameters
			query = queryRepo.save(new Query(url, maxPages, maxImages));

		} else {
			log.debug("Using existing query for URL {} with id {}", query.getUrl().toString(), query.getId());
		}

		return query;
	}

	/**
	 * Start the crawl through a tree of pages starting with the base url
	 * @param huntUrl
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public boolean startSearch(final URL baseUrl) throws IOException {

		boolean success = true;

		// get or create a new query object
		final Query query = getOrCreateQuery(baseUrl, squidProps.getMaxNodes(), squidProps.getMaxImages());

		try {
			pEngine.addRequest(new PageRequestMsg(query, baseUrl));

		} catch (final InterruptedException e) {
			log.error("Unable to invoke a search for url {}. Exception. {}", baseUrl, e);
			success = false;
		}

		// FIXME - delete this
		// update user search parameters
		userParamService.setUserSearchString(UserParameterService.DEFAULT_USER_ID, baseUrl.toString());

		// initialize the status for a new search
		SearchConstants.setSearchStatus(baseUrl, new Long(0), new Long(0), new Long(squidProps.getMaxNodes()), SearchStatusData.SearchStatus.NoResults, searchStatusRepo);

		try {
			// submit a new request to be searched
			searchListener.getPageRequestsQueue().put(new PageSearchRequest(baseUrl, baseUrl, null));

		} catch (final InterruptedException e) {
			// submission request failed. Throw an error
			log.error("Unable to invoke a search for page: {}. Exception {}", baseUrl, e);
			success = false;
		}

		// FIXME end delete this

		return success;
	}

	/**
	 * Get all available queries
	 * @return
	 */
	public List<Query> getQueries() {
		return queryRepo.findAll();
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


		final List<PhotoData> results = photoRepo.findFilteredPhotos(filter.toLowerCase());
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

		final List<PhotoData> photos = (List<PhotoData>) photoRepo.findAll();

		for (final PhotoData p: photos) {
			photoRepo.delete(p);
		}
	}

	/**
	 *  Delete All nodes
	 */
	public void deleteNodes() {
		log.info("Erasing all nodes");

		final List<NodeData> nodes = (List<NodeData>) nodeRepo.findAll();
		for (final NodeData n: nodes) {
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
		final Path downloadDirPath = squidProps.getDownloadDirectory();

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
		return searchStatusRepo.findByUrl(url);
	}


}
