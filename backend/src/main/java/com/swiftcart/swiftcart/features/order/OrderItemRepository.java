package com.swiftcart.swiftcart.features.order;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    public Optional<OrderItem> findById(Long id);
    public List<OrderItem> findByOrderId(Long orderId);
}
