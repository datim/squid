package com.squid.controller;

import javax.annotation.PostConstruct;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.squid.controller.rest.ImageDTO;
import com.squid.controller.rest.PageDTO;
import com.squid.controller.rest.QueryDTO;
import com.squid.controller.rest.SearchStatusDTO;
import com.squid.controller.rest.old.UserParameterDTO;
import com.squid.data.FoundPage;
import com.squid.data.Image;
import com.squid.data.Query;
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

	// convert Page dao to dto
	public PageDTO convert(FoundPage dao) {
		return modelMapper.map(dao,  PageDTO.class);
	}

	// convert Page dao to dto. Static to allow page mapping in controller functions
    public static ImageDTO convert(Image img) {
    	return new ModelMapper().map(img, ImageDTO.class);
    }
}
