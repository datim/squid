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

import javax.annotation.PostConstruct;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

/**
 * TODO: Breadth first search instead of depth-first search
 */
@Service
public class WebCrawler {
	
	private List<String> suffixExclusions = null;
	private List<String> pageExclusions = null;
	static int MAX_IMAGES = 350;
	static int MAX_DEPTH = 50;
	
	int count = 0;
	
	@PostConstruct
	private void setup() {
		// create suffix exclusion list
		suffixExclusions = new ArrayList<>();
		suffixExclusions.add("css");
		suffixExclusions.add("pdf");
	}
	
	// crawl a URL
	/**
	 * Web crawl a URL
	 * @param huntUrl
	 * @return
	 * @throws IOException 
	 */
	public String startCrawl(final URL huntUrl) throws IOException {
		
		Set<String> imageList = new HashSet<>();
		Set<URL> vistedURLs = new HashSet<>();
		Queue<URL> toVisitUrls = new LinkedList<>();
				
		discoverContent(huntUrl, imageList, toVisitUrls, vistedURLs);
		
		// reset count
		count = 0;
		
        // return the list as a string
        return createHtmlBody(imageList);
	}
	
	/**
	 * Recursively find all photos in a series of links. Perform a breadth-first search
	 * @param huntUrl
	 * @return
	 * @throws IOException 
	 */
	private void discoverContent(final URL huntUrl, Set<String> imageList, Queue<URL> toVisitUrls, Set<URL> vistedURLs) throws IOException {
		
		System.out.println("Discover content loop " + count++);
		
		if (count >= MAX_DEPTH) {
			return;
		}
		
		Document doc = Jsoup.connect(huntUrl.toString()).get();
		
		String base = huntUrl.getProtocol() + "://" + huntUrl.getHost();

		// find all photos on this page
		parsePhotos(doc, imageList, base);
		
		// find all link references on this page
		discoverLinks(doc, huntUrl, toVisitUrls, vistedURLs);
		
		while (!toVisitUrls.isEmpty()) {
			
			final URL childUrl = toVisitUrls.remove();
			discoverContent(childUrl, imageList, toVisitUrls, vistedURLs);
			
			// check gate parameters
			if (imageList.size() > MAX_IMAGES || count >= MAX_DEPTH) {
				break;
			}

		}
	}
	
	/**
	 * Discover all URLs associated with the page
	 * @param Document
	 * @return
	 */
	private void discoverLinks(final Document doc, URL parentURL, Queue<URL> toVistUrls, Set<URL> vistedUrls) {
				
		Elements urlElements = doc.select("a[href]");
		
		final String parentHost = parentURL.getHost();
		
		for (Element urlElement: urlElements) {

			String urlString = urlElement.attr("abs:href");
			
			boolean notHtmlPage = false;
			// we onl want HTML pages
			for (String suffix: suffixExclusions) {
				if (urlString.endsWith(suffix)) {
					notHtmlPage = true;
					continue;
				} 
			}
			
			if (notHtmlPage) {
				continue;
			}
			
			URL childUrl;
			
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
			System.out.println("found new URL: " + urlString);
			toVistUrls.add(childUrl);
			
			// make sure we don't visit this url again
			vistedUrls.add(childUrl);
					
		}
	}
	
	/**
	 * Parse photos from the HTML page
	 * @param doc
	 * @return
	 */
	private void parsePhotos(final Document doc, Set<String> imageList, String baseUrl) {
        Elements images = doc.select("img");
        
        for (Element image: images) {
        	String source = image.attr("src");
        	
        	// ignore spacer
        	if (source.endsWith("spacer.gif")) {
        		continue;
        	}
        	
        	String imgUrl = image.attr("src");
        	
        	if (imgUrl.startsWith("http")) {
        		// image is already a well-formed URL
        		imageList.add(imgUrl);
        		
        	} else {
        		// partial image. Add host
            	imageList.add(baseUrl + image.attr("src"));
        	}
        }
        
        return;
	}

	/**
	 * Temporary function to return all images from a page as an HTML document
	 * @param imageList
	 * @param urlBase
	 * @return
	 */
	private String createHtmlBody(Set<String> imageList) {
		
		String rt = "\n";
		
		String html = new String();
		
		html += "<html xmlns=\"http://www.w3.org/1999/xhtml\">" + rt;
		
		html += "<body>" + rt;
		
		html += "<p> " + imageList.size() + " images found. </p>" + rt;

		for (String imageName: imageList) {
			html += "<div>" + rt;
			html += "<img src=" + imageName + ">" + rt;
			html += "<p>" + imageName + "</p>" + rt;
			html += "</div>" + rt;
		}
		
		/*

		html += "<ul>" + rt;
		// add each image as a list item
		for (String imageName: imageList) {
			html += "<li style=\"list-style-type:none\"><img src=" + urlBase + imageName + "></li>" + rt;
		}
		
		html += "</ul>" + rt;
		*/
	
		html += "</body>" + rt;
		html += "</html>" + rt;
		
		return html;
		
	}
}
