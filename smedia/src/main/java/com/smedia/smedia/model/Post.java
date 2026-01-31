package com.smedia.smedia.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "posts")
public class Post {

    @Id
    private String id;

    private String content;

    private LocalDateTime createdAt;

    // 🔥 VERY IMPORTANT: only store userId
    private Long userId;

    // Internal lists (not exposed directly)
    @JsonIgnore
    private List<Comment> comments = new ArrayList<>();

    @JsonIgnore
    private List<Like> likes = new ArrayList<>();

    // ===================== CONSTRUCTORS =====================
    public Post() {
        this.createdAt = LocalDateTime.now();
    }

    public Post(String id, String content, LocalDateTime createdAt, Long userId,
                List<Like> likes, List<Comment> comments) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.userId = userId;
        this.likes = likes != null ? likes : new ArrayList<>();
        this.comments = comments != null ? comments : new ArrayList<>();
    }

    // ===================== COUNTS (EXPOSED TO FRONTEND) =====================
    @JsonProperty("likeCount")
    public int getLikeCount() {
        return likes.size();
    }

    @JsonProperty("commentCount")
    public int getCommentCount() {
        return comments.size();
    }

    // ===================== LIKE HELPERS =====================
    public void addLike(Long userId) {
        // prevent duplicate likes
        boolean alreadyLiked = likes.stream()
                .anyMatch(like -> like.getUserId().equals(userId));

        if (!alreadyLiked) {
            likes.add(new Like(userId, LocalDateTime.now()));
        }
    }

    public void removeLike(Long userId) {
        likes.removeIf(like -> like.getUserId().equals(userId));
    }

    // ===================== GETTERS & SETTERS =====================
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes != null ? likes : new ArrayList<>();
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments != null ? comments : new ArrayList<>();
    }
}
