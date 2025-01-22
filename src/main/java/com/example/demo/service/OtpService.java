package com.example.demo.service;

import com.example.demo.dto.auth.OtpValidationResponse;
import com.example.demo.entity.Otp;
import com.example.demo.repository.OtpRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Xử lý và kiểm tra email
     *
     * @param requestBody Dữ liệu gửi lên từ request
     * @return Chuỗi email đã được xử lý
     * @throws IllegalArgumentException nếu email không hợp lệ
     */
    private String processAndValidateEmail(Object requestBody) {
        String email;

        if (requestBody instanceof String) {
            // Nếu requestBody là chuỗi trực tiếp
            email = ((String) requestBody).replace("\"", "").trim();
        } else if (requestBody instanceof Map) {
            // Nếu requestBody là JSON (được parse thành Map)
            email = (String) ((Map<?, ?>) requestBody).get("email");
        } else {
            throw new IllegalArgumentException("Dữ liệu gửi lên không hợp lệ!");
        }

        // Kiểm tra email có hợp lệ không
        if (email == null || !email.contains("@") || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không hợp lệ! Vui lòng nhập đúng địa chỉ email.");
        }

        return email;
    }

    /**
     * Gửi OTP qua email
     *
     * @param requestBody Dữ liệu request chứa email
     */
    public void sendOtpEmail(Object requestBody) {
        // Xử lý và kiểm tra email
        String email = processAndValidateEmail(requestBody);

        // Kiểm tra xem email có tồn tại trong hệ thống không
        if (!userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email không tồn tại trong hệ thống!");
        }

        // Tạo OTP ngẫu nhiên
        String otp = generateOtp();

        // Thời gian hết hạn OTP
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(1);

        // Lưu hoặc cập nhật OTP vào cơ sở dữ liệu
        Otp otpEntity = otpRepository.findByEmail(email);
        if (otpEntity != null) {
            otpEntity.setOtp(otp);
            otpEntity.setExpiredAt(expiredAt);
            otpEntity.setAttemptCount(0); // Reset số lần thử
        } else {
            otpEntity = Otp.builder()
                    .email(email)
                    .otp(otp)
                    .createdAt(LocalDateTime.now())
                    .expiredAt(expiredAt)
                    .attemptCount(0)
                    .build();
        }
        otpRepository.save(otpEntity);

        // Gửi email với OTP
        sendEmail(email, otp);
    }

    /**
     * Gửi email với OTP
     */
    private void sendEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Mã OTP xác thực");
        message.setText("Mã OTP của bạn là: " + otp);
        mailSender.send(message);
    }

    /**
     * Tạo OTP ngẫu nhiên (6 chữ số)
     */
    private String generateOtp() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    /**
     * Kiểm tra OTP và số lần thử
     */
    public OtpValidationResponse validateOtp(String email, String otpInput) {
        Otp otpEntity = otpRepository.findByEmail(email);

        if (otpEntity == null) {
            return new OtpValidationResponse(false, "Không tìm thấy OTP cho email này!");
        }

        if (otpEntity.getAttemptCount() >= 3) {
            return new OtpValidationResponse(false, "Bạn đã thử quá 3 lần, vui lòng yêu cầu OTP mới!");
        }

        if (otpEntity.getExpiredAt().isBefore(LocalDateTime.now())) {
            return new OtpValidationResponse(false, "OTP đã hết hạn!");
        }

        if (!otpEntity.getOtp().equals(otpInput)) {
            otpEntity.setAttemptCount(otpEntity.getAttemptCount() + 1);
            otpRepository.save(otpEntity);
            return new OtpValidationResponse(false, "OTP không chính xác!");
        }

        return new OtpValidationResponse(true, "OTP hợp lệ!");
    }
}
