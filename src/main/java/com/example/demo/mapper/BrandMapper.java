package com.example.demo.mapper;

import com.example.demo.dto.BrandDTO;
import com.example.demo.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    // Ánh xạ từ Entity sang DTO
    @Mapping(target = "brandId", source = "brandId")
    BrandDTO toDTO(Brand brand);

    @Mapping(target = "brandId", source = "brandId")
    Brand toEntity(BrandDTO brandDTO);

    // Ánh xạ danh sách Entity sang danh sách DTO
    List<BrandDTO> toDTOs(List<Brand> brands);

    // Ánh xạ danh sách DTO sang danh sách Entity
    List<Brand> toEntities(List<BrandDTO> brandDTOs);
}
