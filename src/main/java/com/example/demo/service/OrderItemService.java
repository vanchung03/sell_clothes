package com.example.demo.service;

import com.example.demo.dto.OrderItemDTO;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.ProductVariant;
import com.example.demo.mapper.OrderItemMapper;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductVariantRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductVariantRepository productVariantRepository;

    public OrderItemService(OrderItemRepository orderItemRepository, OrderRepository orderRepository, ProductVariantRepository productVariantRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.productVariantRepository = productVariantRepository;
    }

    // ✅ Lấy tất cả OrderItem
    public List<OrderItemDTO> getAllOrderItems() {
        return orderItemRepository.findAll()
                .stream()
                .map(OrderItemMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ Lấy OrderItem theo ID
    public OrderItemDTO getOrderItemById(Long id) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy OrderItem"));
        return OrderItemMapper.INSTANCE.toDTO(orderItem);
    }

    // ✅ Lấy danh sách OrderItem theo OrderId
    public List<OrderItemDTO> getOrderItemsByOrderId(Long orderId) {
        return orderItemRepository.findByOrderOrderId(orderId)
                .stream()
                .map(OrderItemMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ Thêm OrderItem mới
    @Transactional
    public OrderItemDTO createOrderItem(OrderItemDTO orderItemDTO) {
        Order order = orderRepository.findById(orderItemDTO.getOrderId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Order"));

        ProductVariant variant = productVariantRepository.findById(orderItemDTO.getVariantId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setVariant(variant);
        orderItem.setQuantity(orderItemDTO.getQuantity());
        orderItem.setUnitPrice(orderItemDTO.getUnitPrice());
        orderItem.setTotalPrice(orderItemDTO.getTotalPrice());

        orderItemRepository.save(orderItem);

        return OrderItemMapper.INSTANCE.toDTO(orderItem);
    }

    // ✅ Cập nhật OrderItem
    @Transactional
    public OrderItemDTO updateOrderItem(Long id, OrderItemDTO orderItemDTO) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy OrderItem"));

        ProductVariant variant = productVariantRepository.findById(orderItemDTO.getVariantId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        orderItem.setVariant(variant);
        orderItem.setQuantity(orderItemDTO.getQuantity());
        orderItem.setUnitPrice(orderItemDTO.getUnitPrice());
        orderItem.setTotalPrice(orderItemDTO.getTotalPrice());

        orderItemRepository.save(orderItem);

        return OrderItemMapper.INSTANCE.toDTO(orderItem);
    }

    // ✅ Xóa OrderItem
    @Transactional
    public void deleteOrderItem(Long id) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy OrderItem"));
        orderItemRepository.delete(orderItem);
    }
}
