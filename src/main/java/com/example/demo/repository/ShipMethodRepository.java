package com.example.demo.repository;

import com.example.demo.entity.ShipMethod;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipMethodRepository extends JpaRepository<ShipMethod, Long> {

}
