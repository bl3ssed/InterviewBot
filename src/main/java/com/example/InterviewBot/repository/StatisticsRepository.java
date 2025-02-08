package com.example.InterviewBot.repository;

import com.example.InterviewBot.model.Statistics;
import com.example.InterviewBot.model.User;
import com.example.InterviewBot.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatisticsRepository extends JpaRepository<Statistics, Long> {

    // Поиск статистики по пользователю
    List<Statistics> findByUser(User user);

    // Поиск статистики по тесту
    List<Statistics> findByTest(Test test);

    // Поиск статистики по пользователю и тесту
    List<Statistics> findByUserAndTest(User user, Test test);

    // Получение всех статистик, отсортированных по дате завершения
    List<Statistics> findAllByOrderByCompletedAtDesc();
}