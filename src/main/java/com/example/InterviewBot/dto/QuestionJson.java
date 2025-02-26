package com.example.InterviewBot.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuestionJson {
    private String text;          // Текст вопроса
    private String detailedText;        // Порядковый номер
    private List<AnswerJson> answers;
}