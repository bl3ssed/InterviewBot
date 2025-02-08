package com.example.InterviewBot.model;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "answers")
@Data
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer answerId;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String answerText;

    @Column(nullable = false)
    private Integer number; // Порядковый номер ответа

    @Column(nullable = false)
    private Boolean isCorrect; // Флаг, указывающий, является ли ответ правильным
}