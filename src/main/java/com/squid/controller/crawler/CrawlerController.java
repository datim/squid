package com.squid.controller.crawler;

import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.squid.service.crawler.WebCrawler;

@RestController
@RequestMapping("/crawl")
public class CrawlerController {
	
	@Autowired 
	private WebCrawler crawler;

    @RequestMapping("/go")
    public String index() {
    	URL huntUrl;
    	String output = null;
		try {
			huntUrl = new URL("http://www.stampinup.com/ECWeb/ItemList.aspx?categoryid=102401");
	    	output = crawler.startCrawl(huntUrl);

		} catch (MalformedURLException e) {
			
			// TODO Auto-generated catch block
			//return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			//e.printStackTrace();
		}
		return output;
    }
}
