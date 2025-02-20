package com.example.demo.mapper;

import com.example.demo.dto.OrderItemDTO;
import com.example.demo.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderItemMapper {
    OrderItemMapper INSTANCE = Mappers.getMapper(OrderItemMapper.class);

    @Mapping(source = "order.orderId", target = "orderId")
    @Mapping(source = "variant.variantId", target = "variantId")
    OrderItemDTO toDTO(OrderItem entity);

    @Mapping(source = "orderId", target = "order.orderId")
    @Mapping(source = "variantId", target = "variant.variantId")
    OrderItem toEntity(OrderItemDTO dto);
}
