package com.example.InterviewBot.repository;

import com.example.InterviewBot.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<Test, Integer> {

    Test findByTestId(Integer testId);
    // Поиск теста по названию
    Test findByTitle(String title);

    // Поиск тестов, содержащих определенное слово в названии
    List<Test> findByTitleContainingIgnoreCase(String keyword);

    // Получение всех тестов, отсортированных по дате создания
    List<Test> findAllByOrderByCreatedAtDesc();
}