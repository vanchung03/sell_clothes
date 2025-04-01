package com.example.demo.service;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;



@Service
public class OrderMailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOrderConfirmationEmail(Order order) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            // true: hỗ trợ multipart, UTF-8 định dạng
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(order.getUser().getEmail());
            helper.setSubject("Xác nhận đơn hàng #" + order.getOrderId());
            helper.setFrom("chungvan704@gmail.com");

            // Xây dựng nội dung email HTML
            StringBuilder htmlContent = new StringBuilder();
            htmlContent.append("<html><body style='font-family: Arial, sans-serif;'>")
                    .append("<h2 style='color:#2e6c80;'>Xác nhận đơn hàng #")
                    .append(order.getOrderId()).append("</h2>")
                    .append("<p>Chào <strong>").append(order.getUser().getFullName()).append("</strong>,</p>")
                    .append("<p>Cảm ơn bạn đã đặt hàng tại cửa hàng của chúng tôi. Dưới đây là thông tin đơn hàng của bạn:</p>")
                    .append("<table style='border-collapse: collapse; width: 100%;'>")
                    .append("<tr>")
                    .append("<th style='border: 1px solid #dddddd; padding: 8px;'>Mã đơn hàng</th>")
                    .append("<th style='border: 1px solid #dddddd; padding: 8px;'>Ngày đặt hàng</th>")
                    .append("<th style='border: 1px solid #dddddd; padding: 8px;'>Tổng tiền</th>")
                    .append("</tr>")
                    .append("<tr>")
                    .append("<td style='border: 1px solid #dddddd; padding: 8px;'>").append(order.getOrderId()).append("</td>")
                    .append("<td style='border: 1px solid #dddddd; padding: 8px;'>").append(order.getCreatedAt()).append("</td>")
                    .append("<td style='border: 1px solid #dddddd; padding: 8px;'>").append(order.getTotalAmount()).append("</td>")
                    .append("</tr>")
                    .append("</table>")
                    .append("<p><strong>Phương thức vận chuyển:</strong> ").append(order.getShipMethod().getName()).append("<br>")
                    .append("<strong>Phí vận chuyển:</strong> ").append(order.getShippingFee()).append("<br>")
                    .append("<strong>Trạng thái:</strong> ").append(order.getStatus()).append("</p>")
                    .append("<h3>Sản phẩm đã đặt</h3>")
                    .append("<ul>");
            for (OrderItem item : order.getOrderItems()) {
                htmlContent.append("<li>")
                        .append("SKU: ").append(item.getVariant().getSku())
                        .append(" | Số lượng: ").append(item.getQuantity())
                        .append(" | Giá: ").append(item.getUnitPrice())
                        .append("</li>");
            }
            htmlContent.append("</ul>")
                    .append("<p>Chúc bạn có trải nghiệm mua sắm tuyệt vời!</p>")
                    .append("</body></html>");

            helper.setText(htmlContent.toString(), true);

            mailSender.send(message);
        } catch (Exception ex) {
            ex.printStackTrace();
            // Xử lý lỗi tùy yêu cầu (log, throw exception, v.v.)
        }
    }
}
