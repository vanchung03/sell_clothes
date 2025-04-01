package com.example.demo.controller;

import com.example.demo.dto.VoucherDTO;
import com.example.demo.service.VoucherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/vouchers")
public class VoucherController {
    private final VoucherService voucherService;

    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    // ✅ Lấy tất cả vouchers
    @GetMapping
    public ResponseEntity<List<VoucherDTO>> getAllVouchers() {
        return ResponseEntity.ok(voucherService.getAllVouchers());
    }

    // ✅ Lấy voucher theo mã voucherCode
    @GetMapping("/{voucherCode}")
    public ResponseEntity<VoucherDTO> getVoucherByCode(@PathVariable String voucherCode) {
        VoucherDTO voucher = voucherService.getVoucherByCode(voucherCode);
        return voucher != null ? ResponseEntity.ok(voucher) : ResponseEntity.notFound().build();
    }

    // ✅ Tạo voucher mới
    @PostMapping
    public ResponseEntity<VoucherDTO> createVoucher(@RequestBody VoucherDTO voucherDTO) {
        return ResponseEntity.ok(voucherService.createVoucher(voucherDTO));
    }

    // ✅ Cập nhật voucher
    @PutMapping("/{voucherId}")
    public ResponseEntity<VoucherDTO> updateVoucher(@PathVariable Long voucherId, @RequestBody VoucherDTO voucherDTO) {
        return ResponseEntity.ok(voucherService.updateVoucher(voucherId, voucherDTO));
    }

    // ✅ Xóa voucher theo ID
    @DeleteMapping("/{voucherId}")
    public ResponseEntity<Void> deleteVoucher(@PathVariable Long voucherId) {
        voucherService.deleteVoucher(voucherId);
        return ResponseEntity.noContent().build();
    }
}
