package com.squid.service.crawler;

import java.net.URI;
import java.net.URL;

import org.springframework.stereotype.Service;

@Service
public class WebCrawler {
	
	// crawl a URL
	/**
	 * Web crawl a URL
	 * @param huntUrl
	 * @return
	 */
	public String startCrawl(final URL huntUrl) {
		
		return "Starting to crawl";
	}

}
