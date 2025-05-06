package com.example.demo.dto.groq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroqStreamResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    private Delta delta;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Delta {
        private String role;
        private String content;
    }
}