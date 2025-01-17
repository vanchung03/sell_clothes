package com.example.demo.service;
import com.example.demo.dto.auth.UserRegisterDTO;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserRoleRepository;
//import jakarta.transaction.Transactional;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class UserService {
//    private final UserRepository userRepository;
//    private final RoleRepository roleRepository;
//    private final UserRoleRepository userRoleRepository;
//    private final UserMapper userMapper;
//    private final PasswordEncoder passwordEncoder;
//
//    public UserService(UserRepository userRepository, RoleRepository roleRepository,
//                       UserRoleRepository userRoleRepository, UserMapper userMapper,
//                       PasswordEncoder passwordEncoder) {
//        this.userRepository = userRepository;
//        this.roleRepository = roleRepository;
//        this.userRoleRepository = userRoleRepository;
//        this.userMapper = userMapper;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    @Transactional
//    public User registerUser(UserRegisterDTO dto) {
//        // Chuyển DTO sang User Entity
//        User user = userMapper.toEntity(dto);
//
//        // Mã hóa mật khẩu
//        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
//
//        user.setCreatedAt(LocalDate.now());
//        user.setUpdatedAt(LocalDate.now());
//        user.setAvatar(dto.getAvatar());
//        user.setStatus(1);
//
//        // Lưu thông tin người dùng
//        User savedUser = userRepository.save(user);
//
//        // Tìm role mặc định
//        Role userRole = roleRepository.findByName("ROLE_CUSTOMER")
//                .orElseThrow(() -> new RuntimeException("Default role ROLE_CUSTOMER not found"));
//        // Tạo UserRole
//        UserRole userRoleEntity = UserRole.builder()
//                .user(savedUser)
//                .role(userRole)
//                .createdAt(LocalDateTime.now())
//                .build();
//        // Lưu thông tin vào bảng UserRole
//        userRoleRepository.save(userRoleEntity);
//        return savedUser;
//    }
}
