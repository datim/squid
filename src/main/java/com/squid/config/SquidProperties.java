package com.squid.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import org.apache.commons.discovery.tools.ResourceUtils;


/**
 * Read all properties for Squid from a file
 */
@Service
public class SquidProperties {
	
	static Logger log = Logger.getLogger(SquidProperties.class.getName());

	static final String PROPERTIES_FILE = "application.properties";

	private int maxNodes;
	private int maxImages;
	private String baseUrl;
	private String squidVersion;
	private String proxyHost;
	private String proxyPort;
	private String imageSavePath;
	private Path downloadDirectory;
	
	@Autowired
	Environment env;
	
	/**
	 * Read the properties file and save all of the values. These properties will be used for initial values
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@PostConstruct
	void readPropertiesFile() throws FileNotFoundException, IOException {
		
		final Properties props = ResourceUtils.loadProperties(null, PROPERTIES_FILE, null);
		
		this.maxNodes = Integer.parseInt(props.getProperty("server.search.maxnodes"));
		this.maxImages = Integer.parseInt(props.getProperty("server.search.maxphotos"));
		this.baseUrl = props.getProperty("server.search.baseurl");
		this.squidVersion = props.getProperty("server.version");
		this.proxyHost = props.getProperty("server.proxy.host");
		this.proxyPort = props.getProperty("server.proxy.port");
		this.imageSavePath = props.getProperty("server.imagesavepath");
		
		// set the property
		this.downloadDirectory = Paths.get(props.getProperty("server.imagesavepath"));
		
		// set proxy
		setProxy(this.proxyHost, this.proxyPort);
	}
	
	/**
	 * Set the proxy for the system, if defined
	 * @param host The proxy host
	 * @param port The proxy port
	 */
	private void setProxy(String host, String port) {
		
		if( host != null && port != null) {
			System.setProperty("http.proxyHost", host);
			System.setProperty("http.proxyPort", port);
			System.setProperty("https.proxyHost", host);
			System.setProperty("https.proxyPort", port);
			log.info("Set system proxy to " + host + ":" + port);
		} else {
			log.info("System proxy not set");
		}
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public int getMaxNodes() {
		return maxNodes;
	}

	public void setMaxNodes(int maxNodes) {
		this.maxNodes = maxNodes;
	}

	public int getMaxImages() {
		return maxImages;
	}

	public void setMaxImages(int maxPhotos) {
		this.maxImages = maxPhotos;
	}

	public String getSquidVersion() {
		return squidVersion;
	}

	public void setSquidVersion(String squidVersion) {
		this.squidVersion = squidVersion;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public String getProxyPort() {
		return proxyPort;
	}

	public static String getPropertiesFile() {
		return PROPERTIES_FILE;
	}

	public String getImageSavePath() {
		return imageSavePath;
	}

	public Environment getEnv() {
		return env;
	}

	public Path getDownloadDirectory() {
		return downloadDirectory;
	}
}
