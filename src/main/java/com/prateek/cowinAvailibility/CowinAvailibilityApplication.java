package com.prateek.cowinAvailibility;

import com.prateek.cowinAvailibility.configuration.AppConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableConfigurationProperties(value = AppConfiguration.class)
public class CowinAvailibilityApplication {

	public static void main(String[] args) {
		SpringApplication.run(CowinAvailibilityApplication.class, args);
	}

}
