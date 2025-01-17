package com.example.demo.mapper;

import com.example.demo.dto.RoleDTO;
import com.example.demo.entity.Role;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleDTO toDTO(Role role);

    Role toEntity(RoleDTO roleDTO);
    List<RoleDTO> toDTOs(List<Role> roles);
    List<Role> toEntities(List<RoleDTO> roleDTOs);
}
