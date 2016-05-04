package com.squid.service.crawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
	
	private List<String> suffixExclusions = null;	
	private int vistedNodes = 0;
	
	@PostConstruct
	private void setup() {
		// create suffix exclusion list
		suffixExclusions = new ArrayList<>();
		suffixExclusions.add("css");
		suffixExclusions.add("pdf");
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
		final Queue<URL> toVisitUrls = new LinkedList<>();
			
		// reset the number of visited nodes before recursively searching
		vistedNodes = 0;
		
		// clean out the photo and node repositories before beginning
		photoRepo.deleteAll();
		nodeRepo.deleteAll();
		
		discoverContent(huntUrl, null, imageList, toVisitUrls, vistedURLs);
	}
	
	/**
	 * Start a recursive, breadth-first search in a new thread
	 * @param huntUrl
	 * @throws IOException 
	 */
	public void startSearch(URL huntUrl) throws IOException {
		final Set<String> imageList = new HashSet<>();
		final Set<URL> vistedURLs = new HashSet<>();
		final Queue<URL> toVisitUrls = new LinkedList<>();
			
		// reset the number of visited nodes before recursively searching
		vistedNodes = 0;
		
		discoverContent(huntUrl, null, imageList, toVisitUrls, vistedURLs);		
	}
	
	/**
	 * Recursively find all photos in a series of links. Perform a breadth-first search
	 * @param huntUrl
	 * @return
	 * @throws IOException 
	 */
	private void discoverContent(final URL huntUrl, final NodeData parentNode, Set<String> imageList, Queue<URL> toVisitUrls, Set<URL> vistedURLs) throws IOException {
		
		log.info("Discovered content loop " + vistedNodes++);
		
		// don't exceed the max number of nodes
		if (vistedNodes >= MAX_NODES) {
			log.info("Reached maximum visited nodes");
			return;
		}
		
		final Document huntUrlDoc = Jsoup.connect(huntUrl.toString()).get();
		
		final String baseUrl = huntUrl.getProtocol() + "://" + huntUrl.getHost();
		
		// create a URL node record
		NodeData node = new NodeData();
		node.setUrl(huntUrl);
		node.setParent(parentNode);
		node.setVisited(true);
		
		// save the node
		node = nodeRepo.save(node);
		
		// find all photos associated with this URL
		discoverPhotosAssociatedWithURL(huntUrlDoc, imageList, baseUrl);
		
		// find all links referenced by this URL
		discoverLinksAssociatedWithURL(huntUrlDoc, huntUrl, toVisitUrls, vistedURLs);
		
		while (!toVisitUrls.isEmpty()) {
			
			final URL childUrl = toVisitUrls.remove();
			
			if (childUrl == node.getUrl()) {
				// don't traverse the same URL twice
			}
			discoverContent(childUrl, node, imageList, toVisitUrls, vistedURLs);
			
			// check gate parameters
			if (imageList.size() > MAX_IMAGES || vistedNodes >= MAX_NODES) {
				break;
			}
		}
	}
	
	/**
	 * Discover all links associated with a URL
	 * @param Document
	 * @return
	 */
	private void discoverLinksAssociatedWithURL(final Document doc, URL parentURL, Queue<URL> toVistUrls, Set<URL> vistedUrls) {
				
		final Elements urlElements = doc.select("a[href]");
		final String parentHost = parentURL.getHost();
		
		// iterate through all URLS
		for (Element urlElement: urlElements) {

			final String urlString = urlElement.attr("abs:href");
			boolean notHtmlPage = false;
			
			// we only want HTML pages
			for (String suffix: suffixExclusions) {
				
				if (urlString.endsWith(suffix)) {
					notHtmlPage = true;
					continue;
				} 
			}
			
			// if this is not an HTML page, ignore it
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
				
			// don't visit URLs twice
			if (vistedUrls.contains(childUrl)) {
				continue;
			}
			
			// its' ok to add this URL to the queue
			log.info("Found new URL: " + urlString);
			System.out.println("found new URL: " + urlString);
			toVistUrls.add(childUrl);
			
			// make sure we don't visit this url again
			vistedUrls.add(childUrl);
		}
	}
	
	/**
	 * Parse photos from an HTML document
	 * @param doc
	 * @return
	 */
	private void discoverPhotosAssociatedWithURL(final Document doc, Set<String> imageList, String baseUrl) {
        
		final Elements images = doc.select("img");
        
        for (Element image: images) {
        	String source = image.attr("src");
        	
        	// ignore spacer
        	if (source.endsWith("spacer.gif")) {
        		continue;
        	}
        	
        	final String imgUrl = image.attr("src");
        	
        	// found a photo. save it
        	PhotoData photo = new PhotoData();
        	photo.setName(imgUrl);
        	photo.setSize(59);
        	
        	try {
            	if (imgUrl.startsWith("http")) {
            		// image is already a well-formed URL
            		imageList.add(imgUrl);
            		photo.setUrl(new URL(imgUrl));
            		
            	} else {
            		// partial image. Add host
            		String completeUrl = baseUrl + image.attr("src");
                	imageList.add(completeUrl);
                	photo.setUrl(new URL(completeUrl));
            	}
        	} catch (MalformedURLException e) {
        		System.out.println("Invalid photo url " + imgUrl);
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
	@SuppressWarnings("unchecked")
	public List<PhotoData> getPhotos(int pageNum, int pageSize) {
		Page<PhotoData> photos = photoRepo.findAll(new PageRequest(pageNum, pageSize));		
		return photos.getContent();
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
}
