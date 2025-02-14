package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.dto.auth.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        System.out.println("Updating user: " + userDTO);
        UserDTO updatedUser = userService.updateUser(id, userDTO);
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
