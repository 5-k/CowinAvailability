package com.prateek.cowinAvailibility;

import com.prateek.cowinAvailibility.configuration.AppConfiguration;
import com.prateek.cowinAvailibility.service.chatbot.TelegramSlotPoller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableConfigurationProperties(value = AppConfiguration.class)
public class CowinAvailibilityApplication {

	public static void main(String[] args) {
		SpringApplication.run(CowinAvailibilityApplication.class, args);
	}

}
