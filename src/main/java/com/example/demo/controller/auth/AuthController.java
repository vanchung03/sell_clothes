package com.example.demo.controller.auth;

import com.example.demo.dto.auth.*;
import com.example.demo.entity.Otp;
import com.example.demo.entity.User;
import com.example.demo.enums.RoleName;
import com.example.demo.repository.OtpRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtils;
import com.example.demo.service.OtpService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@CrossOrigin(origins = "http://localhost:4200")  // Cấu hình cho phép frontend ở localhost:4200 truy cập
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final RoleRepository roleRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final JwtUtils jwtUtils;
    @Autowired
    private OtpService otpService;
    @Autowired
    private OtpRepository otpRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            User newUser = userService.register(registerRequest);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đăng ký thành công!",
                    "username", newUser.getUsername()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));
            if (passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                String token = jwtUtils.generateToken(user.getUsername(), user.getEmail(), user.getRoles().stream().map(role -> role.getName().name()).toList(), user.getStatus());
                return new AuthResponse(token, user.getRoles().stream().map(role -> role.getName().name()).toList(), user.getStatus());
            } else {
                throw new RuntimeException("Invalid credentials");
            }
    }

    @GetMapping("all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();  // Lấy tất cả người dùng
    }
    // Đổi mật khẩu
    @PostMapping("/change_password/rs")
    public String changePassword(@RequestHeader("Authorization") String token,
                                 @RequestBody ChangePasswordRequest changePasswordRequest) {
        // Lấy user từ token
        String username = jwtUtils.getUserFromToken(token);
        // Thực hiện thay đổi mật khẩu
        userService.changePassword(username, changePasswordRequest);
        return "Password changed successfully";
    }

    // Gửi OTP qua email
    @PostMapping("/request-otp")
    public ResponseEntity<?> sendOtpEmail(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        try {
            otpService.sendOtpEmail(email);
            return ResponseEntity.ok(Map.of("success", true, "message", "OTP đã được gửi đến email của bạn!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }
    @PutMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String otp = requestBody.get("otp");

        try {
            OtpValidationResponse validationResponse = otpService.validateOtp(email, otp);  // Xác thực OTP

            if (validationResponse.isValid()) {
                return ResponseEntity.ok(Map.of("success", true, "message", "OTP hợp lệ. Vui lòng đặt mật khẩu mới."));
            } else {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", validationResponse.getMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Lỗi xác thực OTP."));
        }
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String otp = requestBody.get("otp");
        String newPassword = requestBody.get("newPassword");

        try {
            // Kiểm tra tính hợp lệ của OTP
            OtpValidationResponse validationResponse = otpService.validateOtp(email, otp);

            if (validationResponse.isValid()) {
                // Lấy user từ email
                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Email không tồn tại trong hệ thống"));

                // Mã hóa mật khẩu mới và lưu lại
                String encodedPassword = passwordEncoder.encode(newPassword);
                user.setPasswordHash(encodedPassword);
                userRepository.save(user);

                return ResponseEntity.ok(Map.of("success", true, "message", "Mật khẩu đã được thay đổi thành công!"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", validationResponse.getMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Lỗi khi thay đổi mật khẩu."));
        }
    }

}
