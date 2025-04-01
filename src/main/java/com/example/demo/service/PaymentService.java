package com.example.demo.service;

import com.example.demo.dto.PaymentDTO;
import com.example.demo.entity.*;
import com.example.demo.enums.PaymentStatus;
import com.example.demo.mapper.PaymentMapper;
import com.example.demo.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final VNPayService vnPayService;
    private final PayPalService payPalService;
    // ✅ Tỉ giá USD / VND (Có thể cập nhật theo thời gian thực bằng API nếu cần)
    private static final BigDecimal EXCHANGE_RATE = new BigDecimal("24000"); // 1 USD = 24,000 VND

    public PaymentService(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            PaymentMethodRepository paymentMethodRepository,
            PaymentHistoryRepository paymentHistoryRepository,
            VNPayService vnPayService,
            PayPalService payPalService) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentHistoryRepository = paymentHistoryRepository;
        this.vnPayService = vnPayService;
        this.payPalService = payPalService;
    }
    /**
     * ✅ Chuyển đổi VND sang USD
     */
    private BigDecimal convertVndToUsd(BigDecimal amountInVnd) {
        return amountInVnd.divide(EXCHANGE_RATE, 2, RoundingMode.HALF_UP); // Làm tròn đến 2 số thập phân
    }


    /**
     * ✅ Xử lý tạo hoặc tiếp tục thanh toán
     */
    @Transactional
    public String createPayment(Long orderId, String methodCode) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        PaymentMethod method = paymentMethodRepository.findByCode(methodCode.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Phương thức thanh toán không hợp lệ"));

        // ✅ Kiểm tra xem đơn hàng đã có thanh toán chưa
        Optional<Payment> existingPayment = paymentRepository.findByOrderOrderId(orderId);

        if (existingPayment.isPresent()) {
            Payment payment = existingPayment.get();

            // Nếu đơn hàng đã thanh toán trước đó
            if (payment.getPaymentStatus() == PaymentStatus.COMPLETED) {
                return "Đơn hàng đã được thanh toán trước đó. Không thể thực hiện lại thanh toán!";
            }

            // Nếu đơn hàng đang chờ thanh toán (PENDING), tiếp tục xử lý thanh toán
            return handlePayment(payment, methodCode);
        }

        // ✅ Nếu chưa có giao dịch -> Tạo giao dịch mới
        Payment newPayment = new Payment();
        newPayment.setOrder(order);
        newPayment.setMethod(method);
        newPayment.setAmount(order.getTotalAmount());
        newPayment.setPaymentStatus(PaymentStatus.PENDING);
        paymentRepository.save(newPayment);

        // ✅ Lưu lịch sử giao dịch
        PaymentHistory history = new PaymentHistory();
        history.setPayment(newPayment);
        history.setStatus("PENDING");
        history.setNote("Tạo giao dịch mới bằng " + methodCode);
        paymentHistoryRepository.save(history);

        return handlePayment(newPayment, methodCode);
    }

    /**
     * ✅ Xử lý phương thức thanh toán dựa trên `methodCode`
     */
    private String handlePayment(Payment payment, String methodCode) {
        switch (methodCode.toUpperCase()) {
            case "CASH":
            case "MOMO":
                return "Thanh toán bằng " + methodCode + " đã được ghi nhận!";
            case "VNPAY":
                return vnPayService.createOrder(
                        (int) Math.round(payment.getAmount()),
                        "Thanh toán đơn hàng " + payment.getOrder().getOrderId(),
                        "");
            case "PAYPAL":
                BigDecimal amountInUsd = convertVndToUsd(BigDecimal.valueOf(payment.getAmount()));
                return payPalService.createPayment(
                        payment.getOrder().getOrderId(),
                        amountInUsd.doubleValue(),
                        "USD",
                        "Thanh toán đơn hàng " + payment.getOrder().getOrderId(),
                        "http://localhost:8080/api/v1/payments/paypal-cancel",
                        "http://localhost:8080/api/v1/payments/callback?method=PAYPAL"
                );
            default:
                throw new RuntimeException("Phương thức thanh toán không hợp lệ!");
        }
    }

    /**
     * ✅ Cập nhật trạng thái thanh toán nếu thanh toán thành công hoặc bị hủy
     */
    @Transactional
    public String updatePaymentStatus(Long orderId, String transactionStatus, String transactionCode) {
        Payment payment = paymentRepository.findByOrderOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch"));

        // ✅ Nếu đã hoàn thành thì không cập nhật nữa
        if (payment.getPaymentStatus() == PaymentStatus.COMPLETED) {
            return "Thanh toán đã hoàn thành trước đó. Không cần cập nhật!";
        }

        // ✅ Xử lý các trạng thái của VNPay
        if ("00".equals(transactionStatus)) {
            // ✅ Nếu thanh toán thành công -> Cập nhật thành COMPLETED
            payment.setPaymentStatus(PaymentStatus.COMPLETED);

            if (transactionCode != null && !transactionCode.isEmpty()) {
                payment.setTransactionCode(transactionCode);
            }
            paymentRepository.save(payment);

            // ✅ Lưu lịch sử giao dịch
            PaymentHistory history = new PaymentHistory();
            history.setPayment(payment);
            history.setStatus("COMPLETED");
            history.setNote("Thanh toán thành công. Mã giao dịch: " + transactionCode);
            paymentHistoryRepository.save(history);

            return "Thanh toán thành công!";
        } else if ("02".equals(transactionStatus)) {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            // ✅ Nếu trạng thái "02" (Bị hủy) -> Chỉ lưu lịch sử, không thay đổi trạng thái
            PaymentHistory history = new PaymentHistory();
            history.setPayment(payment);
            history.setStatus("FAILED");
            history.setNote("Thanh toán bị hủy từ VNPay. Mã giao dịch: " + transactionCode);
            paymentHistoryRepository.save(history);

            return "Thanh toán đã bị hủy!";
        } else {
            // ✅ Nếu thanh toán thất bại -> Cập nhật thành FAILED
            payment.setPaymentStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);

            // ✅ Lưu lịch sử giao dịch
            PaymentHistory history = new PaymentHistory();
            history.setPayment(payment);
            history.setStatus("FAILED");
            history.setNote("Thanh toán thất bại. Trạng thái: " + transactionStatus + " | Mã giao dịch: " + transactionCode);
            paymentHistoryRepository.save(history);

            return "Thanh toán thất bại!";
        }

    }
    /**
     * ✅ Xử lý cập nhật trạng thái thanh toán PayPal
     */
    /**
     * ✅ Cập nhật trạng thái thanh toán PayPal & lưu lịch sử
     */
    @Transactional
    public String updatePaymentStatusForPayPal(Long orderId, boolean isSuccess, String transactionId) {
        try {
            Payment payment = paymentRepository.findByOrderOrderId(orderId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch PayPal cho Order ID: " + orderId));

            // ✅ Nếu đã hoàn thành thì không cần cập nhật
            if (payment.getPaymentStatus() == PaymentStatus.COMPLETED) {
                return "Thanh toán PayPal đã hoàn tất trước đó!";
            }

            PaymentStatus status = isSuccess ? PaymentStatus.COMPLETED : PaymentStatus.FAILED;
            payment.setPaymentStatus(status);

            if (transactionId != null && !transactionId.isEmpty()) {
                payment.setTransactionCode(transactionId);
            }

            paymentRepository.save(payment);

            // ✅ Luôn lưu vào lịch sử giao dịch
            PaymentHistory history = new PaymentHistory();
            history.setPayment(payment);
            history.setStatus(status.toString());
            history.setNote(isSuccess
                    ? "Thanh toán PayPal thành công. Mã giao dịch: " + transactionId
                    : "Thanh toán PayPal thất bại.");
            paymentHistoryRepository.save(history);

            return isSuccess ? "Thanh toán PayPal thành công!" : "Thanh toán PayPal thất bại!";
        } catch (Exception e) {
            // ✅ Ghi log lỗi để kiểm tra
            System.err.println("Lỗi cập nhật trạng thái PayPal: " + e.getMessage());

            // ✅ Lưu lịch sử giao dịch lỗi
            PaymentHistory errorHistory = new PaymentHistory();
            errorHistory.setStatus("FAILED");
            errorHistory.setNote("Lỗi xử lý PayPal: " + e.getMessage());
            paymentHistoryRepository.save(errorHistory);

            return "Lỗi xử lý thanh toán PayPal!";
        }
    }
    /**
     * ✅ Lấy tất cả giao dịch thanh toán và chuyển thành DTO
     */
    public List<PaymentDTO> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
                .map(PaymentMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * ✅ Lấy thông tin thanh toán theo `orderId` và chuyển thành DTO
     */
    public PaymentDTO getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch thanh toán cho đơn hàng: " + orderId));

        return PaymentMapper.INSTANCE.toDTO(payment);
    }


}
