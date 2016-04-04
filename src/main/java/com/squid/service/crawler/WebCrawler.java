package com.squid.service.crawler;

import java.io.IOException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class WebCrawler {
	
	// crawl a URL
	/**
	 * Web crawl a URL
	 * @param huntUrl
	 * @return
	 * @throws IOException 
	 */
	public String startCrawl(final URL huntUrl) throws IOException {
		
		Document doc = Jsoup.connect(huntUrl.toString()).get();

		List<String> imageList = parsePhotos(doc);
        
        // return the list as a string
        return createHtmlBody(imageList, "http://stampinup.com");
	}
	
	/**
	 * Parse photos from the HTML page
	 * @param doc
	 * @return
	 */
	private List<String> parsePhotos(final Document doc) {
        Elements images = doc.select("img");
        

        List<String> imageList = new ArrayList<>();
        for (Element image: images) {
        	String source = image.attr("src");
        	
        	// ignore spacer
        	if (source.endsWith("spacer.gif")) {
        		continue;
        	}
        	imageList.add(image.attr("src"));
        }
        
        return imageList;
	}

	/**
	 * Temporary function to return all images from a page as an HTML document
	 * @param imageList
	 * @param urlBase
	 * @return
	 */
	private String createHtmlBody(List<String> imageList, String urlBase) {
		
		String rt = "\n";
		
		String html = new String();
		
		html += "<html xmlns=\"http://www.w3.org/1999/xhtml\">" + rt;
		
		html += "<body>" + rt;

		
		for (String imageName: imageList) {
			html += "<div>" + rt;
			html += "<img src=" + urlBase + imageName + ">" + rt;
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
