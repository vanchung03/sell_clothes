package com.example.demo.mapper;

import com.example.demo.dto.CategoryDTO;
import com.example.demo.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    // Ánh xạ từ Entity sang DTO
    @Mapping(target = "parentId", source = "parentId")
     CategoryDTO toDTO(Category category);

    // Ánh xạ từ DTO sang Entity
    @Mapping(target = "parentId", source = "parentId")
    Category toEntity(CategoryDTO categoryDTO) ;
}
