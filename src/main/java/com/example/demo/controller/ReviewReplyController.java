package com.example.demo.controller;

import com.example.demo.dto.ReviewReplyDTO;
import com.example.demo.service.ReviewReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/review-replies")
@RequiredArgsConstructor
public class ReviewReplyController {

    private final ReviewReplyService reviewReplyService;

    // 1) Lấy danh sách tất cả reply
    @GetMapping
    public ResponseEntity<List<ReviewReplyDTO>> getAllReplies() {
        return ResponseEntity.ok(reviewReplyService.getAllReplies());
    }

    // 2) Lấy chi tiết 1 reply theo ID
    @GetMapping("/{replyId}")
    public ResponseEntity<ReviewReplyDTO> getReplyById(@PathVariable Long replyId) {
        ReviewReplyDTO dto = reviewReplyService.getReplyById(replyId);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    // 2') Lấy danh sách reply theo reviewId
    @GetMapping("/review/{reviewId}")
    public ResponseEntity<List<ReviewReplyDTO>> getRepliesByReviewId(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewReplyService.getRepliesByReviewId(reviewId));
    }

    // 3) Tạo mới 1 reply
    @PostMapping
    public ResponseEntity<ReviewReplyDTO> createReply(@RequestBody ReviewReplyDTO dto) {
        ReviewReplyDTO created = reviewReplyService.createReply(dto);
        return ResponseEntity.ok(created);
    }

    // 4) Cập nhật 1 reply
    @PutMapping("/{replyId}")
    public ResponseEntity<ReviewReplyDTO> updateReply(@PathVariable Long replyId,
                                                      @RequestBody ReviewReplyDTO dto) {
        ReviewReplyDTO updated = reviewReplyService.updateReply(replyId, dto);
        if (updated == null) {
            // Có thể throw exception hoặc trả về 404
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    // 5) Xoá 1 reply
    @DeleteMapping("/{replyId}")
    public ResponseEntity<Void> deleteReply(@PathVariable Long replyId) {
        reviewReplyService.deleteReply(replyId);
        return ResponseEntity.noContent().build(); // 204
    }
}
