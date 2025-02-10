package com.example.InterviewBot.repository;

import com.example.InterviewBot.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    // Получение отзыва по ID
    Optional<Feedback> findByFeedbackId(Long feedbackId);

    // Получение всех отзывов от пользователя
    List<Feedback> findByUser_UserId(Long userId);

    // Получение всех отзывов с определенной оценкой
    List<Feedback> findByRate(Integer rate);

    // Получение всех отзывов, отсортированных по дате отправки
    List<Feedback> findAllByOrderBySubmittedAtDesc();
}