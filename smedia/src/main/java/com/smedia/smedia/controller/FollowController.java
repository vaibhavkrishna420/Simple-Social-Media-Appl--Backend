package com.smedia.smedia.controller;

import com.smedia.smedia.model.Follow;
import com.smedia.smedia.model.User;
import com.smedia.smedia.repository.FollowRepository;
import com.smedia.smedia.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/follow")
@CrossOrigin(origins = "http://localhost:3000")
public class FollowController {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    // ===================== FOLLOW =====================
    @Transactional
    @PostMapping("/{usernameToFollow}")
    public ResponseEntity<?> followUser(@AuthenticationPrincipal UserDetails currentUser,
                                        @PathVariable String usernameToFollow) {

        if (currentUser.getUsername().equals(usernameToFollow)) {
            return ResponseEntity.badRequest().body("❌ You cannot follow yourself");
        }

        User follower = userRepository.findByUsername(currentUser.getUsername()).orElse(null);
        User following = userRepository.findByUsername(usernameToFollow).orElse(null);

        if (follower == null || following == null) {
            return ResponseEntity.badRequest().body("❌ Invalid usernames");
        }

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            return ResponseEntity.badRequest().body("❌ You are already following this user");
        }

        followRepository.save(new Follow(follower, following));
        return ResponseEntity.ok("✅ Now following " + usernameToFollow);
    }

    // ===================== UNFOLLOW =====================
    @Transactional
    @DeleteMapping("/{usernameToUnfollow}")
    public ResponseEntity<?> unfollowUser(@AuthenticationPrincipal UserDetails currentUser,
                                          @PathVariable String usernameToUnfollow) {

        User follower = userRepository.findByUsername(currentUser.getUsername()).orElse(null);
        User following = userRepository.findByUsername(usernameToUnfollow).orElse(null);

        if (follower == null || following == null) {
            return ResponseEntity.badRequest().body("❌ Invalid usernames");
        }

        followRepository.deleteByFollowerAndFollowing(follower, following);
        return ResponseEntity.ok("✅ Unfollowed " + usernameToUnfollow);
    }

    // ===================== FOLLOWING USERNAMES =====================
    @Transactional(readOnly = true)
    @GetMapping("/following-names")
    public ResponseEntity<?> getFollowingUsernames(@AuthenticationPrincipal UserDetails currentUser) {

        User follower = userRepository.findByUsername(currentUser.getUsername()).orElse(null);
        if (follower == null) {
            return ResponseEntity.status(401).body("❌ Unauthorized");
        }

        List<String> following = followRepository.findByFollower(follower).stream()
                .map(f -> f.getFollowing().getUsername())
                .collect(Collectors.toList());

        return ResponseEntity.ok(following);
    }

    // ===================== FOLLOWING LIST =====================
    @Transactional(readOnly = true)
    @GetMapping("/following")
    public ResponseEntity<List<Map<String, Object>>> getFollowingList(
            @AuthenticationPrincipal UserDetails userDetails) {

        Optional<User> userOpt = userRepository.findByUsername(userDetails.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User currentUser = userOpt.get();

        List<Map<String, Object>> result = followRepository.findByFollower(currentUser).stream()
                .map(follow -> {
                    User followedUser = follow.getFollowing();
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", followedUser.getId());
                    userMap.put("username", followedUser.getUsername());
                    return userMap;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // ===================== MUTUAL FOLLOW =====================
    @GetMapping("/isMutual/{username}")
    public ResponseEntity<?> isMutualFollow(@AuthenticationPrincipal UserDetails currentUser,
                                            @PathVariable String username) {

        Optional<User> user1Opt = userRepository.findByUsername(currentUser.getUsername());
        Optional<User> user2Opt = userRepository.findByUsername(username);

        if (user1Opt.isEmpty() || user2Opt.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Invalid usernames");
        }

        User user1 = user1Opt.get();
        User user2 = user2Opt.get();

        boolean followsUser = followRepository.existsByFollowerAndFollowing(user1, user2);
        boolean followedByUser = followRepository.existsByFollowerAndFollowing(user2, user1);

        Map<String, Object> result = new HashMap<>();
        result.put("youFollow", followsUser);
        result.put("theyFollowYou", followedByUser);
        result.put("isMutual", followsUser && followedByUser);

        return ResponseEntity.ok(result);
    }

    // ===================== FOLLOWERS =====================
    @Transactional(readOnly = true)
    @GetMapping("/followers")
    public ResponseEntity<?> getFollowers(@AuthenticationPrincipal UserDetails currentUser) {

        User following = userRepository.findByUsername(currentUser.getUsername()).orElse(null);
        if (following == null) {
            return ResponseEntity.status(401).body("❌ Unauthorized");
        }

        List<String> followers = followRepository.findByFollowing(following).stream()
                .map(f -> f.getFollower().getUsername())
                .collect(Collectors.toList());

        return ResponseEntity.ok(followers);
    }
}
