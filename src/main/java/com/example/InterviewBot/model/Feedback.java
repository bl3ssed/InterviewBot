package com.example.InterviewBot.model;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "feedback")
@Data
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String feedbackText;

    @Column(nullable = false)
    private Integer rate; // Оценка отзыва

    @Column(nullable = false)
    private Timestamp submittedAt;

}