package com.example.InterviewBot.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
public class BotUtils {

    public BotUtils() {
    }

    public void sendMessage(long chatId, String text,AbsSender sender) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);

        // Пример добавления клавиатуры
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        KeyboardRow row = new KeyboardRow();
        row.add("/start");
        row.add("/help");
        keyboardMarkup.setKeyboard(java.util.Collections.singletonList(row));
        sendMessage.setReplyMarkup(keyboardMarkup);

        try {
            sender.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // Другие вспомогательные методы
}