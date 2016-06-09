package com.squid.service.crawler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.squid.controller.rest.PhotoDTO;
import com.squid.data.NodeData;
import com.squid.data.NodeDataRepository;
import com.squid.data.PhotoData;
import com.squid.data.PhotoDataRepository;

/**
 * TODO: Breadth first search instead of depth-first search
 */
@Service
public class WebCrawler {
	
	static Logger log = Logger.getLogger(WebCrawler.class.getName());

	@Autowired
	private PhotoDataRepository photoRepo;
	
	@Autowired 
	private NodeDataRepository nodeRepo;

	static int MAX_IMAGES = 350;
	static int MAX_NODES = 50;
	static String URL_INLINE_TAG = "#";
	static String URL_SEARCH_TAG = "?";
	
	private List<String> nodeSuffixExclusions = null;	
	private int vistedNodes = 0;
	
	/**
	 * Private class to store information for nodes that
	 * have not yet been traversed
	 */
	private class PendingNode {
		
		// constructor
		public PendingNode(final URL url, final URL parentURL) {
			this.url = url;
			this.parentUrl = parentURL;
		}
		
		public URL url;
		public URL parentUrl;
	}
	
	@PostConstruct
	private void setup() {
		// create suffix exclusion list
		nodeSuffixExclusions = new ArrayList<>();
		nodeSuffixExclusions.add("css");
		nodeSuffixExclusions.add("pdf");
	}
		
	/**
	 * Start a recursive, breadth-first search
	 * @param huntUrl
	 * @return
	 * @throws IOException 
	 */
	public void startCrawl(final URL huntUrl) throws IOException {
		
		final Set<String> imageList = new HashSet<>();
		final Set<URL> vistedURLs = new HashSet<>();
		final Queue<PendingNode> toVisitUrls = new LinkedList<>();
			
		// reset the number of visited nodes before recursively searching
		vistedNodes = 0;
				
		final PendingNode node = new PendingNode(huntUrl, null);
		
		discoverContent(node, imageList, toVisitUrls, vistedURLs);
	}
	
	/**
	 * Recursively find all photos in a series of links. Perform a breadth-first search
	 * @param huntUrl
	 * @return
	 * @throws IOException 
	 */
	private void discoverContent(final PendingNode checkNode, Set<String> imageList, Queue<PendingNode> toVisitUrls, Set<URL> vistedURLs) throws IOException {
		
		log.info("Discovered content loop " + vistedNodes++);
		
		// don't exceed the max number of nodes
		if (vistedNodes >= MAX_NODES) {
			log.info("Reached maximum visited nodes");
			return;
		}
		
		final URL huntUrl = checkNode.url;
		
		Document huntUrlDoc = null;
		
		try {
			huntUrlDoc = Jsoup.connect(huntUrl.toString()).get();
		
		} catch (SocketTimeoutException e) {
			log.severe("Socket timeout attempting to connect to url " + huntUrl.toString());
		}
		
		final String baseUrl = huntUrl.getProtocol() + "://" + huntUrl.getHost();
		
		if (nodeRepo.findByUrl(huntUrl) == null) {
			
			// create a node record and save it
			NodeData node = new NodeData();
			node.setUrl(huntUrl);

			if (huntUrlDoc != null) {
				node.setVisited(true);
			}
			
			// set parent node, if one exists
			if (checkNode.parentUrl != null) {
				node.setParentUrl(checkNode.parentUrl);
			}
			
			log.fine("saving node, url: " + node.getUrl() + ", parent: " + node.getParent());
			
			// save the node
			node = nodeRepo.save(node);
			
			if (huntUrlDoc != null) {
				// find all photos associated with this URL
				discoverPhotosAssociatedWithURL(huntUrlDoc, imageList, baseUrl, huntUrl);
				
				// find all links referenced by this URL
				discoverSubNodes(huntUrlDoc, huntUrl, toVisitUrls, vistedURLs);
			}

			
		} else {
			log.info("Node " + huntUrl.toString() + " has previously been visited. Skipping");
		}
		
		while (!toVisitUrls.isEmpty()) {
			
			final PendingNode childNode = toVisitUrls.remove();
			
			discoverContent(childNode, imageList, toVisitUrls, vistedURLs);
			
			// check gate parameters
			if (imageList.size() > MAX_IMAGES || vistedNodes >= MAX_NODES) {
				break;
			}
		}
	}
	
