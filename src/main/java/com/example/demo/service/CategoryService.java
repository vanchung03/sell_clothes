package com.example.demo.service;

import com.example.demo.dto.CategoryDTO;
import com.example.demo.entity.Category;
import com.example.demo.mapper.CategoryMapper;
import com.example.demo.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryMapper categoryMapper;

    // Tạo mới Category
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = categoryMapper.toEntity(categoryDTO);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDTO(savedCategory);
    }

    // Cập nhật Category
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + id));

        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        category.setSlug(categoryDTO.getSlug());
        category.setStatus(categoryDTO.isStatus());

        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toDTO(updatedCategory);
    }

    // Lấy tất cả Category
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Tìm kiếm Category theo tên
    public List<CategoryDTO> getCategoryByName(String name) {
        List<Category> categories = categoryRepository.findByName(name);
        return categories.stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Xóa Category
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
