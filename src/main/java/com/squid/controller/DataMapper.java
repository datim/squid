package com.squid.controller;

import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.stereotype.Service;

import com.squid.controller.rest.NodeDTO;
import com.squid.controller.rest.PhotoDTO;
import com.squid.controller.rest.SearchStatusDTO;
import com.squid.data.NodeData;
import com.squid.data.PhotoData;
import com.squid.data.SearchStatusData;

/**
 * Map DTO to DAO objects and vice-versa
 *
 */
@Service
public class DataMapper {
	
	// convert dao to dto
	public PhotoDTO daoToDto(final PhotoData dao) {
		
		final PhotoDTO dto = new PhotoDTO();
		dto.setName(dao.getName());
		dto.setNodeUrl(dao.getNodeUrl().toString());
		dto.setUrl(dao.getUrl().toString());
		dto.setHeight(dao.getHeigth());
		dto.setWidth(dao.getWidth());
		dto.setSaved(dao.isSaved());
		dto.setId(dao.getId());
		dto.setBaseUrl(dao.getBaseUrl());
		
		return dto;
	}
	
	// convert dto to dao
	public PhotoData dtoToDao(final PhotoDTO dto) throws MalformedURLException {
		
		final PhotoData dao = new PhotoData();
		dao.setUrl(new URL(dto.getUrl()));
		dao.setName(dto.getName());
		dao.setNodeUrl(new URL(dto.getNodeUrl()));
		dao.setSaved(dto.isSaved());
		dao.setId(dto.getId());
		dao.setBaseUrl(dto.getBaseUrl());
		
		return dao;
	}
	
	// convert dao to dto
	public NodeDTO daoToDto(final NodeData dao) {
		
		final NodeDTO dto = new NodeDTO();
		dto.setUrl(dao.getUrl().toString());
		dto.setId(dao.getId());

		// add parent, if it exists
		if (dao.getParent() != null) {
    		dto.setParentUrl(dao.getParent().toString());
		}
		
		return dto;
	}
	
	// convert dao to dto
	public SearchStatusDTO daoToDto(final SearchStatusData dao) {
		
		final SearchStatusDTO dto = new SearchStatusDTO();
		dto.setUrl(dao.getUrl());
		dto.setNodeCount(dao.getNodeCount());
		dto.setStatus(dao.getStatus());
		dto.setMaxDepth(dao.getMaxDepth());
		dto.setId(dao.getId());
		
		return dto;
	}
}
