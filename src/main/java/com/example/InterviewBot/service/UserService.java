package com.example.InterviewBot.service;

import com.example.InterviewBot.model.User;
import com.example.InterviewBot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    public UserService() {
    }

    @Autowired
    private UserRepository userRepository;

    // Регистрация нового пользователя
    public User registerUser1(String username, String firstName, String role, Long tgId) {
        User user = new User();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setRole(role);
        user.setCreatedAt(Timestamp.from(Instant.now()));
        user.setTgId(tgId);
        return userRepository.save(user);
    }

    public User registerUser(String username, String firstName, String role, Long tgId) {
        if (username == null || username.isBlank() || firstName == null || firstName.isBlank() || role == null || tgId == null) {
            throw new IllegalArgumentException("Все поля должны быть заполнены.");
        }

        if (userRepository.existsByUsername(username) || userRepository.existsByTgId(tgId)) {
            throw new IllegalStateException("Пользователь с таким username или tgId уже существует.");
        }

        User user = new User();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setRole(role);
        user.setCreatedAt(Timestamp.from(Instant.now())); // Используем Timestamp вместо String
        user.setTgId(tgId);

        return userRepository.save(user);
    }


    // Обновление данных пользователя
    public User updateUser(Long tgId,String username, String firstName, String role) {
        User user = userRepository.findByTgId(tgId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setRole(role);
        return userRepository.save(user);
    }

    // Получение пользователя по ID
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    // Получение пользователя по имени пользователя
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Получение всех пользователей с определенной ролью
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    // Другие методы, такие как удаление пользователя и т.д.
}