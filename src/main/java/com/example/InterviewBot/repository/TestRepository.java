package com.example.InterviewBot.repository;

import com.example.InterviewBot.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestRepository extends JpaRepository<Test, Integer> {

    Optional<Test> findByTestId(Integer testId);
    // Поиск теста по названию
    Optional<Test> findByTitle(String title);

    // Поиск тестов, содержащих определенное слово в названии
    List<Test> findByTitleContainingIgnoreCase(String keyword);

    // Получение всех тестов, отсортированных по дате создания
    List<Test> findAllByOrderByCreatedAtDesc();
    List<Test> findAllByOrderByTestId();
}