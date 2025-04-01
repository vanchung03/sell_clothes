package com.example.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ReviewReplyDTO {

    private Long replyId;
    private Long reviewId;
    private Long userId;
    private String replyContent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
