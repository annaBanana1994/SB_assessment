package com.starlingbank.assessment;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class AssessmentApplication {

	private static final Logger LOGGER= LoggerFactory.getLogger(AssessmentApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(AssessmentApplication.class, args);
		LOGGER.info("Application started");
	}
	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
}
