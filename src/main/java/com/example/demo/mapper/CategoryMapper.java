package com.example.demo.mapper;

import com.example.demo.dto.CategoryDTO;
import com.example.demo.entity.Category;
import org.mapstruct.Mapper;
@Mapper(componentModel = "spring")
public interface CategoryMapper {

     CategoryDTO toDTO(Category category);
    Category toEntity(CategoryDTO categoryDTO) ;
}
