package com.example.InterviewBot.model;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String role; // Например, "user", "admin"

    @Column(name="created_at",nullable = false)
    private Timestamp createdAt;

    @Column(name = "tg_id",nullable = false,unique = true)
    private Long tgId;
}
