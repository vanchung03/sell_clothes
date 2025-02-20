package com.example.demo.controller;

import com.example.demo.dto.UserAddressDTO;
import com.example.demo.service.UserAddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/user-addresses")
public class UserAddressController {
    private final UserAddressService userAddressService;

    public UserAddressController(UserAddressService userAddressService) {
        this.userAddressService = userAddressService;
    }

    // ✅ Lấy danh sách địa chỉ theo user_id
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserAddressDTO>> getAddressesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(userAddressService.getAddressesByUserId(userId));
    }

    // ✅ Lấy địa chỉ theo ID
    @GetMapping("/{addressId}")
    public ResponseEntity<UserAddressDTO> getAddressById(@PathVariable Long addressId) {
        return ResponseEntity.ok(userAddressService.getAddressById(addressId));
    }

    // ✅ Thêm địa chỉ mới
    @PostMapping
    public ResponseEntity<UserAddressDTO> createAddress(@RequestBody UserAddressDTO dto) {
        return ResponseEntity.ok(userAddressService.createAddress(dto));
    }

    // ✅ Cập nhật địa chỉ
    @PutMapping("/{addressId}")
    public ResponseEntity<UserAddressDTO> updateAddress(@PathVariable Long addressId, @RequestBody UserAddressDTO dto) {
        return ResponseEntity.ok(userAddressService.updateAddress(addressId, dto));
    }

    // ✅ Xóa địa chỉ
    @DeleteMapping("/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId) {
        userAddressService.deleteAddress(addressId);
        return ResponseEntity.ok("Đã xóa địa chỉ thành công");
    }
}
