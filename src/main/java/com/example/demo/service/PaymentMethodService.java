package com.example.demo.service;

import com.example.demo.dto.PaymentMethodDTO;
import com.example.demo.entity.PaymentMethod;
import com.example.demo.mapper.PaymentMethodMapper;
import com.example.demo.repository.PaymentMethodRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentMethodService {

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private PaymentMethodMapper paymentMethodMapper;

    // Tạo mới PaymentMethod
    public PaymentMethodDTO createPaymentMethod(PaymentMethodDTO paymentMethodDTO) {
        // Nếu client không gửi ngày tháng, ta có thể thiết lập ngày tháng tự động
        if (paymentMethodDTO.getCreatedAt() == null) {
            paymentMethodDTO.setCreatedAt(LocalDate.now().toString());
        }
        if (paymentMethodDTO.getUpdatedAt() == null) {
            paymentMethodDTO.setUpdatedAt(LocalDate.now().toString());
        }

        PaymentMethod paymentMethod = paymentMethodMapper.toEntity(paymentMethodDTO);
        PaymentMethod savedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        return paymentMethodMapper.toDTO(savedPaymentMethod);
    }


    // Cập nhật PaymentMethod
    public PaymentMethodDTO updatePaymentMethod(Long id, PaymentMethodDTO paymentMethodDTO) {
        // Tìm PaymentMethod theo ID
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PaymentMethod not found with ID: " + id));

        // Cập nhật các trường khác ngoài createdAt
        paymentMethod.setName(paymentMethodDTO.getName());
        paymentMethod.setCode(paymentMethodDTO.getCode());
        paymentMethod.setDescription(paymentMethodDTO.getDescription());
        paymentMethod.setStatus(paymentMethodDTO.isStatus());

        // Chỉ cập nhật updatedAt khi có sự thay đổi
        paymentMethod.setUpdatedAt(LocalDate.now()); // Lưu thời gian cập nhật hiện tại

        // Lưu PaymentMethod đã được cập nhật vào cơ sở dữ liệu
        PaymentMethod updatedPaymentMethod = paymentMethodRepository.save(paymentMethod);

        // Chuyển đối tượng thành DTO và trả về
        return paymentMethodMapper.toDTO(updatedPaymentMethod);
    }

    // Lấy tất cả PaymentMethod
    public List<PaymentMethodDTO> getAllPaymentMethods() {
        List<PaymentMethod> paymentMethods = paymentMethodRepository.findAll();
        return paymentMethods.stream()
                .map(paymentMethodMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Tìm kiếm PaymentMethod theo ID
    public PaymentMethodDTO getPaymentMethodById(Long id) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PaymentMethod not found with ID: " + id));
        return paymentMethodMapper.toDTO(paymentMethod);
    }
    // Xóa PaymentMethod
    public void deletePaymentMethod(Long id) {
        paymentMethodRepository.deleteById(id);
    }

}
