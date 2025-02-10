package com.example.InterviewBot.service;

import com.example.InterviewBot.model.Answer;
import com.example.InterviewBot.model.Question;
import com.example.InterviewBot.model.Test;
import com.example.InterviewBot.repository.AnswerRepository;
import com.example.InterviewBot.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnswerService {

    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private QuestionService questionService;

    // Создание нового ответа
    public Answer createAnswer(Integer questionId, String answerText, Integer number, Boolean isCorrect) {
        Answer answer = new Answer();
        Optional<Question> question = questionService.getByQuestionId(questionId);
        answer.setQuestion(question.orElse(null));
        answer.setAnswerText(answerText);
        answer.setNumber(number);
        answer.setIsCorrect(isCorrect);
        return answerRepository.save(answer);
    }

    // Обновление ответа
    public Answer updateAnswer(Integer answerId, String answerText, Integer number, Boolean isCorrect) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));
        answer.setAnswerText(answerText);
        answer.setNumber(number);
        answer.setIsCorrect(isCorrect);
        return answerRepository.save(answer);
    }

    // Удаление ответа
    public void deleteAnswer(Integer answerId) {
        answerRepository.deleteById(answerId);
    }

    // Получение всех ответов для вопроса
    public List<Answer> getAnswersByQuestion(Integer questionId) {
        return answerRepository.findByQuestionId(questionId);
    }

    // Другие методы, если необходимо
}