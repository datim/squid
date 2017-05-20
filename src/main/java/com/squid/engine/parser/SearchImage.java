package com.squid.engine.parser;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squid.data.Image;
import com.squid.data.ImageTopology;
import com.squid.data.Page;
import com.squid.data.Query;
import com.squid.engine.requests.ImageRequestMsg;
import com.squid.service.RepositoryService;

import javassist.NotFoundException;

/**
 * Handle the image management of searching for an image.  Call the page parser if page
 * FIXME - set image t-shirt size size
 * FIXME - define name for image
 * @author Datim
 *
 */
public class SearchImage {

    protected static final Logger log = LoggerFactory.getLogger(SearchImage.class);
	private final RepositoryService repoService;

	// constructor
	public SearchImage(final RepositoryService repoService) {
    	this.repoService = repoService;
	}

	/**
	 * Process a new Image message. Validate that the image exists, get its dimensions,
	 * and create a new image object for it.
	 * @param requestMessage The image message to process
	 * @throws IOException
	 * @throws NotFoundException
	 */
	public void executeMsg(final ImageRequestMsg requestMessage) throws IOException, NotFoundException {

		final URL imageUrl = requestMessage.getUrl();
		final Query searchQuery = requestMessage.getSearchQuery();

		String checksum = null;

		if (!canSearchContinue(searchQuery)) {
			log.info("Maximum number of images discovred for query '{}'. Stopping search", searchQuery);
			return;
		}

		try {
			// calculate the checksum for the image
			checksum = SearchHelperSingleton.getInstance().calcPageChecksum(imageUrl);

		} catch (final IOException e) {
			log.error("Unable to calculate checksum for image '{}'. Exception: {}", imageUrl, e.getMessage());
			throw e;
		}

		// check if an image record exists for this image
		Image image = repoService.getImageRepo().findByUrl(imageUrl);

		if (image == null) {
			// new image record!
			// FIXME - define name for image
			image = saveImageRecord(new Image(imageUrl, checksum, "FIXME"), checksum);

			// set topology information
			setImageTopology(searchQuery, image, requestMessage.getPage());

		} else if (!image.getCheckSum().equals(checksum)) {
			// checksum has changed on the image.  Update record
			saveImageRecord(image, checksum);

			// set topology information
			setImageTopology(searchQuery, image, requestMessage.getPage());
		}
	}

	/**
	 * Return false if query has been stopped or maximum number of images have been found
	 * @param searchQuery
	 * @return
	 */
	private boolean canSearchContinue(final Query searchQuery) {

		boolean continueSearch = true;

		final long imageCount = repoService.getImageTopologyRepo().findByQuery(searchQuery.getId()).size();

		if (imageCount >= searchQuery.getMaxImages()) {
			log.debug("Maximum number of images discovered for query {}. Will not continue", searchQuery.getId());
			continueSearch = false;
		}

		return continueSearch;
	}

	/**
	 * Set a new record or update an existing record. Set the width, height, and size of the image
	 * @param image
	 * @throws IOException
	 * @throws NotFoundException
	 */
	private Image saveImageRecord(final Image image, final String checksum) throws IOException, NotFoundException  {

		Image returnImage = null;

		//set height and width of image
		final BufferedImage bufferedImage = ImageIO.read(image.getUrl());

		if (bufferedImage != null) {
			image.setHeight(bufferedImage.getHeight());
			image.setWidth(bufferedImage.getWidth());
			image.setCheckSum(checksum);
			image.setSize("large"); // FIXME SET Dynamically

		} else {

			log.warn("Unable to get image dimensions for image '{}'", image.getUrl());
			throw new NotFoundException("Image URL: " + image.getUrl());
		}

		try {
			// attempt to save an image record
			returnImage = repoService.getImageRepo().save(image);

		} catch (final org.hibernate.exception.ConstraintViolationException | org.springframework.dao.DataIntegrityViolationException e) {
			// create a new Page record.  Record already created, fail
			log.warn("Failed to create a new page record for url '{}'. Record already exists. Exception: '{}'", image.getUrl(), e.getMessage());
			return null;
		}

		return returnImage;
	}

	/**
	 * Create a new topology mapping for a page against a query. Create it if it does not yet exist.
	 * already exists, just return it.
	 * @param query The query to map to a topology tree
	 * @param page The page to map to a topology tree.
	 * @return An existing page topology mapping, or a new one if it has not yet been created
	 */
	private ImageTopology setImageTopology(final Query query, final Image image, final Page parentPage) {

		ImageTopology ImageMapping = repoService.getImageTopologyRepo().findByQueryAndImageId(query.getId(), image.getId());

		if (ImageMapping == null) {
			// topology record does not exist. Create it.
			log.debug("Creating a new image topology for image '{}' to query '{}'", image.getUrl(), query.getUrl());

			// create a new topology record and save it
			ImageMapping = new ImageTopology(query.getId(), image.getId(), (parentPage == null) ? image.getId() : parentPage.getId());
			ImageMapping = repoService.getImageTopologyRepo().save(ImageMapping);

			log.debug("Topology created for page '{}' with id {}", image.getUrl(), ImageMapping.getId());
		}

		return ImageMapping;
	}
}
