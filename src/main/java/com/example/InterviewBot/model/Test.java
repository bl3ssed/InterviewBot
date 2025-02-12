package com.example.InterviewBot.model;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "tests")
@Data
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer testId;

    @Column(nullable = false,name = "title")
    private String title;

    @Column(columnDefinition = "TEXT",name = "description")
    private String description;

    @Column(nullable = false,name = "created_at")
    private Timestamp createdAt;

    @Column(nullable = false,name = "updated_at")
    private Timestamp updatedAt;
}