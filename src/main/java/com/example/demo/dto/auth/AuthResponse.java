package com.example.demo.dto.auth;

import com.example.demo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private List<String> roles;
    private int status; // Thêm trạng thái người dùng

}
