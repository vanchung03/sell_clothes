package com.example.demo.mapper;

import com.example.demo.dto.FavoriteProductDTO;
import com.example.demo.entity.FavoriteProduct;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FavoriteProductMapper {

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "product.productId", target = "productId")
    FavoriteProductDTO toDto(FavoriteProduct entity);

    @InheritInverseConfiguration
    @Mapping(target = "user", ignore = true)      // Sẽ set trong service
    @Mapping(target = "product", ignore = true)   // Sẽ set trong service
    FavoriteProduct toEntity(FavoriteProductDTO dto);
}
