package com.squid.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class SquidRestConfiguration {
    @Bean
    public WebMvcConfigurer corsConfigurer() {

    	/**
    	 * Allow cross mappings from port 7777
    	 */
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                		.allowedOrigins("http://localhost:7777")
            			.allowedMethods("PUT", "DELETE", "POST", "GET");
            }
        };
    }
}
