package com.squid.service.crawler;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SquidConstants {
	
	static private Path downloadDirectory = Paths.get(System.getProperty("user.home"), "squid", "photos");
	
	static private String version = "0.1";
	
	static private String baseURL = "http://www.stampinup.com/ECWeb/ItemList.aspx?categoryid=102401";

	public static Path getDownloadDirectory() {
		return downloadDirectory;
	}
	
	public static String getVersion() {
		return version;
	}
	
	public static String getBasedURL() {
		return baseURL;
	}
}
