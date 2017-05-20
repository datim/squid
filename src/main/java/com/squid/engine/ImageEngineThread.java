package com.squid.engine;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squid.engine.parser.SearchImage;
import com.squid.engine.requests.ImageRequestMsg;
import com.squid.service.RepositoryService;

import javassist.NotFoundException;

/**
 * Implement parsing of an image in a thread
 * @author Datim
 */
public class ImageEngineThread extends EngineThread {

    protected static final Logger log = LoggerFactory.getLogger(ImageEngineThread.class);

	//constructor
	protected ImageEngineThread(ImageRequestMsg msg, RepositoryService repoService) {
		super(msg, repoService);
	}

	/**
	 * Define the logic for parsing images that will run in a thread
	 */
	@Override
	protected void execute() {
		try {
			// parse an image URL
			new SearchImage(repoService).executeMsg((ImageRequestMsg)requestMessage);

		} catch (final IOException | NotFoundException e) {
			log.error("Unable to parse image {} for query {}. Exception {}", requestMessage.getUrl().toString(), requestMessage.getSearchQuery().getId(), e.getMessage());
		}
	}
}
