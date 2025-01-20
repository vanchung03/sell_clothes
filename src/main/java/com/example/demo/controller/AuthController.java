package com.example.demo.controller;

import com.example.demo.dto.auth.AuthRequest;
import com.example.demo.dto.auth.AuthResponse;
import com.example.demo.dto.auth.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // Đăng ký
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        try {
            userService.register(request);
            return ResponseEntity.ok("User registered successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Đăng nhập
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
        try {
            AuthResponse response = userService.authenticate(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new AuthResponse(null, null));
        }
    }

    // Lấy danh sách người dùng
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
