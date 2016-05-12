package com.squid.controller.crawler;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.squid.controller.rest.NodeDTO;
import com.squid.controller.rest.PhotoDTO;
import com.squid.data.NodeData;
import com.squid.data.PhotoData;
import com.squid.service.crawler.WebCrawler;

@RestController
@RequestMapping("/crawl")
public class CrawlerController {
	
	static Logger log = Logger.getLogger(WebCrawler.class.getName());

	private String version = "0.1";
	private String urlString = "http://www.stampinup.com/ECWeb/ItemList.aspx?categoryid=102401";

	@Autowired 
	private WebCrawler crawler;
	
    @RequestMapping("/go")
    public void index() throws IOException {
		URL huntUrl = new URL(urlString);
    	crawler.startCrawl(huntUrl);
    }
    
    /**
     * Obtain a list of all discovered photos
     */
    @RequestMapping(path="/photos", method = RequestMethod.GET)
    public List<PhotoDTO> getPhotos(@RequestParam(value="PageNum", defaultValue = "1") int pageNum,
    		                        @RequestParam(value="PageSize", defaultValue = "100") int pageSize) {
    	
    	// retrieve a stored list of photos
    	final List<PhotoData> photos = crawler.getPhotos(pageNum, pageSize);
    	
    	final List<PhotoDTO> photoDTOs = new ArrayList<>(photos.size());
    	
    	// convert DAO to DTO
    	for (PhotoData dao: photos) {
    		PhotoDTO dto = new PhotoDTO();
    		dto.setName(dao.getName());
    		dto.setUrl(dao.getUrl().toString());
    		dto.setNodeUrl(dao.getNodeUrl().toString());
    		dto.setHeight(dao.getHeigth());
    		dto.setWidth(dao.getWidth());
    		
    		photoDTOs.add(dto);
    	}
    	
    	// return list of photos
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
    
    /**
     * Get the product version
     */
    @RequestMapping(path="/version", method = RequestMethod.GET)
    public String getVersion() {
    	System.out.println("Getting version");
    	log.info("Version requested");
    	return version;
    }
    
    @RequestMapping(path="/nodes", method = RequestMethod.GET)
    public List<NodeDTO> getNodes() {
    	final List<NodeData> nodes = crawler.getNodes();
    	
    	final List<NodeDTO> dtoList = new ArrayList<>(nodes.size());
    	
    	// convert to DTO
    	for (NodeData dao: nodes) {
    		final NodeDTO dto = new NodeDTO();
    		dto.setUrl(dao.getUrl().toString());

    		// add parent, if it exists
    		if (dao.getParent() != null) {
        		dto.setParentUrl(dao.getParent().toString());
    		}

    		dtoList.add(dto);
    	}
    	
    	return dtoList;
    }
}
