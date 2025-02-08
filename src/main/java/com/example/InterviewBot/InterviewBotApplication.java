package com.example.InterviewBot;

import com.example.InterviewBot.config.BotConfig;
import com.example.InterviewBot.controller.TelegramBotController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class InterviewBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(InterviewBotApplication.class, args);
	}

}
