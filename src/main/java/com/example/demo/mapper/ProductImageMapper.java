package com.example.demo.mapper;

import com.example.demo.dto.ProductImageDTO;
import com.example.demo.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {

    @Mapping(target = "productId", source = "product.productId")
    ProductImageDTO toDTO(ProductImage productImage);

    @Mapping(target = "product.productId", source = "productId")
    ProductImage toEntity(ProductImageDTO productImageDTO);
}
