package com.example.demo.controller;

import com.example.demo.dto.PaymentDTO;
import com.example.demo.service.PaymentService;
import com.example.demo.service.PayPalService;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.RelatedResources;
import com.paypal.api.payments.Sale;
import com.paypal.api.payments.Transaction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

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
     * ✅ API Callback từ PayPal & VNPay
     */
    @GetMapping("/callback")
    public RedirectView handlePaymentCallback(@RequestParam Map<String, String> params) {
        String method = params.get("method");
        String redirectUrl = "http://localhost:4200/payment-result?status=failed"; // Mặc định thanh toán thất bại

        try {
            if ("PAYPAL".equalsIgnoreCase(method)) {
                if (!params.containsKey("paymentId") || !params.containsKey("PayerID")) {
                    return new RedirectView("http://localhost:4200/payment-result?status=failed");
                }

                String paymentId = params.get("paymentId");
                String payerId = params.get("PayerID");

                Payment payment = payPalService.executePayment(paymentId, payerId);
                String orderIdStr = extractOrderIdFromPayment(payment);

                if (orderIdStr == null || orderIdStr.isEmpty()) {
                    return new RedirectView("http://localhost:4200/payment-result?status=failed");
                }

                Long orderId = Long.parseLong(orderIdStr);
                paymentService.updatePaymentStatusForPayPal(orderId, true, paymentId);
                redirectUrl = "http://localhost:4200/payment-result?status=success";
            } else if ("VNPAY".equalsIgnoreCase(method)) {
                String orderInfo = params.get("vnp_OrderInfo"); // Order ID
                String transactionStatus = params.get("vnp_TransactionStatus");
                String transactionCode = params.get("vnp_TransactionNo");

                if (orderInfo != null && transactionStatus != null) {
                    Long orderId = Long.parseLong(orderInfo.replaceAll("[^0-9]", "")); // Lọc ký tự số
                    paymentService.updatePaymentStatus(orderId, transactionStatus, transactionCode);

                    if ("00".equals(transactionStatus)) {
                        redirectUrl = "http://localhost:4200/payment-result?status=success";
                    } else {
                        redirectUrl = "http://localhost:4200/payment-result?status=failed";
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi xử lý callback: " + e.getMessage());
        }

        return new RedirectView(redirectUrl);
    }

    /**
     * ✅ API Hủy Thanh Toán PayPal
     */
    @GetMapping("/paypal-cancel")
    public RedirectView handlePayPalCancel(@RequestParam(required = false) String token) {
        return new RedirectView("http://localhost:4200/payment-result?status=cancel");
    }

    /**
     * ✅ API: Lấy danh sách tất cả giao dịch thanh toán
     */
    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    /**
     * ✅ API: Lấy thông tin thanh toán theo `orderId`
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentDTO> getPaymentByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }

    /**
     * ✅ Hàm trợ giúp để lấy Order ID từ phản hồi của PayPal
     */
    private String extractOrderIdFromPayment(Payment payment) {
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
                }
            }
        }
        return orderIdStr;
    }
}
