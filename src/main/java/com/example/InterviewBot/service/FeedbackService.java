package com.example.InterviewBot.service;

import com.example.InterviewBot.model.Feedback;
import com.example.InterviewBot.model.User;
import com.example.InterviewBot.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    // Создание нового отзыва
    public Feedback createFeedback(Long userId, String feedbackText, Integer rate) {
        Feedback feedback = new Feedback();
        feedback.setUser(new User(userId));
        feedback.setFeedbackText(feedbackText);
        feedback.setRate(rate);
        feedback.setSubmittedAt(java.time.LocalDateTime.now().toString());
        return feedbackRepository.save(feedback);
    }

    // Получение всех отзывов от пользователя
    public List<Feedback> getFeedbackByUser(Long userId) {
        return feedbackRepository.findByUserId(userId);
    }

    // Другие методы, если необходимо
}