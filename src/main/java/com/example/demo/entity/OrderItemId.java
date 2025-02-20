package com.example.demo.entity;

import java.io.Serializable;
import java.util.Objects;

public class OrderItemId implements Serializable {
    private Long order;
    private Long variant;

    public OrderItemId() {}

    public OrderItemId(Long order, Long variant) {
        this.order = order;
        this.variant = variant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemId that = (OrderItemId) o;
        return Objects.equals(order, that.order) && Objects.equals(variant, that.variant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, variant);
    }
}
