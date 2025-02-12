package com.example.InterviewBot.service;

import com.example.InterviewBot.model.Feedback;
import com.example.InterviewBot.model.User;
import com.example.InterviewBot.repository.FeedbackRepository;
import com.example.InterviewBot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    // Создание нового отзыва
    public Feedback createFeedback(Long tgId, String feedbackText, Integer rate) {
        Feedback feedback = new Feedback();
        feedback.setUser(userRepository.findByTgId(tgId).orElse(null));
        feedback.setFeedbackText(feedbackText);
        feedback.setRate(rate);
        feedback.setSubmittedAt(Timestamp.from(Instant.from(Instant.now())));
        return feedbackRepository.save(feedback);
    }

    public Feedback updateFeedback(User user, String feedbackText, Integer rate) {
        Feedback updatedFeedback = feedbackRepository.findByUser_tgId(user.getTgId()).orElse(null);
        if (updatedFeedback != null) {
            updatedFeedback.setFeedbackText(feedbackText);
            updatedFeedback.setRate(rate);
            updatedFeedback.setSubmittedAt(Timestamp.from(Instant.from(Instant.now())));
            return feedbackRepository.save(updatedFeedback);
        }
        else {
            return null;
        }

    }

    public Feedback setFeedbackRate(Long tgId, Integer rate ) {
        Feedback feedback = feedbackRepository.findByUser_tgId(tgId).orElse(null);
        if (feedback != null) {
            feedback.setRate(rate);
            return feedbackRepository.save(feedback);
        }
        return null;

    }

    // Получение всех отзывов от пользователя
    public List<Feedback> getFeedbackByUser(Long userId) {
        return feedbackRepository.findByUser_UserId(userId);
    }

    // Другие методы, если необходимо
}