package com.example.InterviewBot.service;

import com.example.InterviewBot.model.Statistics;
import com.example.InterviewBot.model.User;
import com.example.InterviewBot.model.Test;
import com.example.InterviewBot.repository.StatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class StatisticsService {

    @Autowired
    private StatisticsRepository statisticsRepository;

    // Сохранение статистики
    public Statistics saveStatistics(User user, Test test, int score, int totalQuestions) {
        Statistics statistics = new Statistics();
        statistics.setUser(user);
        statistics.setTest(test);
        statistics.setScore(score);
        statistics.setTotalQuestions(totalQuestions);
        statistics.setCompletedAt(Timestamp.from(Instant.from(Instant.now())));
        return statisticsRepository.save(statistics);
    }

    public Statistics updateStatistics(User user, Test test, int score, int totalQuestions) {
        Statistics statistics = statisticsRepository.findByUserAndTest(user,test);
        statistics.setScore(score);
        statistics.setTotalQuestions(totalQuestions);
        statistics.setCompletedAt(Timestamp.from(Instant.from(Instant.now())));
        return statisticsRepository.save(statistics);
    }

    // Получение статистики по пользователю
    public List<Statistics> getStatisticsByUser(User user) {
        return statisticsRepository.findByUser(user);
    }

    // Получение статистики по тесту
    public List<Statistics> getStatisticsByTest(Test test) {
        return statisticsRepository.findByTest(test);
    }

    // Получение статистики по пользователю и тесту
    public Statistics getStatisticsByUserAndTest(User user, Test test) {
        return statisticsRepository.findByUserAndTest(user, test);
    }

    // Получение всех статистик, отсортированных по дате завершения
    public List<Statistics> getAllStatisticsOrderedByDate() {
        return statisticsRepository.findAllByOrderByCompletedAtDesc();
    }
}
