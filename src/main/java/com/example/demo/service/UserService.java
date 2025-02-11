package com.example.demo.service;

import com.example.demo.dto.auth.AuthRequest;
import com.example.demo.dto.auth.AuthResponse;
import com.example.demo.dto.auth.ChangePasswordRequest;
import com.example.demo.dto.auth.RegisterRequest;
import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.User;
import com.example.demo.enums.RoleName;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtil;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtil, RoleRepository roleRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    // Register a new user
    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Tên người dùng đã tồn tại!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã được sử dụng!");
        }
        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email không hợp lệ!");
        }
        if (!request.getPhone().matches("^0\\d{9}$")) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ!");
        }
        // Tạo User mới nếu các điều kiện hợp lệ
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
                .status(1)
                .build();
        return userRepository.save(user);
    }

    // Authenticate user, generate tokens, and set refresh token in cookie
    public ResponseEntity<?> authenticate(@Valid AuthRequest request) {
        // Tìm người dùng từ cơ sở dữ liệu
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // Kiểm tra mật khẩu
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tên đăng nhập hoặc mật khẩu không đúng"));
        }

        // Tạo danh sách quyền của người dùng
        List<String> roles = user.getRoles().stream().map(role -> role.getName().name()).toList();

        // Tạo Access Token và Refresh Token
        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getEmail(), roles, String.valueOf(user.getStatus()));
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        // Kiểm tra xem refresh token đã tồn tại trong DB chưa
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user);
        if (existingToken.isPresent()) {
            RefreshToken tokenEntity = existingToken.get();
            tokenEntity.setToken(refreshToken);
            refreshTokenRepository.save(tokenEntity);
        } else {
            RefreshToken tokenEntity = new RefreshToken(null, refreshToken, user);
            refreshTokenRepository.save(tokenEntity);
        }

        // Tạo cookie để lưu refresh token (HTTP-only – chỉ được set bởi server)
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(false)
                .secure(false)
                .path("/")
                .maxAge(86400000)       // 1 ngày
                .sameSite("Lax")   // Điều chỉnh nếu cần
                .build();

        // Trả về response với header Set-Cookie và body chứa access token
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AuthResponse(accessToken, refreshToken));
    }

    // Đổi mật khẩu
    public void changePassword(String username, ChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Old password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
    }
    // Thêm phương thức để lấy tất cả người dùng
    public List<User> getAllUsers() {
        return userRepository.findAll();  // Trả về danh sách tất cả người dùng
    }

}



