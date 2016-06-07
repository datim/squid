package com.squid.controller.crawler;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.squid.controller.rest.DataMapper;
import com.squid.controller.rest.NodeDTO;
import com.squid.controller.rest.PhotoDTO;
import com.squid.data.NodeData;
import com.squid.data.PhotoData;
import com.squid.service.crawler.SquidConstants;
import com.squid.service.crawler.WebCrawler;

@RestController
@RequestMapping("/crawl")
public class CrawlerController {
	
	static Logger log = Logger.getLogger(WebCrawler.class.getName());

	@Autowired 
	private WebCrawler crawler;
	
	@Autowired
	private DataMapper dataMapper;
	
    @RequestMapping("/go")
    public void index() throws IOException {
		URL huntUrl = new URL(SquidConstants.getBasedURL());
    	crawler.startCrawl(huntUrl);
    }
    
    /**
     * Obtain a list of all discovered photos
     */
    @RequestMapping(path="/photos", method = RequestMethod.GET)
    public List<PhotoDTO> getPhotos(@RequestParam(value="PageNum", defaultValue = "1") int pageNum,
    		                        @RequestParam(value="PageSize", defaultValue = "500") int pageSize) {
    	
    	// retrieve a stored list of photos
    	final List<PhotoData> photos = crawler.getPhotos(pageNum, pageSize);
    	
    	final List<PhotoDTO> photoDTOs = new ArrayList<>(photos.size());
    	
    	// convert DAO to DTO
    	for (PhotoData dao: photos) {
    		photoDTOs.add(dataMapper.daoToDto(dao));
    	}
    	
    	// return list of photos
    	return photoDTOs;
    }
    
    /**
     * Download a photo
     */
    @RequestMapping(path="/photos/download", method = RequestMethod.POST) 
    public @ResponseBody PhotoDTO downloadPhoto(@RequestBody PhotoDTO dto) throws IOException {
    	
    	// save the photo
    	final PhotoData savedPhoto = crawler.savePhoto(dataMapper.dtoToDao(dto));
    	
    	final PhotoDTO returnDTO = dataMapper.daoToDto(savedPhoto);
    	return returnDTO;
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
    	return SquidConstants.getVersion();
    }
    
    @RequestMapping(path="/nodes", method = RequestMethod.GET)
    public List<NodeDTO> getNodes() {
    	final List<NodeData> nodes = crawler.getNodes();
    	
    	final List<NodeDTO> dtoList = new ArrayList<>(nodes.size());
    	
    	// convert to DTO
    	for (NodeData dao: nodes) {
    		dtoList.add(dataMapper.daoToDto(dao));
    	}
    	
    	return dtoList;
    }
    
    /**
     * Report the number of nodes
     */
    @RequestMapping(path="/nodes/count", method = RequestMethod.GET)
    public long getNodeCount() {
    	return crawler.getNodeCount();
    }
    
    /**
     * Delete all photo and node content
     */
    @RequestMapping(path="/content", method = RequestMethod.DELETE)
    public void deletePhotos() {
    	crawler.deletePhotos();
    	crawler.deleteNodes();    	
    }
}
