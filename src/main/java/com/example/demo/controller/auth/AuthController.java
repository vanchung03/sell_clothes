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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public String register(@RequestBody RegisterRequest request) {
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
        userRepository.save(user);
        return "User registered successfully";
    }
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
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
    // Quên mật khẩu : send email get otp
    @PostMapping("/sendOtpEmail")
    public String sendOtpEmail(@RequestBody Object requestBody) {
        try {
            otpService.sendOtpEmail(requestBody);
            return "OTP đã được gửi đến email của bạn!";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    
    

//     API thay đổi mật khẩu với OTP
@PutMapping("/reset-password")
public String resetPassword(@RequestParam String email, @RequestParam String otp, @RequestParam String newPassword) {
    // Xác thực OTP
    OtpValidationResponse validationResponse = otpService.validateOtp(email, otp);

    // Kiểm tra nếu OTP hợp lệ
    if (validationResponse.isValid()) {
        // Tìm kiếm người dùng theo email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại trong hệ thống"));

        // Mã hóa mật khẩu mới và cập nhật vào cơ sở dữ liệu
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPasswordHash(encodedPassword);
        user.setUpdatedAt(LocalDate.now()); // Cập nhật thời gian cập nhật
        userRepository.save(user);
        return "Mật khẩu đã được thay đổi thành công!";
    }

    // Nếu OTP không hợp lệ, trả về thông báo lỗi từ phản hồi OTP
    return validationResponse.getMessage();
}


}
