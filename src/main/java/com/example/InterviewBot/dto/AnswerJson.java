package com.example.InterviewBot.dto;

import lombok.Data;

@Data
public class AnswerJson {
    private String text;          // Текст ответа
    private boolean correct;      // Правильность ответа
    private int order;            // Порядок в списке ответов
}