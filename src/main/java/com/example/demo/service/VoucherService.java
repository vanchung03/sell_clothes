package com.example.demo.service;

import com.example.demo.dto.VoucherDTO;
import com.example.demo.entity.Voucher;
import com.example.demo.enums.DiscountType;
import com.example.demo.mapper.VoucherMapper;
import com.example.demo.repository.VoucherRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class VoucherService {
    private final VoucherRepository voucherRepository;

    public VoucherService(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    // ✅ Lấy danh sách tất cả vouchers
    public List<VoucherDTO> getAllVouchers() {
        return voucherRepository.findAll().stream()
                .map(VoucherMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    public VoucherDTO getVoucherByCode(String voucherCode) {
        Optional<Voucher> voucherOpt = voucherRepository.findByVoucherCode(voucherCode);

        if (voucherOpt.isEmpty()) {
            throw new RuntimeException("Voucher không tồn tại!");
        }

        Voucher voucher = voucherOpt.get();

        // ✅ Kiểm tra nếu số lượng voucher đã hết
        if (voucher.getQuantity() <= 0) {
            throw new RuntimeException("Voucher đã hết số lượng sử dụng!");
        }

        VoucherDTO voucherDTO = VoucherMapper.INSTANCE.toDTO(voucher);
        voucherDTO.setQuantity(voucher.getQuantity());

        return voucherDTO;
    }



    // ✅ Tạo voucher mới
    public VoucherDTO createVoucher(VoucherDTO voucherDTO) {
        Voucher voucher = VoucherMapper.INSTANCE.toEntity(voucherDTO);
        return VoucherMapper.INSTANCE.toDTO(voucherRepository.save(voucher));
    }

    // ✅ Cập nhật voucher
    public VoucherDTO updateVoucher(Long voucherId, VoucherDTO voucherDTO) {
        Voucher voucher = (Voucher) voucherRepository.findByVoucherId(voucherId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher"));

        voucher.setVoucherCode(voucherDTO.getVoucherCode());
        voucher.setDiscountAmount(voucherDTO.getDiscountAmount());
        voucher.setExpiryDate(voucherDTO.getExpiryDate());
        voucher.setActive(voucherDTO.isActive()); // ✅ Đổi từ `isActive` thành `active`
        voucher.setDiscountType(DiscountType.valueOf(voucherDTO.getDiscountType()));
        voucher.setMaxDiscount(voucherDTO.getMaxDiscount());
        // ✅ Cập nhật số lượng (cho phép chỉnh sửa lên 100)
        if (voucherDTO.getQuantity() > 0 && voucherDTO.getQuantity() <= 100) {
            voucher.setQuantity(voucherDTO.getQuantity());
        }
        return VoucherMapper.INSTANCE.toDTO(voucherRepository.save(voucher));
    }

    // ✅ Xóa voucher theo ID
    public void deleteVoucher(Long voucherId) {
        if (!voucherRepository.existsById(voucherId)) {
            throw new RuntimeException("Không tìm thấy voucher để xóa");
        }
        voucherRepository.deleteById(voucherId);
    }
}
