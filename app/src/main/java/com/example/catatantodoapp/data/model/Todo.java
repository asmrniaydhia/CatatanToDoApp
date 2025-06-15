package com.example.catatantodoapp.data.model;

import java.io.Serializable;

public class Todo implements Serializable {
    private String id;
    private String title;
    private boolean completed;
    private long timestamp;
    private String userId;
    private long reminderTime; // Tambahkan field ini

    public Todo() {
    }

    public Todo(String id, String title, boolean completed, long timestamp, String userId, long reminderTime) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.timestamp = timestamp;
        this.userId = userId;
        this.reminderTime = reminderTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(long reminderTime) {
        this.reminderTime = reminderTime;
    }
}