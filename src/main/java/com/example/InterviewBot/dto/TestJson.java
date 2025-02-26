package com.example.InterviewBot.dto;


import lombok.Data;
import java.util.List;

@Data
public class TestJson {
    private String testName;      // Название теста
    private List<QuestionJson> questions;
}

