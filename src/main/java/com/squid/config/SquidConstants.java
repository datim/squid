package com.squid.config;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SquidConstants {
	
	static private Path downloadDirectory = Paths.get(System.getProperty("user.home"), "squid", "photos");
	
	public static Path getDownloadDirectory() {
		return downloadDirectory;
	}
}
