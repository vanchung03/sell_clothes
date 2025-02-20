package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.enums.PaymentStatus;
import com.example.demo.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final VNPayService vnPayService;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository,
                          PaymentMethodRepository paymentMethodRepository, PaymentHistoryRepository paymentHistoryRepository,
                          VNPayService vnPayService) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentHistoryRepository = paymentHistoryRepository;
        this.vnPayService = vnPayService;
    }

    @Transactional
    public String createPayment(Long orderId, String methodCode) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        PaymentMethod method = paymentMethodRepository.findByCode(methodCode.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Phương thức thanh toán không hợp lệ"));

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setMethod(method);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentStatus(PaymentStatus.PENDING); // ✅ Mặc định là PENDING
        paymentRepository.save(payment);

        // ✅ Lưu lịch sử giao dịch
        PaymentHistory history = new PaymentHistory();
        history.setPayment(payment);
        history.setStatus("PENDING");
        history.setNote("Tạo giao dịch mới bằng " + methodCode);
        paymentHistoryRepository.save(history);

        // ✅ Xử lý các phương thức thanh toán
        switch (methodCode.toUpperCase()) {
            case "CASH":
            case "MOMO": // ✅ MoMo sẽ hoạt động giống Cash (Chưa tích hợp API MoMo)
                return "Thanh toán bằng " + methodCode + " đã được ghi nhận!";
            case "VNPAY":
                return vnPayService.createOrder(
                        (int) Math.round(order.getTotalAmount()), // Chuyển đổi `double` -> `int`
                        "Thanh toán đơn hàng " + order.getOrderId(),
                        "http://localhost:8080/api/v1/payments/vnpay-payment");
            default:
                throw new RuntimeException("Phương thức thanh toán không hợp lệ!");
        }
    }

    @Transactional
    public String updatePaymentStatus(Long orderId, String transactionStatus) {
        Payment payment;
        payment = paymentRepository.findByOrderOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch"));

        PaymentStatus status = transactionStatus.equals("00") ? PaymentStatus.COMPLETED : PaymentStatus.FAILED;
        payment.setPaymentStatus(status);
        paymentRepository.save(payment);

        // ✅ Lưu lịch sử cập nhật trạng thái
        PaymentHistory history = new PaymentHistory();
        history.setPayment(payment);
        history.setStatus(status.toString());
        history.setNote("Cập nhật trạng thái từ VNPay: " + transactionStatus);
        paymentHistoryRepository.save(history);

        return status == PaymentStatus.COMPLETED ? "Thanh toán thành công!" : "Thanh toán thất bại!";
    }
}
