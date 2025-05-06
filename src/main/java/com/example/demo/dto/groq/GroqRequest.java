package com.example.demo.dto.groq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroqRequest {
    private String model = "llama3-8b-8192";  // Default model
    private List<Message> messages;
    private double temperature = 0.7;
    private boolean stream = true;  // For streaming responses

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role; // "user" or "assistant"
        private String content;
    }
}