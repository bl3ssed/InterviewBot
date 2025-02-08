package com.example.InterviewBot.service;

import com.example.InterviewBot.model.Test;
import com.example.InterviewBot.model.User;
import com.example.InterviewBot.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TestService {

    @Autowired
    private TestRepository testRepository;

    // Создание нового теста
    public Test createTest(String title, String description) {
        Test test = new Test();
        test.setTitle(title);
        test.setDescription(description);
        test.setCreatedAt(java.time.LocalDateTime.now().toString());
        test.setUpdatedAt(java.time.LocalDateTime.now().toString());
        return testRepository.save(test);
    }

    // Обновление теста
    public Test updateTest(Integer testId, String title, String description) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));
        test.setTitle(title);
        test.setDescription(description);
        test.setUpdatedAt(java.time.LocalDateTime.now().toString());
        return testRepository.save(test);
    }

    // Удаление теста
    public void deleteTest(Integer testId) {
        testRepository.deleteById(testId);
    }

    // Получение теста по названию
    public Test getTestByTitle(String title) {
        return testRepository.findByTitle(title);
    }

    // Получение всех тестов, содержащих определенное слово в названии
    public List<Test> getTestsByKeyword(String keyword) {
        return testRepository.findByTitleContainingIgnoreCase(keyword);
    }

    // Получение всех тестов, отсортированных по дате создания
    public List<Test> getAllTestsOrderedByDate() {
        return testRepository.findAllByOrderByCreatedAtDesc();
    }
    public Optional<Test> getTestById(Integer testId) {
        return TestRepository.findByTestId(testId);
    }
}