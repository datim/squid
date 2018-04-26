package com.squid.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.squid.config.SquidProperties;
import com.squid.controller.exceptions.ResourceNotFoundException;
import com.squid.data.FoundPage;
import com.squid.data.Image;
import com.squid.data.ImageTopology;
import com.squid.data.ImageTopologyRepository;
import com.squid.data.PageTopology;
import com.squid.data.PageTopologyRepository;
import com.squid.data.Query;
import com.squid.data.QueryRepository;
import com.squid.data.old.PhotoData;
import com.squid.data.old.PhotoDataRepository;
import com.squid.engine.MessageEngine;
import com.squid.engine.requests.PageRequestMsg;

import javassist.NotFoundException;

/**
 * TODO: Breadth first search instead of depth-first search
 * FIXME: Define max images and max pages through API
 */
@Service
public class SearchService {


	@Autowired
	private SquidProperties squidProps;

	@Autowired
	private PhotoDataRepository photoRepo;

    private static final Logger log = LoggerFactory.getLogger(SearchService.class);

	@Autowired
	private QueryRepository queryRepo;

	@Autowired
	private RepositoryService repoService;

	@Autowired
	private PageTopologyRepository pageTopoRepo;

	@Autowired
	private ImageTopologyRepository imageTopoRepo;

	@Autowired
	private QueryStatusService queryStatus;

	private MessageEngine pEngine;


	/**
	 * After the service starts, launch the listening thread
	 * that will execute page searches
	 */
	@PostConstruct
	public void init() {

		// start the search engine
		final int THREADPOOLSIZE = 10;	// FIXME, make configurable
		pEngine = new MessageEngine("PageSearch", THREADPOOLSIZE, repoService);
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
	 * Remove the previous contents of a search
	 * @param id
	 */
	private void flushSearchResults(long id) {
	    final long pageCount = pageTopoRepo.deleteByQuery(id);
	    final long imageCount = imageTopoRepo.deleteByQuery(id);
	    log.info("Flushed {} pages and {} images in query history", pageCount, imageCount);
	}

	/**
	 * Start the crawl through a tree of pages starting with the base url
	 * @param huntUrl
	 * @throws IOException
	 * @throws InterruptedException
	 * @return id of query running search
	 */
	public long startSearch(final URL baseUrl) throws IOException {

		// get or create a new query object
		final Query query = getOrCreateQuery(baseUrl, squidProps.getMaxNodes(), squidProps.getMaxImages());

		// set the query status
		queryStatus.setQueryStatusRunning(query);

		// remove any existing topology information
		//flushSearchResults(query.getId()); // FIXME DELETE

		try {
			pEngine.addRequest(new PageRequestMsg(query, baseUrl));

		} catch (final InterruptedException e) {
			log.error("Unable to invoke a search for url {}. Exception. {}", baseUrl, e);
		}

		return query.getId();
	}

	/**
	 * Get all available queries
	 * @return
	 */
	public List<Query> getQueries() {
		return queryRepo.findAll();
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
	 * Download a Photo to the default directory. Overwrite photo if it exists
	 * @param Download a photo to the default directory. Save the updated photo
	 * @throws IOException
	 * @throws NotFoundException
	 */
	@Deprecated
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
	 * Return the status of a query
	 * @param queryId the query status to look up
	 * @return The status string
	 */
	public String getQueryStatus(long queryId) {

		final Query query = queryRepo.findById(queryId);

		if (query == null) {
			log.error("Unable to find status. Query with id {} doesn't exist", queryId);
			throw new ResourceNotFoundException("Query");
		}

		return queryStatus.getStatus(query);
	}

	/**
	 * Find a query by id
	 */
	public Query getQuery(long queryId) {
		return queryRepo.findById(queryId);
	}

	/**
	 * Image counts for a query
	 * @param queryId
	 * @return image count for a query
	 */
	public long getQueryImageCount(long queryId) {
		return imageTopoRepo.findByQuery(queryId).size();
	}

	/**
	 * Page counts for a query
	 * @param queryId
	 * @return page count for query
	 */
	public long getQueryPageCount(long queryId) {
		return pageTopoRepo.findByQuery(queryId).size();
	}

	/**
	 * Get the list of pages associated with a query
	 * @param queryId the query to report images for
	 * @return a list of pages associated with a query
	 */
	public List<FoundPage> getQueryPages(long queryId) {

		final List<PageTopology> pagesByQuery = pageTopoRepo.findByQuery(queryId);
		final List<FoundPage> pages = new ArrayList<>(pagesByQuery.size());

		for (final PageTopology topPage: pagesByQuery) {
			pages.add(repoService.getPageRepo().findById(topPage.getPage()));
		}

		return pages;
	}

	/**
	 * Get a list of images associated with the query
	 * @param queryId the query to report images for
	 * @return a list of images associated with a query
	 * @throws NotFoundException
	 */
	public Page<Image> getQueryImages(long queryId, Pageable pageable) throws NotFoundException {

		final List<ImageTopology> imagePageByQuery = imageTopoRepo.findByQuery(queryId);

		if ((imagePageByQuery == null) || imagePageByQuery.isEmpty()) {
			log.warn("No images for query id [{}]", queryId);
			throw new NotFoundException("Could not find images for query id '" + queryId + "'");
		}

		// fetch only the ids from the image queries
		// grab only the image ids for each imagePage topology record
		final List<Long> imageIds = imagePageByQuery.stream()
				.map(imgQuery -> imgQuery.getImageId()).collect(Collectors.toList());

		// find all images that map to the requested image queries
		final Page<Image> images = repoService.getImageRepo().findByIdIn(imageIds, pageable);
		return images;
	}

	/**
	 * Stop a search for an existing query
	 * @param queryId
	 * @return The query that was stopped
	 * @throws NotFoundException
	 */
	public Query stopSearch(long queryId) throws NotFoundException {

		// fetch query. Throw exception if it can't be found
		final Query query = queryRepo.findById(queryId);

		if (query == null) {
			log.error("Unable to stop query with id '{}'. Id does not exist", queryId);
			throw new NotFoundException("Query with id '" + queryId + "' doesn't exist");
		}

		// stop query and return it
		queryStatus.setStop(query);
		log.info("Query '[{}]{}' stopped", query.getId(), query.getUrl());
		return query;
	}
}
