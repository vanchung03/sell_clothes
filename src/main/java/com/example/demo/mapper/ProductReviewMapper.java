package com.example.demo.mapper;

import com.example.demo.dto.ProductReviewDTO;
import com.example.demo.entity.ProductReview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductReviewMapper {

    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "productId", source = "product.productId")
    ProductReviewDTO toDTO(ProductReview review);

    @Mapping(target = "user.userId", source = "userId")
    @Mapping(target = "product.productId", source = "productId")
    ProductReview toEntity(ProductReviewDTO reviewDTO);
}
