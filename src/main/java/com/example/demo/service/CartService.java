package com.example.demo.service;

import com.example.demo.dto.CartDTO;
import com.example.demo.dto.CartItemDTO;
import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.ProductVariant;
import com.example.demo.mapper.CartMapper;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.ProductVariantRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductVariantRepository productVariantRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productVariantRepository = productVariantRepository;
        this.userRepository = userRepository;
    }

    @Transactional // 🔥 Giữ session mở khi lấy dữ liệu
    public CartDTO getCartByUserId(Long userId) {
        // Kiểm tra nếu user chưa có giỏ hàng thì tự động tạo mới
        Cart cart = cartRepository.findByUserUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy user")));  // 🔥 Đảm bảo user tồn tại
                    newCart.setCartItems(new ArrayList<>()); // 🔥 Khởi tạo danh sách cartItems
                    return cartRepository.save(newCart);
                });

        // Chạy vòng lặp để khởi tạo cartItems trước khi map DTO
        cart.getCartItems().size(); // 🔥 Kích hoạt Lazy Loading

        return CartMapper.INSTANCE.toDTO(cart);
    }

    @Transactional // 🔥 Giữ session mở khi thêm sản phẩm vào giỏ hàng
    public CartDTO addItemToCart(Long userId, CartItemDTO cartItemDTO) {
        Cart cart = cartRepository.findByUserUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy user")));
                    newCart.setCartItems(new ArrayList<>()); // 🔥 Khởi tạo danh sách cartItems
                    return cartRepository.save(newCart);
                });

        ProductVariant variant = productVariantRepository.findById(cartItemDTO.getVariantId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể"));

        // ✅ Đảm bảo cartItems không bị null
        if (cart.getCartItems() == null) {
            cart.setCartItems(new ArrayList<>()); // 🔥 Khởi tạo nếu null
        }

        Optional<CartItem> existingCartItem = cart.getCartItems().stream()
                .filter(item -> item.getVariant().getVariantId().equals(variant.getVariantId()))
                .findFirst();

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + cartItemDTO.getQuantity());
            cartItem.setTotalPrice(cartItem.getUnitPrice() * cartItem.getQuantity());
            cartItemRepository.save(cartItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setVariant(variant);
            cartItem.setQuantity(cartItemDTO.getQuantity());
            cartItem.setUnitPrice(variant.getPrice());
            cartItem.setTotalPrice(variant.getPrice() * cartItemDTO.getQuantity());
            cartItemRepository.save(cartItem);
        }

        cart.getCartItems().size(); // 🔥 Kích hoạt Lazy Loading trước khi map DTO

        return CartMapper.INSTANCE.toDTO(cart);
    }
    @Transactional // 🔥 Giữ session mở khi thêm hoặc cập nhật sản phẩm vào giỏ hàng
    public CartDTO updateCartItem(Long userId, CartItemDTO cartItemDTO) {
        Cart cart = cartRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng của user"));

        ProductVariant variant = productVariantRepository.findById(cartItemDTO.getVariantId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể"));

        // ✅ Đảm bảo cartItems không bị null
        if (cart.getCartItems() == null) {
            cart.setCartItems(new ArrayList<>()); // 🔥 Khởi tạo nếu null
        }

        Optional<CartItem> existingCartItem = cart.getCartItems().stream()
                .filter(item -> item.getVariant().getVariantId().equals(variant.getVariantId()))
                .findFirst();

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItemDTO.getQuantity()); // 🔥 Cập nhật số lượng thay vì cộng dồn
            cartItem.setTotalPrice(cartItem.getUnitPrice() * cartItem.getQuantity());
            cartItemRepository.save(cartItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setVariant(variant);
            cartItem.setQuantity(cartItemDTO.getQuantity());
            cartItem.setUnitPrice(variant.getPrice());
            cartItem.setTotalPrice(variant.getPrice() * cartItemDTO.getQuantity());
            cartItemRepository.save(cartItem);
        }
        cart.getCartItems().size(); // 🔥 Kích hoạt Lazy Loading trước khi map DTO

        return CartMapper.INSTANCE.toDTO(cart);
    }


    // ✅ Xóa sản phẩm khỏi giỏ hàng
    public void removeItemFromCart(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }
}
