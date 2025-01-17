package com.example.demo.mapper;

import com.example.demo.dto.ProductVariantDTO;
import com.example.demo.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductVariantMapper {
    // Chuyển từ ProductVariant entity sang ProductVariantDTO
    @Mapping(target = "productId", source = "product.productId")
    ProductVariantDTO toDTO(ProductVariant productVariant);

    // Chuyển từ ProductVariantDTO sang ProductVariant entity
    @Mapping(target = "product.productId", source = "productId")
    ProductVariant toEntity(ProductVariantDTO productVariantDTO);
}
