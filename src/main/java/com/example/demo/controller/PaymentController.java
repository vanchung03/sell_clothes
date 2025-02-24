package com.example.demo.controller;

import com.example.demo.dto.PaymentDTO;
import com.example.demo.service.PaymentService;
import com.example.demo.service.PayPalService;
import com.paypal.api.payments.RelatedResources;
import com.paypal.api.payments.Sale;
import com.paypal.api.payments.Transaction;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.paypal.api.payments.Payment;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final PayPalService payPalService;

    public PaymentController(PaymentService paymentService, PayPalService payPalService) {
        this.paymentService = paymentService;
        this.payPalService = payPalService;
    }

    /**
     * ✅ API Xử Lý Thanh Toán (VNPay, PayPal, MoMo, Tiền Mặt)
     */
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<String> createPayment(@PathVariable Long orderId, @RequestParam String methodCode) {
        String response = paymentService.createPayment(orderId, methodCode);
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ API Callback tự động cho VNPay & PayPal
     */
    @GetMapping("/callback")
    public ResponseEntity<String> handleVNPayCallback(@RequestParam Map<String, String> params) {
        if (!params.containsKey("method")) {
            return ResponseEntity.badRequest().body("Thiếu tham số phương thức thanh toán!");
        }

        String method = params.get("method");

        // ✅ Xử lý PayPal
        if ("PAYPAL".equalsIgnoreCase(method)) {
            if (!params.containsKey("paymentId") || !params.containsKey("PayerID")) {
                return ResponseEntity.badRequest().body("Dữ liệu callback PayPal không hợp lệ! Thiếu paymentId hoặc PayerID.");
            }

            String paymentId = params.get("paymentId");
            String payerId = params.get("PayerID");

            try {
                Payment payment = payPalService.executePayment(paymentId, payerId);

                // ✅ Ghi log toàn bộ response từ PayPal để kiểm tra
                System.out.println("🔍 PayPal Payment Response: " + payment.toJSON());

                // ✅ Lấy Order ID từ PayPal transaction
                String orderIdStr = null;

                // 🛠 Cách 1: Lấy từ InvoiceNumber (ưu tiên nếu có)
                for (Transaction transaction : payment.getTransactions()) {
                    if (transaction.getInvoiceNumber() != null && !transaction.getInvoiceNumber().isEmpty()) {
                        orderIdStr = transaction.getInvoiceNumber();
                        break;
                    }
                }

                // 🛠 Cách 2: Lấy từ Custom Field (nếu có)
                if (orderIdStr == null || orderIdStr.isEmpty()) {
                    orderIdStr = payment.getTransactions().get(0).getCustom();
                }

                // 🛠 Cách 3: Lấy từ Description (nếu có)
                if (orderIdStr == null || orderIdStr.isEmpty()) {
                    String description = payment.getTransactions().get(0).getDescription();
                    if (description != null && description.matches(".*\\d+.*")) {
                        orderIdStr = description.replaceAll("\\D+", ""); // Loại bỏ ký tự không phải số
                    }
                }

                // 🛠 Cách 4: Lấy từ RelatedResources → Sale → ParentPayment (KHÔNG phải Order ID)
                if (orderIdStr == null || orderIdStr.isEmpty()) {
                    List<RelatedResources> relatedResources = payment.getTransactions().get(0).getRelatedResources();
                    if (!relatedResources.isEmpty()) {
                        Sale sale = relatedResources.get(0).getSale();
                        if (sale != null) {
                            orderIdStr = sale.getParentPayment(); // ❌ Không phải Order ID, chỉ để debug
                            System.err.println("⚠️ Chú ý: `parent_payment` là Payment ID, không phải Order ID!");
                        }
                    }
                }

                // ✅ Nếu vẫn không có Order ID, báo lỗi
                if (orderIdStr == null || orderIdStr.isEmpty()) {
                    System.err.println("⚠️ Không lấy được Order ID từ PayPal! Kiểm tra response.");
                    return ResponseEntity.status(500).body("Không lấy được Order ID từ PayPal!");
                }

                // ✅ Chỉ lấy số từ Order ID
                orderIdStr = orderIdStr.replaceAll("\\D+", "");
                Long orderId = Long.parseLong(orderIdStr);

                return ResponseEntity.ok(paymentService.updatePaymentStatusForPayPal(orderId, true, paymentId));
            } catch (Exception e) {
                System.err.println("❌ Lỗi xử lý callback PayPal: " + e.getMessage());
                return ResponseEntity.status(500).body("Lỗi xử lý thanh toán PayPal!");
            }
        }


        // Xử lý VNPay
        if ("VNPAY".equalsIgnoreCase(method)) {
            String orderInfo = params.get("vnp_OrderInfo"); // Order ID
            String transactionStatus = params.get("vnp_TransactionStatus");
            String transactionCode = params.get("vnp_TransactionNo");

            if (orderInfo == null || transactionStatus == null) {
                return ResponseEntity.badRequest().body("Dữ liệu callback không hợp lệ!");
            }

            Long orderId = Long.parseLong(orderInfo.replaceAll("[^0-9]", "")); // Lọc ký tự số
            String result = paymentService.updatePaymentStatus(orderId, transactionStatus, transactionCode);
            return ResponseEntity.ok(result);
        }

        return ResponseEntity.badRequest().body("Phương thức thanh toán không hợp lệ!");
    }


    /**
     * ✅ API Hủy Thanh Toán PayPal
     */
    @GetMapping("/paypal-cancel")
    public ResponseEntity<String> handlePayPalCancel(@RequestParam String orderId) {
        try {
            Long parsedOrderId = Long.parseLong(orderId);
            return ResponseEntity.ok(paymentService.updatePaymentStatusForPayPal(parsedOrderId, false, null));
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Lỗi xử lý hủy thanh toán PayPal");
        }
    }
    // ✅ API: Lấy danh sách tất cả giao dịch thanh toán
    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    // ✅ API: Lấy thông tin thanh toán theo `orderId`
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentDTO> getPaymentByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }
}
