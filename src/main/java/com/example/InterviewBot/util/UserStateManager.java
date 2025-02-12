package com.example.InterviewBot.util;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserStateManager {

    private Map<Long, String> userStates = new HashMap<>();

    public Map<Long, String> getUserStates() {
        return userStates;
    }

    public void setUserStates(Map<Long, String> userStates) {
        this.userStates = userStates;
    }
}