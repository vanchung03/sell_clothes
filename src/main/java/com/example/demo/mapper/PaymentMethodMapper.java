package com.example.demo.mapper;

import com.example.demo.dto.PaymentMethodDTO;
import com.example.demo.entity.PaymentMethod;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMethodMapper {
    // Ánh xạ từ Entity sang DTO

    PaymentMethodDTO toDTO(PaymentMethod paymentMethod);
    // Ánh xạ từ DTO sang Entity
    PaymentMethod toEntity(PaymentMethodDTO paymentMethodDTO);
}
