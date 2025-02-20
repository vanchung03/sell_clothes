package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.dto.auth.RegisterRequest;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.enums.RoleName;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();

    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Register a new user
    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Tên người dùng đã tồn tại!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã được sử dụng!");
        }
        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email không hợp lệ!");
        }
        if (!request.getPhone().matches("^0\\d{9}$")) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ!");
        }
        // Tạo User mới nếu các điều kiện hợp lệ
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .avatar(request.getAvatar())
                .fullName(request.getFullName())
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .phone(request.getPhone())
                .roles(Set.of(roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow()))
                .status(1)
                .build();
        return userRepository.save(user);
    }

    public UserDTO update_Profile(Long id, UserDTO userDTO) {
        return userRepository.findById(id).map(user -> {
            // Cập nhật các trường thông tin nếu có
            if (userDTO.getEmail() != null) user.setEmail(userDTO.getEmail());
            if (userDTO.getFullName() != null) user.setFullName(userDTO.getFullName());
            if (userDTO.getPhone() != null) user.setPhone(userDTO.getPhone());
            if (userDTO.getAvatar() != null) user.setAvatar(userDTO.getAvatar());


            // Kiểm tra nếu mật khẩu mới khác mật khẩu cũ thì mới cập nhật
            if (userDTO.getPasswordHash() != null && !userDTO.getPasswordHash().isEmpty()) {
                if (!passwordEncoder.matches(userDTO.getPasswordHash(), user.getPasswordHash())) {
                    user.setPasswordHash(passwordEncoder.encode(userDTO.getPasswordHash())); // Mã hóa và cập nhật
                }
            }
            // Cập nhật thời gian sửa đổi
            user.setUpdatedAt(LocalDate.now());
            // Lưu và trả về DTO
            return userMapper.toDTo(userRepository.saveAndFlush(user));
        }).orElse(null); // Trả về null nếu không tìm thấy người dùng
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        return userRepository.findById(id).map(user -> {
            boolean isUpdated = false;

            // Cập nhật email nếu hợp lệ
            if (userDTO.getEmail() != null && !userDTO.getEmail().isEmpty()) {
                if (!userDTO.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                    throw new IllegalArgumentException("Email không hợp lệ!");
                }
                user.setEmail(userDTO.getEmail());
                isUpdated = true;
            }

            // Cập nhật họ tên
            if (userDTO.getFullName() != null && !userDTO.getFullName().isEmpty()) {
                user.setFullName(userDTO.getFullName());
                isUpdated = true;
            }

            // Cập nhật số điện thoại nếu hợp lệ
            if (userDTO.getPhone() != null && !userDTO.getPhone().isEmpty()) {
                if (!userDTO.getPhone().matches("^0\\d{9}$")) {
                    throw new IllegalArgumentException("Số điện thoại không hợp lệ!");
                }
                user.setPhone(userDTO.getPhone());
                isUpdated = true;
            }

            // Cập nhật trạng thái nếu có
            if (userDTO.getStatus() >= 0) {
                user.setStatus(userDTO.getStatus());
                isUpdated = true;
            }

            // Nếu có cập nhật thì mới lưu
            if (isUpdated) {
                user.setUpdatedAt(LocalDate.now());
                return userMapper.toDTo(userRepository.saveAndFlush(user));
            }

            throw new IllegalArgumentException("Không có thông tin nào để cập nhật!");
        }).orElseThrow(() -> new EntityNotFoundException("User không tồn tại!"));
    }



    public UserDTO updateAvatar(Long id, String imageUrl) {
        return userRepository.findById(id).map(user -> {
            // Cập nhật ảnh đại diện
            user.setAvatar(imageUrl);
            user.setUpdatedAt(LocalDate.now());

            // Lưu thay đổi vào database
            userRepository.saveAndFlush(user);

            // Trả về thông tin người dùng dưới dạng DTO
            return userMapper.toDTo(user);
        }).orElse(null); // Trả về null nếu không tìm thấy người dùng
    }

    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

}