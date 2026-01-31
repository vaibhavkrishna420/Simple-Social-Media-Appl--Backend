package com.smedia.smedia.controller;

import com.smedia.smedia.dto.MessageDto;
import com.smedia.smedia.model.Message;
import com.smedia.smedia.model.User;
import com.smedia.smedia.repository.MessageRepo;
import com.smedia.smedia.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "http://localhost:3000")
public class MessageController {

    @Autowired
    private MessageRepo messageRepo;   

    @Autowired
    private UserRepository userRepo;   

    // ===================== SEND MESSAGE =====================
    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody MessageDto dto) {

        if (dto.getSenderId() == null ||
            dto.getReceiverId() == null ||
            dto.getContent() == null ||
            dto.getContent().isBlank()) {
            return ResponseEntity.badRequest().body("Invalid message data.");
        }

        // Validate users (MySQL)
        User sender = userRepo.findById(dto.getSenderId()).orElse(null);
        User receiver = userRepo.findById(dto.getReceiverId()).orElse(null);

        if (sender == null || receiver == null) {
            return ResponseEntity.badRequest().body("Sender or receiver not found.");
        }

        // Create Mongo message
        Message message = new Message();
        message.setSenderId(sender.getId());
        message.setReceiverId(receiver.getId());
        message.setContent(dto.getContent());

        messageRepo.save(message);
        return ResponseEntity.ok("Message sent successfully");
    }

    // ===================== GET CHAT BETWEEN TWO USERS =====================
    @GetMapping("/{user1Id}/{user2Id}")
    public ResponseEntity<List<Message>> getChat(@PathVariable Long user1Id,
                                                 @PathVariable Long user2Id) {

        List<Message> chatHistory =
                messageRepo.findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByTimestampAsc(
                        user1Id, user2Id,
                        user2Id, user1Id
                );

        return ResponseEntity.ok(chatHistory);
    }
}
