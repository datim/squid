package com.squid.search;

import java.util.ArrayList;
import java.util.List;

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
}
