package com.example.demo.service;

import com.example.demo.dto.RoleDTO;
import com.example.demo.entity.Role;
import com.example.demo.mapper.RoleMapper;
import com.example.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleMapper roleMapper;

    // Tạo mới Role
    public RoleDTO createRole(RoleDTO roleDTO) {
        Role role = roleMapper.toEntity(roleDTO);
        role.setCreatedAt(LocalDate.now());
        role.setUpdatedAt(LocalDate.now());
        Role savedRole = roleRepository.save(role);
        return roleMapper.toDTO(savedRole);
    }

    // Cập nhật Role
    public RoleDTO updateRole(Long roleId, RoleDTO roleDTO) {
        Optional<Role> existingRoleOpt = roleRepository.findById(roleId);
        if (existingRoleOpt.isPresent()) {
            Role existingRole = existingRoleOpt.get();
            // Cập nhật thông tin Role từ RoleDTO
            existingRole.setName(roleDTO.getName());
            existingRole.setDescription(roleDTO.getDescription());
            existingRole.setUpdatedAt(LocalDate.now());
            Role updatedRole = roleRepository.save(existingRole);
            return roleMapper.toDTO(updatedRole);
        }
        return null; // hoặc có thể ném ngoại lệ nếu không tìm thấy
    }

    // Xóa Role
    public boolean deleteRole(Long roleId) {
        Optional<Role> existingRoleOpt = roleRepository.findById(roleId);
        if (existingRoleOpt.isPresent()) {
            roleRepository.delete(existingRoleOpt.get());
            return true;
        }
        return false; // hoặc có thể ném ngoại lệ nếu không tìm thấy
    }

    // Lấy tất cả Roles
    public List<RoleDTO> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return roleMapper.toDTOs(roles);
    }

    // Lấy Role theo ID
    public RoleDTO getRoleById(Long roleId) {
        Optional<Role> roleOpt = roleRepository.findById(roleId);
        return roleOpt.map(roleMapper::toDTO).orElse(null); // hoặc có thể ném ngoại lệ nếu không tìm thấy
    }
}
