package com.example.InterviewBot.util;

import com.example.InterviewBot.config.BotConfig;
import com.example.InterviewBot.model.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

@Component
public class BotUtils {

    private final String BOT_TOKEN;

    @Autowired
    public BotUtils(BotConfig botConfig) {
        this.BOT_TOKEN = botConfig.getToken();
    }

    public void sendMessage(long chatId, String text,AbsSender sender) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);

        try {
            sender.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void sendMessageWithKeyboard(long chatId, String text,AbsSender sender) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);

        // Пример добавления клавиатуры
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        KeyboardRow row = new KeyboardRow();
        row.add("/stat");
        row.add("/tests");
        row.add("/feedback");
        row.add("/help");
        keyboardMarkup.setKeyboard(java.util.Collections.singletonList(row));
        keyboardMarkup.setResizeKeyboard(true);
        sendMessage.setReplyMarkup(keyboardMarkup);

        try {
            sender.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void sendMessageWithKeyboardEndTest(long chatId, String text,AbsSender sender) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);

        // Пример добавления клавиатуры
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        KeyboardRow row = new KeyboardRow();
        row.add("/stat");
        row.add("/tests");
        row.add("/feedback");
        row.add("/help");
        keyboardMarkup.setKeyboard(java.util.Collections.singletonList(row));
        keyboardMarkup.setResizeKeyboard(true);
        sendMessage.setReplyMarkup(keyboardMarkup);

        try {
            sender.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageWithKeyboardTest(long chatId, String text,AbsSender sender,int count) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);

        // Пример добавления клавиатуры
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        KeyboardRow row = new KeyboardRow();
        for (int i = 0; i < count; i++) {
            Integer index = i+1;
            row.add(index.toString());
        }
        keyboardMarkup.setKeyboard(java.util.Collections.singletonList(row));
        keyboardMarkup.setResizeKeyboard(true);
        sendMessage.setReplyMarkup(keyboardMarkup);

        try {
            sender.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageWithKeyboardTestList(long chatId, String text, AbsSender sender, List<Test> tests) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);

        // Пример добавления клавиатуры
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        KeyboardRow row = new KeyboardRow();
        for (Test test : tests) {
            Integer index = test.getTestId();
            row.add(index.toString());
        }
        keyboardMarkup.setKeyboard(java.util.Collections.singletonList(row));
        keyboardMarkup.setResizeKeyboard(true);
        sendMessage.setReplyMarkup(keyboardMarkup);

        try {
            sender.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    // Метод для скачивания файла


    // Другие вспомогательные методы
}