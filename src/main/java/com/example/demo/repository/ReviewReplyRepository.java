package com.example.demo.repository;

import com.example.demo.entity.ReviewReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewReplyRepository extends JpaRepository<ReviewReply, Long> {
    // Các phương thức mặc định CRUD đã có sẵn:
    // findById, save, deleteById, findAll, ...
    // Lấy danh sách reply theo reviewId
    List<ReviewReply> findAllByReviewId(Long reviewId);
}
