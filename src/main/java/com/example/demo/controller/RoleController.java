package com.example.demo.controller;

import com.example.demo.dto.RoleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

//    @Autowired
//    private RoleService roleService;
//    // Lấy quyền theo ID
//    @GetMapping("/{id}")
//    public ResponseEntity<RoleDTO> getRoleById(@PathVariable Long id) {
//        RoleDTO role = roleService.getRoleById(id);
//        return ResponseEntity.ok(role);
//    }
//    // Lấy quyền theo tên
//    @GetMapping("/name/{name}")
//    public ResponseEntity<RoleDTO> getRoleByName(@PathVariable String name) {
//        RoleDTO role = roleService.getRoleByName(name);
//        return ResponseEntity.ok(role);
//    }
//    @GetMapping
//    public ResponseEntity<List<RoleDTO>> getAllRoles() {
//        List<RoleDTO> roles = roleService.getAllRoles();
//        return ResponseEntity.ok(roles);
//    }
//
//    @PostMapping
//    public ResponseEntity<RoleDTO> createRole(@RequestBody RoleDTO roleDTO) {
//        RoleDTO createdRole = roleService.createRole(roleDTO);
//        return ResponseEntity.ok(createdRole);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<RoleDTO> updateRole(@PathVariable Long id, @RequestBody RoleDTO roleDTO) {
//        RoleDTO updatedRole = roleService.updateRole(id, roleDTO);
//        return ResponseEntity.ok(updatedRole);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
//        roleService.deleteRole(id);
//        return ResponseEntity.ok("Role deletion was successful");
//    }
}
