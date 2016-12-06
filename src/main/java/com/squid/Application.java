package com.squid;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {

	/**
	 * The main application!
	 * @param args Command line arguments
	 */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
