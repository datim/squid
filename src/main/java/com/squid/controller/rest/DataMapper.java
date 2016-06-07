package com.squid.controller.rest;

import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.stereotype.Service;

import com.squid.data.NodeData;
import com.squid.data.PhotoData;

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
		
		return dto;
	}
	
	// convert dto to dao
	public PhotoData dtoToDao(final PhotoDTO dto) throws MalformedURLException {
		
		final PhotoData dao = new PhotoData();
		dao.setUrl(new URL(dto.getUrl()));
		dao.setName(dto.getName());
		dao.setNodeUrl(new URL(dto.getNodeUrl()));
		dao.setSaved(dto.isSaved());
		
		return dao;
	}
	
	// convert dao to dto
	public NodeDTO daoToDto(final NodeData dao) {
		
		final NodeDTO dto = new NodeDTO();
		dto.setUrl(dao.getUrl().toString());

		// add parent, if it exists
		if (dao.getParent() != null) {
    		dto.setParentUrl(dao.getParent().toString());
		}
		
		return dto;
	}

}
