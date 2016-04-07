package com.squid.service.crawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	
	static final String CSS_SUFFIX = ".css";
	static int MAX_IMAGES = 350;
	static int MAX_DEPTH = 50;
	
	int count = 0;
	
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
				
		discoverContent(huntUrl, imageList, vistedURLs);
		
		// reset count
		count = 0;
		
        // return the list as a string
        return createHtmlBody(imageList);
	}
	
	/**
	 * Recursively find all photos in a series of links
	 * @param huntUrl
	 * @return
	 * @throws IOException 
	 */
	private void discoverContent(final URL huntUrl, Set<String> imageList, Set<URL> vistedURLs) throws IOException {
		
		System.out.println("Discover content loop " + count++);
		
		if (count >= MAX_DEPTH) {
			// reached max depth, return
			return;
		}
		
		Document doc = Jsoup.connect(huntUrl.toString()).get();
		
		String base = huntUrl.getProtocol() + "://" + huntUrl.getHost();

		// find all photos on this page
		parsePhotos(doc, imageList, base);
		
		// find all link references on this page
		List<URL> subRefs = discoverLinks(doc, huntUrl, vistedURLs);
		
		for (URL url: subRefs) {
			discoverContent(url, imageList, vistedURLs);
			
			// don't get more than max images
			if (imageList.size() > MAX_IMAGES) {
				break;
			}
		}
		
		return;
	}
	
	/**
	 * Discover all URLs associated with the page
	 * @param Document
	 * @return
	 */
	private List<URL> discoverLinks(final Document doc, URL parentURL, Set<URL> vistedUrls) {
		
		List<URL> urlList = new ArrayList<>();
		
		Elements urlElements = doc.select("a[href]");
		
		String parentHost = parentURL.getHost();
		
		for (Element urlElement: urlElements) {
			try {
				String urlString = urlElement.attr("abs:href");
				
				// ignore style sheets
				if (urlString.endsWith(CSS_SUFFIX)) {
					continue;
				}
				
				URL childUrl = new URL(urlString);
				
				// ignore links that don't have the same base as the parent
				if (!childUrl.getHost().equals(parentHost)) {
					//System.out.println("Ignore off-site url " + childUrl);
					String x = childUrl.getHost();
					continue;
				}
				
				// don't re-vist URLs
				if (vistedUrls.contains(childUrl)) {
					// link was already visited
					//System.out.println("URL " + childUrl + " has already been traversed");
					continue;
					
				} else {
					// mark the url as visted so we don't see it again
					System.out.println("found new URL: " + urlString);
					vistedUrls.add(childUrl);
					urlList.add(new URL(urlString));
				}
								
			} catch (MalformedURLException e) {
				System.out.println("URL is malformed: " + urlElement.attr("abs:href"));
				continue;
			}
		}
		
		return urlList;
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
