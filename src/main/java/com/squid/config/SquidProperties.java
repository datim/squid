package com.squid.config;

import java.io.FileNotFoundException;
import java.io.IOException;
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
	
	static final String PROPERTIES_FILE = "application.properties";

	int maxNodes;
	int maxImages;
	String baseUrl;
	String squidVersion;
	
	static Logger log = Logger.getLogger(SquidProperties.class.getName());

	
	@Autowired
	Environment env;
	
	/**
	 * Read the properties file and save all of the values
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
		
		setProxy(props.getProperty("server.proxy.host"), props.getProperty("server.proxy.port"));
	}
	
	/**
	 * Set the proxy for the system, if defined
	 * @param host
	 * @param port
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
}
