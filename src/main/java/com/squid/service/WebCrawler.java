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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.squid.config.SquidConstants;
import com.squid.config.SquidProperties;
import com.squid.data.NodeData;
import com.squid.data.NodeDataRepository;
import com.squid.data.PhotoData;
import com.squid.data.PhotoDataRepository;
import com.squid.data.SearchStatusData;
import com.squid.data.SearchStatusRepository;
import com.squid.search.SearchNodes;

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

	
	/**
	 * Start the crawl through a tree of pages starting with the base url
	 * @param huntUrl
	 * @throws IOException
	 */
	public void startCrawl(final URL baseUrl) throws IOException {
		
		// begin a search in a new thread and return
		final SearchNodes searchThread = new SearchNodes(baseUrl, photoRepo, nodeRepo, searchStatusRepo, squidProps.getMaxImages(), squidProps.getMaxNodes());
		searchThread.start();
	}

	/**
	 * Retrieve stored list of photos
	 */
	public List<PhotoData> getPhotos(int pageNum, int pageSize) {
		//Page<PhotoData> photos = photoRepo.findAll(new PageRequest(pageNum, pageSize));	
		//return photos.getContent();
		return (List<PhotoData>) photoRepo.findAll(new Sort(Sort.Direction.ASC, "id"));
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
	 */
	public PhotoData savePhoto(PhotoData photo) throws IOException {
		
		// get download directory
		Path downloadDirPath = SquidConstants.getDownloadDirectory();
		
		// create it if it doesn't exist
		final File downloadDir = new File(downloadDirPath.toString());
		
		if (!downloadDir.exists()) {
			downloadDir.mkdirs();
		}
		
		// construct the path to the file
		final Path downloadFilePath = Paths.get(downloadDirPath.toString(), photo.getName()); 
		
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
	 * Return the last search status
	 * @return
	 */
	public SearchStatusData getSearchStatus(String url) {
		// it is expected that there will only be one record
		return searchStatusRepo.findByUrl(url);
	}
}
