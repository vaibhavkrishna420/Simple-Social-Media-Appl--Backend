package com.smedia.smedia.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.smedia.smedia.model.Comment;

public interface CommentRepository extends MongoRepository<Comment, String> {

    List<Comment> findByPostIdOrderByCreatedAtAsc(String postId);
}
