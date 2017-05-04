package com.squid.engine.old;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.squid.data.SearchStatusRepository;
import com.squid.data.old.SearchStatusData;

/**
 * Contain static methods helpful for facilitating search
 * @author Datim
 *
 */
public class SearchConstants {
	
	/**
	 * Return a list of invalid image name suffixes
	 */
	static public List<String> getInvalidImageSuffixs() {
		
		final List<String> invalidSuffixes = new ArrayList<>();
		invalidSuffixes.add("spacer.gif");

		return invalidSuffixes;
	}
	
	
	/**
	 * Return a list of invalid page extensions that should not be 
	 * traversed when crawling through pages.
	 */
	static public List<String> getInvalidPageExtensions() {
		final List<String> invalidSuffixes = new ArrayList<>();
		invalidSuffixes.add("css");
		invalidSuffixes.add("pdf");

		return invalidSuffixes;
	}
	/**
	 * Return a list of Key Words that should signal the 
	 * end of an image name. All characters after this boundary should be 
	 * stripped off.
	 * 
	 * example: 'http://someurl/images/file1.jpg#hashcode' . Strip all
	 *          characters after '#'.
	 */
	static public List<String> getImageBoundaryKeyWords() {
		
		final List<String> boundaryKeys = new ArrayList<>();

		// define a set of boundary keys. These keys should
		// signal the end of an image name.  All characters
		// after this boundary should be stripped off.
		// e.g. 'http://someurl/images/file1.jpg#hashcode'
		boundaryKeys.add("?");
		boundaryKeys.add("=");
		
		return boundaryKeys;
	}
	
	/**
	 * Return a list of Key Words that should signal the 
	 * end of a URL. All characters after this boundary should be 
	 * stripped off.
	 * 
	 * example: 'http://someurl/test/test1?query=blah' . Strip all
	 *          characters after '?'.
	 */
	static public List<String> getPageURLBoundaryKeyWords() {
		
		final List<String> boundaryKeys = new ArrayList<>();
		boundaryKeys.add("#");

		return boundaryKeys;
	}
	
	/**
	 * Return a list of valid image extensions
	 */
	static public List<String> getValidImageExtensions() {
		
		final List<String> extensions = new ArrayList<>();
		extensions.add(".jpg");
		extensions.add(".jpeg");
		extensions.add(".png");
		extensions.add(".gif");

		return extensions;
	}
	
	/**
	 * Update the search status
	 * 
	 * @param huntUrl The url being searched
	 * @param nodeCount The number of pages that have been discovered
	 * @param imageCount The number of images that have been discovered
	 * @param status The status of the search
	 */
	public static void setSearchStatus(URL searchUrl, Long nodeCount, Long imageCount, Long maxNodes, SearchStatusData.SearchStatus status, final SearchStatusRepository searchStatusRepo) {
		
		// maintain one record for status. If the record already exists, update it
		SearchStatusData searchStatus = searchStatusRepo.findByUrl(searchUrl.toString());
		
		if (searchStatus == null) {
			// record doesn't exist, create a new one
			searchStatus = new SearchStatusData();
		}
		
		// update the status results
		searchStatus.setUrl(searchUrl.toString());
		searchStatus.setMaxDepth(maxNodes);
		searchStatus.setNodeCount(nodeCount);
		searchStatus.setImageCount(imageCount);
		searchStatus.setStatus(status);
		
		// save the status
		searchStatusRepo.save(searchStatus);
	}
}
