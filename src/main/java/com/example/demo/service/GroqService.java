package com.example.demo.service;// src/main/java/com/example/aichat/service/GroqService.java

import com.example.demo.dto.ChatMessage;
import com.example.demo.dto.groq.GroqRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class GroqService {

    private final WebClient webClient;
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    public GroqService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public void processUserMessage(String sessionId, String username, String message) {
        // Create a request to Groq API
        GroqRequest request = new GroqRequest();
        request.setStream(true);

        // Add user message to the request
        GroqRequest.Message userMessage = new GroqRequest.Message("user", message);
        request.setMessages(Collections.singletonList(userMessage));

        // Send message to Groq API and process the streaming response
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // First, acknowledge that we received the message
        ChatMessage ackMessage = new ChatMessage(
                UUID.randomUUID().toString(),
                "Processing your request...",
                "system",
                ChatMessage.MessageType.CHAT,
                LocalDateTime.now()
        );
        messagingTemplate.convertAndSend("/topic/chat/" + sessionId, ackMessage);

        // Stream buffer to accumulate AI response
        final StringBuilder responseBuffer = new StringBuilder();
        final String aiMessageId = UUID.randomUUID().toString();

        // Call Groq API with streaming enabled
        webClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class)  // Get raw SSE data
                .subscribe(
                        chunk -> {
                            try {
                                // Process and extract content from chunk
                                // This is simplified - in reality you'd need to parse the SSE format
                                if (chunk.contains("content")) {
                                    // Parse chunk and extract content
                                    String content = extractContent(chunk);
                                    if (!content.isEmpty()) {
                                        responseBuffer.append(content);

                                        // Send incremental update through WebSocket immediately
                                        ChatMessage incrementalMessage = new ChatMessage(
                                                aiMessageId,
                                                responseBuffer.toString(),
                                                "AI",
                                                ChatMessage.MessageType.CHAT,
                                                LocalDateTime.now()
                                        );
                                        messagingTemplate.convertAndSend("/topic/chat/" + sessionId, incrementalMessage);
                                    }
                                }
                            } catch (Exception e) {
                                sendErrorMessage(sessionId, "Error processing AI response: " + e.getMessage());
                            }
                        },
                        error -> sendErrorMessage(sessionId, "Error from AI service: " + error.getMessage()),
                        () -> {
                            // Send final complete message when stream completes
                            ChatMessage completeMessage = new ChatMessage(
                                    aiMessageId,
                                    responseBuffer.toString(),
                                    "AI",
                                    ChatMessage.MessageType.CHAT,
                                    LocalDateTime.now()
                            );
                            messagingTemplate.convertAndSend("/topic/chat/" + sessionId, completeMessage);
                        }
                );
    }


    private String extractContent(String chunkData) {
        // This is a simplified extraction - in a real implementation,
        // you would use proper JSON parsing here
        if (chunkData.contains("\"content\":")) {
            int start = chunkData.indexOf("\"content\":") + 11;
            int end = chunkData.indexOf("\"", start);
            if (start > 0 && end > start) {
                return chunkData.substring(start, end);
            }
        }
        return "";
    }

    private void sendErrorMessage(String sessionId, String errorMessage) {
        ChatMessage errorResponse = new ChatMessage(
                UUID.randomUUID().toString(),
                errorMessage,
                "system",
                ChatMessage.MessageType.CHAT,
                LocalDateTime.now()
        );
        messagingTemplate.convertAndSend("/topic/chat/" + sessionId, errorResponse);
    }
}