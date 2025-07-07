package com.swiftcart.swiftcart.features.order;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem, Integer> {

    public Optional<OrderItem> findByOrderItemId(Long orderItemId);
    public List<OrderItem> findAllByOrder_OrderId(Long orderId);
}
