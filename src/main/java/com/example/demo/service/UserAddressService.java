package com.example.demo.service;

import com.example.demo.dto.UserAddressDTO;
import com.example.demo.entity.User;
import com.example.demo.entity.UserAddress;
import com.example.demo.mapper.UserAddressMapper;
import com.example.demo.repository.UserAddressRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserAddressService {
    private final UserAddressRepository userAddressRepository;
    private final UserRepository userRepository;

    public UserAddressService(UserAddressRepository userAddressRepository, UserRepository userRepository) {
        this.userAddressRepository = userAddressRepository;
        this.userRepository = userRepository;
    }

    // ✅ Lấy tất cả địa chỉ của một user
    public List<UserAddressDTO> getAddressesByUserId(Long userId) {
        return userAddressRepository.findByUserUserId(userId)
                .stream()
                .map(UserAddressMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ Lấy một địa chỉ theo ID
    public UserAddressDTO getAddressById(Long addressId) {
        return userAddressRepository.findById(addressId)
                .map(UserAddressMapper.INSTANCE::toDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));
    }

    // ✅ Thêm địa chỉ mới
    public UserAddressDTO createAddress(UserAddressDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        UserAddress address = UserAddressMapper.INSTANCE.toEntity(dto);
        address.setUser(user);
        return UserAddressMapper.INSTANCE.toDTO(userAddressRepository.save(address));
    }

    // ✅ Cập nhật địa chỉ
    public UserAddressDTO updateAddress(Long addressId, UserAddressDTO dto) {
        UserAddress address = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));

        address.setAddressLine(dto.getAddressLine());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setWard(dto.getWard());
        address.setDefault(dto.isDefault());

        return UserAddressMapper.INSTANCE.toDTO(userAddressRepository.save(address));
    }

    // ✅ Xóa địa chỉ
    public void deleteAddress(Long addressId) {
        userAddressRepository.deleteById(addressId);
    }
}
