package com.example.demo.controller;

import com.example.demo.service.GroqService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.demo.dto.ChatMessage;

import java.time.LocalDateTime;
import java.util.UUID;
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final GroqService groqService;

    @Autowired
    public ChatController(SimpMessagingTemplate messagingTemplate, GroqService groqService) {
        this.messagingTemplate = messagingTemplate;
        this.groqService = groqService;
    }

    @PostMapping("/app/chat.sendMessage/{sessionId}")
    public void sendMessage(@PathVariable String sessionId, @RequestBody ChatMessage chatMessage) {
        // Generate ID if not present
        if (chatMessage.getId() == null || chatMessage.getId().isEmpty()) {
            chatMessage.setId(UUID.randomUUID().toString());
        }

        // Set timestamp if not present
        if (chatMessage.getTimestamp() == null) {
            chatMessage.setTimestamp(LocalDateTime.now());
        }

        // Send the initial message to the specific chat session topic immediately
        messagingTemplate.convertAndSend("/topic/chat/" + sessionId, chatMessage);

        // Process the message with AI if it’s a user message
        if ("USER".equalsIgnoreCase(chatMessage.getSender()) || !"AI".equalsIgnoreCase(chatMessage.getSender())) {
            groqService.processUserMessage(sessionId, chatMessage.getSender(), chatMessage.getContent());
        }
    }
}
