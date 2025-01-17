package com.example.demo.mapper;

import com.example.demo.dto.ProductDTO;
import com.example.demo.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "categoryId", source = "category.categoryId")
    @Mapping(target = "brandId", source = "brand.brandId")
    ProductDTO toDTO(Product product);

    @Mapping(target = "category.categoryId", source = "categoryId")
    @Mapping(target = "brand.brandId", source = "brandId")
    Product toEntity(ProductDTO productDTO);
}
