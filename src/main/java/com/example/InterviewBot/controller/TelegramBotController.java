package com.example.InterviewBot.controller;


import com.example.InterviewBot.config.BotConfig;
import com.example.InterviewBot.model.User;
import com.example.InterviewBot.service.UserService;
import com.example.InterviewBot.util.BotUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramBot;

@Component
@AllArgsConstructor
public class TelegramBotController extends TelegramLongPollingBot {

    @Autowired
    private final BotConfig botconfig;

    @Autowired
    private UserService userService = new UserService();

    @Autowired
    private BotUtils botUtils;

    @Override
    public String getBotUsername() {
        return botconfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botconfig.getToken();
    }
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String username = update.getMessage().getFrom().getUserName();
            String firstName = update.getMessage().getFrom().getFirstName();

            // Обработка команды /start
            if (messageText.equals("/start")) {
                try {
                    User user = userService.registerUser(username, firstName, "user");
                    botUtils.sendMessage(chatId, "Вы успешно зарегистрированы! Добро пожаловать, " + firstName + ".",this);
                }catch (TelegramApiException ex) {
                    throw new RuntimeException(ex);
                }
                catch (Exception e) {
                    try {
                        botUtils.sendMessage(chatId, "Вы уже зарегистрированы.",this);
                    } catch (TelegramApiException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            } else {
                // Обработка других команд и сообщений
                try {
                    botUtils.sendMessage(chatId, "Команда не распознана. Пожалуйста, используйте /start для начала.",this);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    private void startCommandReceived(Long chatId, String name) {
        String answer = "Здравствуйте, " + name + " !" + "\n" +
                "Это тг бот для подготовки к собеседованиям.";
        try {
            botUtils.sendMessage(chatId, answer,this);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}
