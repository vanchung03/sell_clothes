package com.example.demo.mapper;

import com.example.demo.dto.OrderDTO;
import com.example.demo.dto.OrderItemDTO;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "address.addressId", target = "addressId")
//    @Mapping(source = "orderItems", target = "orderItems")
    OrderDTO toDTO(Order entity);

    Order toEntity(OrderDTO dto);

    @Mapping(source = "order.orderId", target = "orderId")
    @Mapping(source = "variant.variantId", target = "variantId")
    OrderItemDTO toDTO(OrderItem entity);

    OrderItem toEntity(OrderItemDTO dto);
    List<OrderItemDTO> toItemDTOList(List<OrderItem> orderItems);
}
