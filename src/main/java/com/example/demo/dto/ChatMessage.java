package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String id;
    private String content;
    private String sender;
    private MessageType type;
    private LocalDateTime timestamp = LocalDateTime.now();

    public enum MessageType {
        CHAT, JOIN, LEAVE, TYPING
    }
}