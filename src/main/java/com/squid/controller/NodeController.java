package com.squid.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.squid.config.SquidProperties;
import com.squid.controller.rest.NodeDTO;
import com.squid.data.old.NodeData;
import com.squid.service.SearchService;

@RestController
@RequestMapping("/crawl")
public class NodeController {
	
	static Logger log = Logger.getLogger(SearchService.class.getName());

	@Autowired 
	private SearchService crawler;
	
	@Autowired
	private DataMapper dataMapper;
	
	@Autowired
	private SquidProperties squidProps;

    /**
     * Get the product version
     */
    @RequestMapping(path="/version", method = RequestMethod.GET)
    public String getVersion() {
    	System.out.println("Getting version");
    	log.info("Version requested");
    	final String version = squidProps.getSquidVersion();
    	return version;
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
    @RequestMapping(path="/nodes", method = RequestMethod.DELETE)
    public void deletePhotos() {
    	crawler.deletePhotos();
    	crawler.deleteNodes();    	
    }
}
