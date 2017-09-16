package com.squid.controller;

import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.PostConstruct;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.squid.controller.rest.ImageDTO;
import com.squid.controller.rest.PageDTO;
import com.squid.controller.rest.PhotoDTO;
import com.squid.controller.rest.QueryDTO;
import com.squid.controller.rest.SearchStatusDTO;
import com.squid.controller.rest.old.UserParameterDTO;
import com.squid.data.Image;
import com.squid.data.Page;
import com.squid.data.Query;
import com.squid.data.old.PhotoData;
import com.squid.data.old.SearchStatusData;
import com.squid.data.old.UserParameterData;

/**
 * Map DTO to DAO objects and map DAO to DTO objects
 *
 */
@Service
public class DataMapper {

	private ModelMapper modelMapper;

	@PostConstruct
	private void initialize() {
		modelMapper = new ModelMapper();
	}

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
	public SearchStatusDTO daoToDto(final SearchStatusData dao) {

		final SearchStatusDTO dto = new SearchStatusDTO();
		dto.setUrl(dao.getUrl());
		dto.setNodeCount(dao.getNodeCount());
		dto.setStatus(dao.getStatus().getString());
		dto.setMaxDepth(dao.getMaxDepth());
		dto.setId(dao.getId());
		dto.setImageCount(dao.getImageCount());

		return dto;
	}

	// convert dao to dto
	public UserParameterDTO daoToDto(final UserParameterData dao) {

		final UserParameterDTO dto = new UserParameterDTO();
		dto.setId(dao.getId());
		dto.setUserId(dao.getUserId());
		dto.setSavePath(dao.getSavePath());
		dto.setMaxImageCount(dao.getMaxImageCount());
		dto.setMaxPageCount(dao.getMaxPageCount());
		dto.setSearchFilter(dao.getSearchFilter());
		dto.setSearchURL(dao.getSearchURL());

		return dto;
	}

	// convert query dao to dto
	public QueryDTO convert(Query dao) {
		return modelMapper.map(dao, QueryDTO.class);
	}

	// convert Image dao to dto
	public ImageDTO convert(Image dao) {
		return modelMapper.map(dao, ImageDTO.class);
	}

	// convert Page dao to dto
	public PageDTO convert(Page dao) {
		return modelMapper.map(dao,  PageDTO.class);
	}
}
