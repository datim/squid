package com.squid;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.squid.data.BoxData;
import com.squid.data.BoxRepository;

@ComponentScan
@EnableAutoConfiguration
@Configuration
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    /*
    @Bean
    public CommandLineRunner forceSave(BoxRepository repo) {
    	return (args) -> {
    	repo.save(new BoxData(1, 12, 5, 10));
    	};
    }
    */
    
}
