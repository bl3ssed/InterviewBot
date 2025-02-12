package com.example.InterviewBot.util;

import com.example.InterviewBot.model.Question;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CurrentTestManager {
    private Map<Long, Long> currentTests = new HashMap<>();
    private Map<Long, Integer> currentQuestionIndices = new HashMap<>();
    private Map<Long, List<Question>> questions = new HashMap<>();
    private Map<Long, Integer> score = new HashMap<>();

    public void setCurrentTest(long chatId, long testId) {
        currentTests.put(chatId, testId);
    }

    public long getCurrentTest(long chatId) {
        return currentTests.get(chatId);
    }

    public void setCurrentQuestionIndex(long chatId, int index) {
        currentQuestionIndices.put(chatId, index);
    }

    public int getCurrentQuestionIndex(long chatId) {
        return currentQuestionIndices.get(chatId);
    }

    public void setQuestions(long chatId, List<Question> questionsList) {
        questions.put(chatId, questionsList);
    }

    public List<Question> getQuestions(long chatId) {
        return questions.get(chatId);
    }

    public void incrementScore(long chatId) {
        score.put(chatId, score.getOrDefault(chatId, 0) + 1);
    }

    public int getScore(long chatId) {
        return score.getOrDefault(chatId, 0);
    }

    public int getTotalQuestions(long chatId) {
        return questions.get(chatId).size();
    }

    public void removeCurrentTest(long chatId) {
        currentTests.remove(chatId);
        currentQuestionIndices.remove(chatId);
        questions.remove(chatId);
        score.remove(chatId);
    }
}