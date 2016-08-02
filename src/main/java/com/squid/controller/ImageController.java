package com.squid.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.squid.controller.rest.PhotoDTO;
import com.squid.data.PhotoData;
import com.squid.service.SearchService;

import javassist.NotFoundException;

/**
 * Controller for image retrieval APIs
 * @author Datim
 */
@RestController
@RequestMapping("/crawl")
public class ImageController {
	
	static Logger log = Logger.getLogger(ImageController.class.getName());
	
	@Autowired 
	private SearchService crawler;
	
	@Autowired
	private DataMapper dataMapper;
	
	 /**
     * Obtain a list of all discovered photos
     */
    @RequestMapping(path="/photos", method = RequestMethod.GET)
    public List<PhotoDTO> getPhotos(@RequestParam(value="filter", defaultValue = "") String filter) {
    	
    	log.info("Request all photos");
    	
    	// retrieve a stored list of photos
    	final List<PhotoData> photos = crawler.getPhotos(filter);
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
     * @throws NotFoundException 
     */
    @RequestMapping(path="/photos/{id}/download", method = RequestMethod.GET) 
    public @ResponseBody PhotoDTO downloadPhoto(@PathVariable("id") long photoId) throws IOException, NotFoundException {
    	
    	// download photo by id
    	log.info("request to download photo");
    	return dataMapper.daoToDto(crawler.savePhoto(photoId));
    }
   
    /**
     * Obtain the number of discovered photos
     * @return
     */
    @RequestMapping(path="/photos/count", method = RequestMethod.GET)
    public long getPhotoCount() {
    	log.info("request for all photos");
    	return crawler.getPhotosCount();
    }
}
