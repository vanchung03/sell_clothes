package com.example.demo.service;

import com.example.demo.dto.ReviewReplyDTO;
import com.example.demo.entity.ReviewReply;
import com.example.demo.mapper.ReviewReplyMapper;
import com.example.demo.repository.ReviewReplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewReplyService {

    @Autowired
    private ReviewReplyRepository reviewReplyRepository;

    @Autowired
    private ReviewReplyMapper reviewReplyMapper;

    // 1) Lấy danh sách tất cả reply
    public List<ReviewReplyDTO> getAllReplies() {
        List<ReviewReply> entities = reviewReplyRepository.findAll();
        return entities.stream()
                .map(reviewReplyMapper::toDto)
                .toList();
    }

    // 2) Lấy reply theo ID
    public ReviewReplyDTO getReplyById(Long replyId) {
        return reviewReplyRepository.findById(replyId)
                .map(reviewReplyMapper::toDto)
                .orElse(null);
    }

    // 2') Lấy danh sách reply theo reviewId
    public List<ReviewReplyDTO> getRepliesByReviewId(Long reviewId) {
        List<ReviewReply> replies = reviewReplyRepository.findAllByReviewId(reviewId);
        return replies.stream()
                .map(reviewReplyMapper::toDto)
                .toList();
    }

    // 3) Tạo reply
    public ReviewReplyDTO createReply(ReviewReplyDTO dto) {
        // Có thể kiểm tra reviewId và userId có hợp lệ không (nếu muốn)
        ReviewReply entity = reviewReplyMapper.toEntity(dto);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        ReviewReply saved = reviewReplyRepository.save(entity);
        return reviewReplyMapper.toDto(saved);
    }

    // 4) Cập nhật reply
    public ReviewReplyDTO updateReply(Long replyId, ReviewReplyDTO dto) {
        ReviewReply existing = reviewReplyRepository.findById(replyId).orElse(null);
        if (existing == null) {
            return null; // hoặc throw exception
        }
        existing.setReplyContent(dto.getReplyContent());
        existing.setUpdatedAt(LocalDateTime.now());
        ReviewReply updated = reviewReplyRepository.save(existing);
        return reviewReplyMapper.toDto(updated);
    }

    // 5) Xoá reply
    public void deleteReply(Long replyId) {
        reviewReplyRepository.deleteById(replyId);
    }
}
