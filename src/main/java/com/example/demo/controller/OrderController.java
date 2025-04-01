package com.example.demo.controller;

import com.example.demo.dto.OrderDTO;
import com.example.demo.entity.Order;
import com.example.demo.entity.PaymentHistory;
import com.example.demo.enums.OrderStatus;
import com.example.demo.service.OrderMailService;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;
    @Autowired
    private OrderMailService orderMailService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ✅ API Checkout (Chuyển giỏ hàng thành đơn hàng)
    @PostMapping("/{userId}/checkout")
    public ResponseEntity<OrderDTO> checkoutCart(
            @PathVariable Long userId,
            @RequestParam Long addressId,
            @RequestParam Long shipMethodId,
            @RequestParam(required = false) String voucherCode // ✅ Thêm voucherCode vào query params
    ) {
        return ResponseEntity.ok(orderService.checkout(userId, addressId, shipMethodId, voucherCode));
    }

    @PostMapping("/{orderId}/sendMail")
    public ResponseEntity<String> sendOrderConfirmationMail(@PathVariable Long orderId) {
        // Lấy Order entity với thông tin liên quan đã được nạp (sử dụng fetch join)
        Order order = orderService.getOrderEntityById(orderId);
        // Gửi email xác nhận
        orderMailService.sendOrderConfirmationEmail(order);
        return ResponseEntity.ok("Email đã được gửi thành công!");
    }


    // ✅ 2. Lấy danh sách đơn hàng của một người dùng
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    // ✅ 3. Lấy chi tiết đơn hàng theo ID
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    // ✅ 4. Cập nhật trạng thái đơn hàng
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase()); // ✅ Chuyển chuỗi sang Enum
            return ResponseEntity.ok(orderService.updateOrderStatus(orderId, orderStatus));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // ✅ Xử lý lỗi nếu status không hợp lệ
        }
    }

    // ✅ 5. Xóa đơn hàng
    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok("Đã xóa đơn hàng thành công!");
    }
//     ✅ 6. Lấy tất cả đơn hàng
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }


}
