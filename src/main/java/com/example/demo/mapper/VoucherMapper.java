package com.example.demo.mapper;

import com.example.demo.dto.VoucherDTO;
import com.example.demo.entity.Voucher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface VoucherMapper {
    VoucherMapper INSTANCE = Mappers.getMapper(VoucherMapper.class);

    @Mapping(source = "active", target = "active") // ✅ Đảm bảo MapStruct map đúng "active"
    VoucherDTO toDTO(Voucher voucher);

    @Mapping(source = "active", target = "active") // ✅ Map ngược lại từ DTO sang Entity
    Voucher toEntity(VoucherDTO voucherDTO);
}