	/**
	 * Discover all nodes associated with the current node
	 * @param Document
	 * @return
	 */
	private void discoverSubNodes(final Document doc, URL parentURL, Queue<PendingNode> toVistUrls, Set<URL> discoveredUrls) {
				
		final Elements urlElements = doc.select("a[href]");
		final String parentHost = parentURL.getHost();
		
		// find all the URL nodes on this page.
		for (Element urlElement: urlElements) {

			String urlString = urlElement.attr("abs:href");
			
			// Strip off in-page tags from URL
			if (urlString.contains(URL_INLINE_TAG)) {
				urlString = urlString.substring(0, urlString.indexOf(URL_INLINE_TAG));
			}			
			boolean notHtmlPage = false;
			
			// Exclude URLs with particular suffixes
			for (String suffix: nodeSuffixExclusions) {
				
				if (urlString.endsWith(suffix)) {
					notHtmlPage = true;
					continue;
				} 
			}
			
			if (notHtmlPage) {
				continue;
			}
			
			URL childUrl = null;
			
			try {
				childUrl = new URL(urlString);
				
			} catch (MalformedURLException e) {
				System.out.println("Malformed url for " + urlString);
				continue;
			}
			
			// ignore links that don't have the same base as the parent
			if (!childUrl.getHost().equals(parentHost)) {
				continue;
			}
				
			// don't visit a URL that has already been seen
			if (discoveredUrls.contains(childUrl)) {
				continue;
			}
			
			// add this URL to the queue to be visited
			log.info("Found new URL: " + urlString);
			System.out.println("found new URL: " + urlString);
			
			toVistUrls.add(new PendingNode(childUrl, parentURL));
			
			// record this URL as having been seen
			discoveredUrls.add(childUrl);
		}
	}
	
	/**
	 * Parse photos from an HTML document
	 * @param doc
	 * @return
	 */
	private void discoverPhotosAssociatedWithURL(final Document doc, Set<String> imageList, String baseUrl, URL nodeURL) {
        
		final Elements images = doc.select("img");
        
        for (Element image: images) {
        	String source = image.attr("src");
        	
        	// ignore spacer
        	if (source.endsWith("spacer.gif")) {
        		continue;
        	}
        	        	
			// Strip off search tags from URL
			if (source.contains(URL_SEARCH_TAG)) {
				source = source.substring(0, source.indexOf(URL_SEARCH_TAG));
			}	
        	        	
        	// found a photo. save it
        	PhotoData photo = new PhotoData();
        	
        	// 
        	try {
            	if (source.startsWith("http")) {
            		// image is already a well-formed URL
            		imageList.add(source);
            		photo.setUrl(new URL(source));
            		
            	} else {
            		// partial image. Add host
            		String completeUrl = baseUrl + image.attr("src");
                	imageList.add(completeUrl);
                	photo.setUrl(new URL(completeUrl));
            	}
        	} catch (MalformedURLException e) {
        		System.out.println("Invalid photo url " + source);
        		continue;
        	}
        	
        	photo.setName(source.substring(source.lastIndexOf("/") + 1));
        	photo.setNodeUrl(nodeURL);
        	photo.setBaseUrl(baseUrl);
        	
        	// don't save the photo if it has already been saved for this URL
        	if (photoRepo.findByNameAndBaseUrl(photo.getName(), photo.getBaseUrl()) != null) {
        		log.fine("Photo " + photo.getName() + " already discovered. Skipping");
        		continue;
        	}
   	
        	// save the photo
        	photoRepo.save(photo);
        }
        
        return;
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
}
