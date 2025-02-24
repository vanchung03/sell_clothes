package com.example.demo.controller;

import com.example.demo.dto.OrderItemDTO;
import com.example.demo.service.OrderItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/order-items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    // ✅ Lấy tất cả OrderItem
    @GetMapping
    public ResponseEntity<List<OrderItemDTO>> getAllOrderItems() {
        return ResponseEntity.ok(orderItemService.getAllOrderItems());
    }

    // ✅ Lấy danh sách OrderItem theo orderId
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderItemDTO>> getOrderItemsByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderItemService.getOrderItemsByOrderId(orderId));
    }

    // ✅ Lấy OrderItem theo ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderItemDTO> getOrderItemById(@PathVariable Long id) {
        return ResponseEntity.ok(orderItemService.getOrderItemById(id));
    }

    // ✅ Thêm OrderItem mới
    @PostMapping
    public ResponseEntity<OrderItemDTO> createOrderItem(@RequestBody OrderItemDTO orderItemDTO) {
        return ResponseEntity.ok(orderItemService.createOrderItem(orderItemDTO));
    }

    // ✅ Cập nhật OrderItem
    @PutMapping("/{id}")
    public ResponseEntity<OrderItemDTO> updateOrderItem(@PathVariable Long id, @RequestBody OrderItemDTO orderItemDTO) {
        return ResponseEntity.ok(orderItemService.updateOrderItem(id, orderItemDTO));
    }

    // ✅ Xóa OrderItem
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrderItem(@PathVariable Long id) {
        orderItemService.deleteOrderItem(id);
        return ResponseEntity.ok("Xóa OrderItem thành công!");
    }
}
