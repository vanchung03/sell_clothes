package com.example.demo.mapper;

import com.example.demo.dto.CartDTO;
import com.example.demo.dto.CartItemDTO;
import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CartMapper {
    CartMapper INSTANCE = Mappers.getMapper(CartMapper.class);

    @Mapping(source = "user.userId", target = "userId") // 🔥 Map userId từ User entity
    CartDTO toDTO(Cart entity);

    @Mapping(source = "userId", target = "user.userId") // 🔥 Map userId ngược lại khi convert DTO -> Entity
    Cart toEntity(CartDTO dto);

    @Mapping(source = "cart.cartId", target = "cartId") // 🔥 Map cartId từ Cart entity
    @Mapping(source = "variant.variantId", target = "variantId") // 🔥 Map variantId từ ProductVariant entity
    CartItemDTO toDTO(CartItem entity);

    @Mapping(source = "cartId", target = "cart.cartId")
    @Mapping(source = "variantId", target = "variant.variantId")
    CartItem toEntity(CartItemDTO dto);
}
