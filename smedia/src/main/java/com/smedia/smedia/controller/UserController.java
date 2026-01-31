package com.smedia.smedia.controller;

import com.smedia.smedia.model.User;
import com.smedia.smedia.model.Post;
import com.smedia.smedia.repository.PostRepository;
import com.smedia.smedia.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    // ===================== USER PROFILE =====================
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long id) {

        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        List<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(id);

        // Return only required user fields
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("username", user.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("user", userMap);
        response.put("posts", posts);

        return ResponseEntity.ok(response);
    }

    // ===================== SEARCH USERS =====================
    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam String query) {
        return userRepository.findByUsernameContainingIgnoreCase(query);
    }

    // ===================== GET USER BY USERNAME =====================
    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("username", user.getUsername());

        return ResponseEntity.ok(userMap);
    }
    // ===================== GET USERNAME BY ID =====================
    @GetMapping("/id/{id}")
    public Map<String, Object> getUsernameById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> res = new HashMap<>();
        res.put("id", user.getId());
        res.put("username", user.getUsername());

        return res;
    }

}

