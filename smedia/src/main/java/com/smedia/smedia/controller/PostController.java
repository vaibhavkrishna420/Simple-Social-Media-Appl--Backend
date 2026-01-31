package com.smedia.smedia.controller;

import com.smedia.smedia.model.Post;
import com.smedia.smedia.model.User;
import com.smedia.smedia.repository.PostRepository;
import com.smedia.smedia.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:3000")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    // ===================== GET ALL POSTS =====================   
    @GetMapping
    public List<Map<String, Object>> getAllPosts() {

        return postRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(post -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", post.getId());
                map.put("content", post.getContent());
                map.put("createdAt", post.getCreatedAt());
                map.put("userId", post.getUserId());
                map.put("likeCount", post.getLikeCount());
                map.put("commentCount", post.getCommentCount());

                userRepository.findById(post.getUserId())
                    .ifPresent(user -> map.put("username", user.getUsername()));

                return map;
            })
            .toList();
    }

    // ===================== CREATE POST =====================
    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal(expression = "username") String username
    ) {
        if (username == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();
        post.setContent(body.get("content"));
        post.setUserId(user.getId());          // ✅ ONLY userId
        post.setCreatedAt(LocalDateTime.now());

        postRepository.save(post);
        return ResponseEntity.ok(post);
    }

    // ===================== LIKE POST =====================
    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(
            @PathVariable String postId,
            @AuthenticationPrincipal(expression = "username") String username
    ) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // ✅ Prevent duplicate likes
        boolean alreadyLiked = post.getLikes()
                .stream()
                .anyMatch(like -> like.getUserId().equals(user.getId()));

        if (!alreadyLiked) {
            post.addLike(user.getId());
            postRepository.save(post);
        }

        return ResponseEntity.ok("Post liked");
    }

    // ===================== UNLIKE POST =====================
    @PostMapping("/{postId}/unlike")
    public ResponseEntity<?> unlikePost(
            @PathVariable String postId,
            @AuthenticationPrincipal(expression = "username") String username
    ) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.removeLike(user.getId());
        postRepository.save(post);

        return ResponseEntity.ok("Post unliked");
    }
}
