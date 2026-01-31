package com.smedia.smedia.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Like {

    private Long userId;        
    private LocalDateTime createdAt;

    public Like() {
        this.createdAt = LocalDateTime.now();
    }

    public Like(Long userId, LocalDateTime createdAt) {
        this.userId = userId;
        this.createdAt = createdAt;
    }

    // --- Getters & Setters ---

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Like)) return false;
        Like other = (Like) obj;
        return Objects.equals(userId, other.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return "Like{" +
                "userId=" + userId +
                ", createdAt=" + createdAt +
                '}';
    }
}
