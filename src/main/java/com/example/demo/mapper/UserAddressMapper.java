package com.example.demo.mapper;

import com.example.demo.dto.UserAddressDTO;
import com.example.demo.entity.UserAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserAddressMapper {
    UserAddressMapper INSTANCE = Mappers.getMapper(UserAddressMapper.class);

    @Mapping(source = "user.userId", target = "userId")
    UserAddressDTO toDTO(UserAddress entity);

    @Mapping(source = "userId", target = "user.userId")
    UserAddress toEntity(UserAddressDTO dto);
}
