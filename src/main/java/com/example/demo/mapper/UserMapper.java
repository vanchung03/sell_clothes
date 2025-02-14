package com.example.demo.mapper;

import com.example.demo.dto.UserDTO;
import com.example.demo.dto.auth.RegisterRequest;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.enums.RoleName;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "passwordHash", ignore = true) // Sẽ mã hóa sau
    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRolesToString")
    UserDTO toDTo(User user);

    @Mapping(target = "passwordHash", ignore = true) // Sẽ mã hóa sau
    User toEntity(RegisterRequest dto);

    // Phương thức ánh xạ tùy chỉnh để chuyển đổi Set<Role> -> Set<String>
    @Named("mapRolesToString")
    default Set<String> mapRolesToString(Set<Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(role -> role.getName().name()) // Lấy tên RoleName dưới dạng String
                .collect(Collectors.toSet());
    }
}
