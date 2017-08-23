package com.squid.engine.parser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Singleton instance with helper methods
 * @author Datim
 *
 */
public class SearchHelperSingleton {

	private static SearchHelperSingleton singleton;

	// private constructor
	private SearchHelperSingleton() {}

	/**
	 * Thread safe singleton access
	 * @return An instance of the SearchHelper class
	 */
	public synchronized static SearchHelperSingleton getInstance() {
		if (singleton == null) {
			singleton = new SearchHelperSingleton();
		}

		return singleton;
	}

	/**
	 *  Calculate the checksum for a URL
	 *
	 * Reference: http://stackoverflow.com/questions/6881029/how-to-check-whether-a-website-has-been-updated-and-send-a-email
	 * @param pageUrl the URL to calculate checksum for
	 * @return The calculated checksum for a page
	 * @throws IOException
	 */
	public String calcPageChecksum(final URL pageUrl) throws IOException  {

		String checksum = null;

		final HttpURLConnection pageConnection = (HttpURLConnection) pageUrl.openConnection();
		pageConnection.setRequestMethod("GET");
		pageConnection.setDoOutput(true);
	    pageConnection.connect();

        // Use MD5 because where just hashing a string and its faster, no security here
	    checksum = DigestUtils.md5Hex(pageConnection.getInputStream());

        return checksum;
	}
}
