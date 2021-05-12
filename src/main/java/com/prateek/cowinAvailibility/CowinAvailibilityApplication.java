package com.prateek.cowinAvailibility;

import com.prateek.cowinAvailibility.configuration.AppConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching(proxyTargetClass = true)
@EnableScheduling
@EnableAsync

@EnableConfigurationProperties(value = AppConfiguration.class)
public class CowinAvailibilityApplication {

	public static void main(String[] args) {
		SpringApplication.run(CowinAvailibilityApplication.class, args);
	}

}
