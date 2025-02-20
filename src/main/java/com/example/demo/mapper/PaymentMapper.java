package com.example.demo.mapper;
import com.example.demo.dto.PaymentDTO;
import com.example.demo.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PaymentMapper {
    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

    @Mapping(source = "order.orderId", target = "orderId")
    @Mapping(source = "method.name", target = "method")
    PaymentDTO toDTO(Payment entity);
}
