package com.example.InterviewBot.service;

import com.example.InterviewBot.model.Question;
import com.example.InterviewBot.model.Test;
import com.example.InterviewBot.model.User;
import com.example.InterviewBot.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;
    private TestService testService;

    // Создание нового вопроса
    public Question createQuestion(Integer testId, String questionText) {
        Question question = new Question();
        Optional<Test> test = testService.getTestById(testId);
        question.setTest(test.orElse(null));
        question.setQuestionText(questionText);
        question.setCreatedAt(java.time.LocalDateTime.now().toString());
        question.setUpdatedAt(java.time.LocalDateTime.now().toString());
        return questionRepository.save(question);
    }

    // Обновление вопроса
    public Question updateQuestion(Integer questionId, String questionText) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        question.setQuestionText(questionText);
        question.setUpdatedAt(java.time.LocalDateTime.now().toString());
        return questionRepository.save(question);
    }

    // Удаление вопроса
    public void deleteQuestion(Integer questionId) {
        questionRepository.deleteById(questionId);
    }

    // Получение всех вопросов для теста
    public List<Question> getQuestionsByTest(Integer testId) {
        return questionRepository.findByTestId(testId);
    }

    public Optional<Question> getByQuestionId(Integer questionId) {
        return questionRepository.findById(questionId);
    }

    // Другие методы, если необходимо
}