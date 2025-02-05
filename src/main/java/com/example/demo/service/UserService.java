package com.example.demo.service;

import com.example.demo.dto.auth.AuthRequest;
import com.example.demo.dto.auth.AuthResponse;
import com.example.demo.dto.auth.ChangePasswordRequest;
import com.example.demo.dto.auth.RegisterRequest;
import com.example.demo.entity.Otp;
import com.example.demo.entity.User;
import com.example.demo.enums.RoleName;
import com.example.demo.repository.OtpRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtil;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtil, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.roleRepository = roleRepository;
    }

public User register(RegisterRequest request) {
    if (userRepository.existsByUsername(request.getUsername())) {
        throw new IllegalArgumentException("Tên người dùng đã tồn tại!");
    }
    if (userRepository.existsByEmail(request.getEmail())) {
        throw new IllegalArgumentException("Email đã được sử dụng!");
    } if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
        throw new IllegalArgumentException("Email không hợp lệ!");
    }
    if (!request.getPhone().matches("^0\\d{9}$")) {
        throw new IllegalArgumentException("Số điện thoại không hợp lệ!");
    }
    // Nếu tất cả hợp lệ, tạo User mới
    User user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .avatar(request.getAvatar())
            .fullName(request.getFullName())
            .createdAt(LocalDate.now())
            .updatedAt(LocalDate.now())
            .phone(request.getPhone())
            .roles(Set.of(roleRepository.findByName(RoleName.ROLE_USER).orElseThrow()))
            .status(1).build();
    return userRepository.save(user);
}


    public AuthResponse authenticate(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials!");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getEmail(), user.getRoles().stream().map(role -> role.getName().name()).toList(), user.getStatus());

        return new AuthResponse(token, user.getRoles().stream().map(role -> role.getName().name()).toList(), user.getStatus());
    }
    // Thêm phương thức để lấy tất cả người dùng
    public List<User> getAllUsers() {
        return userRepository.findAll();  // Trả về danh sách tất cả người dùng
    }

    // Đổi mật khẩu
    public void changePassword(String username, ChangePasswordRequest changePasswordRequest) {
        // Lấy user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Old password is incorrect");
        }
        // Cập nhật mật khẩu mới
        user.setPasswordHash(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
    }
}