package com.example.demo.controller;

import com.example.demo.entity.ShipMethod;
import com.example.demo.service.ShipMethodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/shipping")

public class ShipMethodController {
    private final ShipMethodService shipMethodService;

    public ShipMethodController(ShipMethodService shipMethodService) {
        this.shipMethodService = shipMethodService;
    }

    // ✅ API: Lấy danh sách tất cả phương thức vận chuyển
    @GetMapping("/methods")
    public ResponseEntity<List<ShipMethod>> getAllShipMethods() {
        return ResponseEntity.ok(shipMethodService.getAllShipMethods());
    }

    // ✅ API: Lấy phương thức vận chuyển theo ID
    @GetMapping("/methods/{id}")
    public ResponseEntity<ShipMethod> getShipMethodById(@PathVariable Long id) {
        Optional<ShipMethod> shipMethod = shipMethodService.getShipMethodById(id);
        return shipMethod.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ API: Thêm mới phương thức vận chuyển
    @PostMapping("/methods")
    public ResponseEntity<ShipMethod> createShipMethod(@RequestBody ShipMethod shipMethod) {
        return ResponseEntity.ok(shipMethodService.createShipMethod(shipMethod));
    }

    // ✅ API: Cập nhật phương thức vận chuyển
    @PutMapping("/methods/{id}")
    public ResponseEntity<ShipMethod> updateShipMethod(@PathVariable Long id, @RequestBody ShipMethod shipMethod) {
        return ResponseEntity.ok(shipMethodService.updateShipMethod(id, shipMethod));
    }

    // ✅ API: Xóa phương thức vận chuyển
    @DeleteMapping("/methods/{id}")
    public ResponseEntity<Void> deleteShipMethod(@PathVariable Long id) {
        shipMethodService.deleteShipMethod(id);
        return ResponseEntity.noContent().build();
    }
}
