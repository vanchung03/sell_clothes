package com.example.demo.controller.auth;

import com.example.demo.dto.auth.*;
import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.User;
import com.example.demo.repository.OtpRepository;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtils;
import com.example.demo.service.OtpService;
import com.example.demo.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
  // Cấu hình cho phép frontend ở localhost:4200 truy cập
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private AuthService authService;
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
    @Autowired
    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            User newUser = authService.register(registerRequest);
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
    @GetMapping("/check-cookie")
    public ResponseEntity<String> checkCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    return ResponseEntity.ok("Cookie refreshToken: " + cookie.getValue());
                }
            }
        }
        return ResponseEntity.ok("No refreshToken cookie found.");
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        return authService.authenticate(request);
    }
//     Refresh endpoint: lấy refresh token từ cookie (HTTP-only)
//    @PostMapping("/refresh2")
//    public ResponseEntity<?> refresh(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
//        if (refreshToken == null) {
//            return ResponseEntity.badRequest().body(Map.of("error", "Refresh token is missing"));
//        }
//        Optional<RefreshToken> tokenEntity = refreshTokenRepository.findByToken(refreshToken);
//        if (tokenEntity.isPresent()) {
//            User user = tokenEntity.get().getUser();
//            String newAccessToken = jwtUtils.generateAccessToken(user.getUsername(), user.getEmail(),
//                    user.getRoles().stream().map(role -> role.getName().name()).toList(),
//                    String.valueOf(user.getStatus()));
//            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
//        } else {
//            return ResponseEntity.badRequest().body(Map.of("error", "Invalid refresh token"));
//        }
//    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> requestBody) {
        String refreshToken = requestBody.get("refreshToken");

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Refresh token is missing"));
        }

        Optional<RefreshToken> tokenEntity = refreshTokenRepository.findByToken(refreshToken);

        if (tokenEntity.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid refresh token"));
        }
        RefreshToken refreshTokenObj = tokenEntity.get();

        // Kiểm tra refresh token có hợp lệ không
        if (!jwtUtils.validateToken(refreshToken)) {
            refreshTokenRepository.delete(refreshTokenObj);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Refresh token expired or invalid"));
        }

        User user = refreshTokenObj.getUser();
        String newAccessToken = jwtUtils.generateAccessToken(
                user.getUsername(),
                user.getEmail(),
                user.getRoles().stream().map(role -> role.getName().name()).toList(),
                String.valueOf(user.getStatus()),
                String.valueOf(user.getUserId())
        );

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }



    // Đổi mật khẩu
    @PostMapping("/change_password/rs")
    public String changePassword(@RequestHeader("Authorization") String token,
                                 @RequestBody ChangePasswordRequest changePasswordRequest) {
        // Lấy user từ token
        String username = jwtUtils.getUserFromToken(token);
        // Thực hiện thay đổi mật khẩu
        authService.changePassword(username, changePasswordRequest);
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
