package com.example.InterviewBot.model;

public class TestDto {
    private final long testId;
    private final String title;

    public TestDto(long testId, String title) {
        this.testId = testId;
        this.title = title;
    }

    public long getTestId() { return testId; }
    public String getTitle() { return title; }
}