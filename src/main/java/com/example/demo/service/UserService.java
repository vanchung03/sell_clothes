package com.example.demo.service;


import com.example.demo.dto.auth.AuthRequest;
import com.example.demo.dto.auth.AuthResponse;
import com.example.demo.dto.auth.RegisterRequest;
import com.example.demo.entity.Role;
import com.example.demo.entity.RoleName;
import com.example.demo.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;

import com.example.demo.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        Role userRole = roleRepository.findById(2L)
                .orElseThrow(() -> new RuntimeException("Default role not found!"));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .avatar(request.getAvatar())
                .roles(Collections.singleton(userRole))
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .status(1).build();


        userRepository.save(user);
    }

    public AuthResponse authenticate(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials!");
        }

        String token = jwtUtil.generateToken(user); // Truyền đối tượng `User`
        return new AuthResponse(token, user);
    }


    // Lấy tất cả người dùng
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
