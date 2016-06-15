package com.squid.search;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Logger;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.squid.data.NodeData;
import com.squid.data.NodeDataRepository;
import com.squid.data.PhotoData;
import com.squid.data.PhotoDataRepository;
import com.squid.data.SearchStatusData;
import com.squid.data.SearchStatusRepository;

/**
 * Search nodes in a new thread
 *
 */
public class SearchNodes extends Thread {
		
	static Logger log = Logger.getLogger(SearchNodes.class.getName());

	static String URL_INLINE_TAG = "#";
	static String URL_SEARCH_TAG = "?";
	
	private List<String> nodeSuffixExclusions = null;	
	private int vistedNodes = 0;
	private URL searchUrl;
	private long maxNodes;
	private long maxImages;
	private PhotoDataRepository photoRepo;
	private NodeDataRepository nodeRepo;
	private SearchStatusRepository searchStatusRepo;
	private SearchStatusData searchStatus;
	
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
	
	/**
	 * Constructor
	 * @param huntUrl
	 * @param searchStatusRepo 
	 * @param nodeRepo 
	 * @param photoRepo 
	 */
	public SearchNodes(final URL huntUrl, final PhotoDataRepository photoRepoIn, final NodeDataRepository nodeRepoIn, 
					   final SearchStatusRepository inSearchRepo, long maxImages, long maxNodes) {
		this.searchUrl = huntUrl;
		this.vistedNodes = 0;
		this.photoRepo = photoRepoIn;
		this.nodeRepo = nodeRepoIn;
		this.searchStatusRepo = inSearchRepo;
		this.maxImages = maxImages;
		this.maxNodes = maxNodes;
		
		// create exclusions
		nodeSuffixExclusions = new ArrayList<>();
		nodeSuffixExclusions.add("css");
		nodeSuffixExclusions.add("pdf");
	}

	/**
	 * Execute a new thread
	 */
	@Override
	public void run() {
		try {
			startSearch(this.searchUrl);
		} catch (IOException e) {
			log.severe("An error occurred while searching sub-pages: " + e);
		}
	}
	
	/**
	 * Start a recursive, breadth-first search
	 * @param huntUrl
	 * @return
	 * @throws IOException 
	 */
	private void startSearch(final URL huntUrl) throws IOException {
		
		log.info("Starting search for page " + huntUrl);
		
		final Set<String> imageList = new HashSet<>();
		final Set<URL> vistedURLs = new HashSet<>();
		final Queue<PendingNode> toVisitUrls = new LinkedList<>();
		
		final PendingNode node = new PendingNode(huntUrl, null);
		
		// maintain one record. If the record already exists, update it
		searchStatus = searchStatusRepo.findByUrl(huntUrl.toString());
		
		if (searchStatus == null) {
			// record doesn't exist, create a new one
			searchStatus = new SearchStatusData();
		}

		searchStatus.setUrl(huntUrl.toString());
		searchStatus.setMaxDepth(maxNodes);
		searchStatus.setNodeCount(0);
		searchStatus.setStatus(SearchStatusData.SearchStatus.NoResults);
		
		searchStatus = searchStatusRepo.save(searchStatus);
		
		discoverContent(node, imageList, toVisitUrls, vistedURLs);
		
		// complete status
		searchStatus.setNodeCount(maxNodes);
		searchStatus.setStatus(SearchStatusData.SearchStatus.Complete);
		searchStatusRepo.save(searchStatus);
	}
	
	/**
	 * Recursively find all photos in a series of links. Perform a breadth-first search
	 * @param huntUrl
	 * @return
	 * @throws IOException 
	 */
	private void discoverContent(final PendingNode checkNode, Set<String> imageList, Queue<PendingNode> toVisitUrls, Set<URL> vistedURLs) throws IOException {
		
		// update status
		searchStatus.setNodeCount(nodeRepo.count());
		searchStatus.setStatus(SearchStatusData.SearchStatus.InProgress);
		searchStatusRepo.save(searchStatus);
		
		log.info("Discovered content loop " + vistedNodes++);
		
		// don't exceed the max number of nodes
		if (vistedNodes >= maxNodes) {
			log.info("Reached maximum visited nodes");
			return;
		}
		
		final URL huntUrl = checkNode.url;
		
		Document huntUrlDoc = null;
		
		try {
			huntUrlDoc = Jsoup.connect(huntUrl.toString()).get();
		
		} catch (SocketTimeoutException e) {
			log.severe("Socket timeout attempting to connect to url " + huntUrl.toString());

		} catch (HttpStatusException e) {
			log.severe("Unable to fetch URL: " + e);
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
			if (imageList.size() > maxImages || vistedNodes >= maxNodes) {
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

}
