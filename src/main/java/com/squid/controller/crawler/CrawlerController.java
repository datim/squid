package com.squid.controller.crawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.squid.controller.rest.PhotoDTO;
import com.squid.data.PhotoData;
import com.squid.service.crawler.WebCrawler;

@RestController
@RequestMapping("/crawl")
public class CrawlerController {
	
	@Autowired 
	private WebCrawler crawler;
	
	private String version = "1.0";

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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
    }
    
    /**
     * Obtain a list of all discovered photos
     */
    @RequestMapping(path="/photos", method = RequestMethod.GET)
    public List<PhotoDTO> getPhotos() {
    	
    	// retrieve a stored list of photos
    	final List<PhotoData> photos = crawler.getPhotos();
    	
    	final List<PhotoDTO> photoDTOs = new ArrayList<>(photos.size());
    	
    	// convert DAO to DTO
    	for (PhotoData dao: photos) {
    		PhotoDTO dto = new PhotoDTO();
    		dto.setId(dao.getId());
    		dto.setName(dao.getName());
    		dto.setUrl(dao.getUrl().toString());
    		
    		photoDTOs.add(dto);
    	}
    	
    	return photoDTOs;
    }
    
    /**
     * Obtain the number of discovered photos
     * @return
     */
    @RequestMapping(path="/photos/count", method = RequestMethod.GET)
    public long getPhotoCount() {
    	return crawler.getPhotosCount();
    }
    
    @RequestMapping(path="/version", method = RequestMethod.GET)
    public String getVersion() {
    	return version;
    }
}
