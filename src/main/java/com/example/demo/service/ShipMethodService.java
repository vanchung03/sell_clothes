package com.example.demo.service;

import com.example.demo.entity.ShipMethod;
import com.example.demo.repository.ShipMethodRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShipMethodService {
    private final ShipMethodRepository shipMethodRepository;

    public ShipMethodService(ShipMethodRepository shipMethodRepository) {
        this.shipMethodRepository = shipMethodRepository;
    }

    // ✅ Lấy tất cả phương thức vận chuyển
    public List<ShipMethod> getAllShipMethods() {
        return shipMethodRepository.findAll();
    }

    // ✅ Lấy phương thức vận chuyển theo ID
    public Optional<ShipMethod> getShipMethodById(Long id) {
        return shipMethodRepository.findById(id);
    }

    // ✅ Thêm mới phương thức vận chuyển
    @Transactional
    public ShipMethod createShipMethod(ShipMethod shipMethod) {
        return shipMethodRepository.save(shipMethod);
    }

    // ✅ Cập nhật phương thức vận chuyển
    @Transactional
    public ShipMethod updateShipMethod(Long id, ShipMethod updatedShipMethod) {
        return shipMethodRepository.findById(id).map(existingShipMethod -> {
            existingShipMethod.setName(updatedShipMethod.getName());
            existingShipMethod.setDescription(updatedShipMethod.getDescription());
            existingShipMethod.setShippingFee(updatedShipMethod.getShippingFee());
            return shipMethodRepository.save(existingShipMethod);
        }).orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức vận chuyển"));
    }

    // ✅ Xóa phương thức vận chuyển
    @Transactional
    public void deleteShipMethod(Long id) {
        if (!shipMethodRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy phương thức vận chuyển để xóa");
        }
        shipMethodRepository.deleteById(id);
    }
}
