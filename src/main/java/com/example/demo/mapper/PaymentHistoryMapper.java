package com.example.demo.mapper;

import com.example.demo.dto.PaymentHistoryDTO;
import com.example.demo.entity.PaymentHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PaymentHistoryMapper {
    PaymentHistoryMapper INSTANCE = Mappers.getMapper(PaymentHistoryMapper.class);

    @Mapping(source = "payment.paymentId", target = "paymentId")
    @Mapping(source = "payment.order.orderId", target = "orderId")
    @Mapping(source = "payment.order.status", target = "orderStatus")
    @Mapping(source = "payment.amount", target = "amount")
    @Mapping(source = "payment.transactionCode", target = "transactionCode")
    @Mapping(source = "payment.method.name", target = "paymentMethod")
    PaymentHistoryDTO toDTO(PaymentHistory entity);
}
