package com.example.demo.service;

import com.example.demo.dto.OrderDTO;
import com.example.demo.entity.*;
import com.example.demo.enums.OrderStatus;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserAddressRepository userAddressRepository;
    private final ProductVariantRepository productVariantRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                        CartRepository cartRepository, CartItemRepository cartItemRepository,
                        UserAddressRepository userAddressRepository , ProductVariantRepository productVariantRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userAddressRepository = userAddressRepository;
        this.productVariantRepository = productVariantRepository;
    }

    // ✅ 1. Tạo đơn hàng từ giỏ hàng
    @Transactional
    public OrderDTO checkout(Long userId, Long addressId) {
        Cart cart = cartRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("Giỏ hàng trống!"));

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Không có sản phẩm trong giỏ hàng.");
        }

        // ✅ Tìm địa chỉ giao hàng
        UserAddress address = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));

        // ✅ Tạo đơn hàng
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setAddress(address);
        order.setTotalAmount(cart.getCartItems().stream().mapToDouble(CartItem::getTotalPrice).sum());
        order.setShippingFee(30000.0);
        order.setStatus(OrderStatus.PENDING);
        orderRepository.save(order);

        // ✅ Chuyển CartItems thành OrderItems & Cập nhật stockQuantity
        for (CartItem cartItem : cart.getCartItems()) {
            ProductVariant variant = cartItem.getVariant();

            // 🔥 Kiểm tra tồn kho
            if (variant.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Sản phẩm " + variant.getSku() + " không đủ hàng.");
            }

            // ✅ Giảm số lượng tồn kho
            variant.setStockQuantity(variant.getStockQuantity() - cartItem.getQuantity());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setVariant(variant);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItem.setTotalPrice(cartItem.getTotalPrice());
            orderItemRepository.save(orderItem);

            // ✅ Cập nhật tồn kho vào database
            productVariantRepository.save(variant);
        }

        // ✅ Xóa giỏ hàng sau khi đặt hàng
        cartItemRepository.deleteAll(cart.getCartItems());
        cartRepository.delete(cart);

        return OrderMapper.INSTANCE.toDTO(order);
    }


    // ✅ 2. Lấy danh sách đơn hàng của người dùng
    public List<OrderDTO> getOrdersByUser(Long userId) {
        return orderRepository.findByUserUserId(userId)
                .stream()
                .map(OrderMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ 3. Lấy chi tiết đơn hàng theo ID
    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        return OrderMapper.INSTANCE.toDTO(order);
    }

    // ✅ 4. Cập nhật trạng thái đơn hàng
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        order.setStatus(status);
        orderRepository.save(order);

        return OrderMapper.INSTANCE.toDTO(order);
    }

    // ✅ 5. Xóa đơn hàng
    @Transactional
    public void deleteOrder(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new RuntimeException("Không tìm thấy đơn hàng để xóa");
        }
        orderRepository.deleteById(orderId);
    }
}
