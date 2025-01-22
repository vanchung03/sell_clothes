package com.example.demo.dto.auth;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String oldPassword;  // Mật khẩu cũ
    private String newPassword;  // Mật khẩu mới
}
