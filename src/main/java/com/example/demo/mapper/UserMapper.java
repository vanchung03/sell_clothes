package com.example.demo.mapper;


import com.example.demo.dto.auth.UserRegisterDTO;
import com.example.demo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "passwordHash", ignore = true) // Sẽ mã hóa sau
    User toEntity(UserRegisterDTO dto);
}
