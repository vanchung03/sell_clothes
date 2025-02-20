package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.dto.auth.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.service.CloudinaryService;
import com.example.demo.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private CloudinaryService cloudinaryService;
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

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
    @PostMapping(value = "/upload-avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (file.isEmpty()) {
                response.put("error", "File không được để trống!");
                return ResponseEntity.badRequest().body(response);
            }

            // ✅ Upload ảnh lên Cloudinary
            String imageUrl = cloudinaryService.uploadFile(file, "avatars", "temp_user_avatar");

            response.put("success", true);
            response.put("imageUrl", imageUrl);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("error", "Lỗi tải ảnh!");
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> updateAvatarUser(@PathVariable Long id,
                                                                @RequestParam("file") MultipartFile file) {

        Map<String, Object> response = new HashMap<>();
        try {
            if (file.isEmpty()) {
                response.put("error", "File không được để trống!");
                return ResponseEntity.badRequest().body(response);
            }

            Optional<User> userOptional = userService.getUserById(id);
            if (userOptional.isEmpty()) {
                response.put("error", "User not found!");
                return ResponseEntity.status(404).body(response);
            }

            // Upload ảnh lên Cloudinary
            String imageUrl = cloudinaryService.uploadFile(file, "avatars", "user_" + id);

            // Cập nhật avatar trong database
            UserDTO updatedUser = userService.updateAvatar(id, imageUrl);

            if (updatedUser == null) {
                response.put("error", "Failed to update avatar!");
                return ResponseEntity.status(500).body(response);
            }

            response.put("success", true);
            response.put("message", "Avatar updated successfully");
            response.put("imageUrl", imageUrl);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("error", "Lỗi tải ảnh!");
            return ResponseEntity.status(500).body(response);
        }
    }

    // ✅ API cập nhật thông tin người dùng (Không cập nhật mật khẩu và ảnh đại diện)
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        try {
            UserDTO updatedUser = userService.updateUser(id, userDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("user", updatedUser);
            response.put("message", "Cập nhật thông tin người dùng thành công!");
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", "Người dùng không tồn tại!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @PutMapping("/{id}/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        System.out.println("Updating user: " + userDTO);
        UserDTO updatedUser = userService.update_Profile(id, userDTO);
        Map<String, Object> response = new HashMap<>();
        if (updatedUser != null) {
            response.put("user", updatedUser);
            response.put("message", "User updated successfully");
            return ResponseEntity.ok(response);
        }
        response.put("message", "User not found");
        return ResponseEntity.status(404).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        Map<String, Object> response = new HashMap<>();
        if (deleted) {
            response.put("message", "User deleted successfully");
            return ResponseEntity.ok(response);
        }
        response.put("message", "User not found");
        return ResponseEntity.status(404).body(response);
    }
}