package com.example.InterviewBot.model;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tests")
@Data
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer testId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String createdAt;

    @Column(nullable = false)
    private String updatedAt;
}