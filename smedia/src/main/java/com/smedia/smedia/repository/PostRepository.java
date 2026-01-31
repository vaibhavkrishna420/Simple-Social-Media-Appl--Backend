package com.smedia.smedia.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.smedia.smedia.model.Post;

import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {

    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Post> findAllByOrderByCreatedAtDesc();
}
