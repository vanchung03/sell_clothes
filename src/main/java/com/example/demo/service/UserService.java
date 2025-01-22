package com.example.demo.service;

import com.example.demo.dto.auth.AuthRequest;
import com.example.demo.dto.auth.AuthResponse;
import com.example.demo.dto.auth.ChangePasswordRequest;
import com.example.demo.dto.auth.RegisterRequest;
import com.example.demo.entity.Otp;
import com.example.demo.entity.User;
import com.example.demo.repository.OtpRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;

    }
    public void register(RegisterRequest request) {
        User user = User.builder()  // Sử dụng builder
                .username(request.getUsername())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .avatar(request.getAvatar())
                .passwordHash(passwordEncoder.encode(request.getPassword()))  // Mã hóa mật khẩu
                .phone(request.getPhone())
                .updatedAt(LocalDate.now())
                .createdAt(LocalDate.now())
                .status(1).build();  // Tạo đối tượng User

        userRepository.save(user);  // Lưu đối tượng User vào cơ sở dữ liệu
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