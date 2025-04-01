package com.example.demo.repository;

import com.example.demo.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
    // Tìm kiếm danh mục có chứa từ khóa trong name (không phân biệt hoa thường)
    // Tìm kiếm danh mục theo từ khóa trong tên, không phân biệt hoa thường
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Category> searchByName(@Param("keyword") String keyword);

    // Tìm kiếm danh mục có chứa từ khóa "nam" hoặc "nữ"
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE '%nam%' OR LOWER(c.name) LIKE '%nữ%'")
    List<Category> searchByGenderKeywords();
    List<Category> findByName(String name);
}
