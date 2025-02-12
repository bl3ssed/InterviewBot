package com.example.InterviewBot.repository;

import com.example.InterviewBot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Поиск пользователя по имени пользователя
    Optional<User> findByUsername(String username);

    Optional<User> findByUserId(Long userId);

    Optional<User> findByTgId(Long tgId);

    // Поиск пользователей по роли
    List<User> findByRole(String role);

    // Поиск пользователей, зарегистрированных после определенной даты
    List<User> findByCreatedAtAfter(Timestamp date);

    boolean existsByUsername(String username);
    boolean existsByTgId(Long tgId);
}