package com.example.InterviewBot.service;

import com.example.InterviewBot.model.User;
import com.example.InterviewBot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    public UserService() {
    }

    @Autowired
    private UserRepository userRepository;

    // Регистрация нового пользователя
    public User registerUser(String username, String firstName, String role) {
        User user = new User();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setRole(role);
        return userRepository.save(user);
    }

    // Обновление данных пользователя
    public User updateUser(Long userId, String username, String firstName, String role) {
        User user = userRepository.findById(userId)
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