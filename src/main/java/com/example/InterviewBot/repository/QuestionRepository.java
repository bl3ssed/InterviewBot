package com.example.InterviewBot.repository;

import com.example.InterviewBot.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {

    Optional<Question> findById(int id);

    // Получение всех вопросов для конкретного теста
    List<Question> findByTestId(Integer testId);

    // Поиск вопросов, содержащих определенное слово в тексте
    List<Question> findByQuestionTextContainingIgnoreCase(String keyword);

    // Получение всех вопросов, отсортированных по дате создания
    List<Question> findAllByOrderByCreatedAtDesc();
}