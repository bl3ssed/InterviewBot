package com.example.InterviewBot.repository;

import com.example.InterviewBot.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Integer> {

    // Получение всех ответов для конкретного вопроса
    List<Answer> findByQuestionId(Integer questionId);

    // Поиск правильных ответов для вопроса
    List<Answer> findByQuestionIdAndIsCorrectTrue(Integer questionId);

    // Получение всех ответов, отсортированных по номеру
    List<Answer> findByQuestionIdOrderByNumberAsc(Integer questionId);
}