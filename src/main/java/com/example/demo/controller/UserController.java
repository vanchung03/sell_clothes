package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")  // Cấu hình cho phép frontend ở localhost:4200 truy cập
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private final UserRepository userRepository;
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();  // Lấy tất cả người dùng
    }
}
