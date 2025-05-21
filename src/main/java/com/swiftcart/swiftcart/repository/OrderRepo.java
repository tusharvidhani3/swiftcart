package com.swiftcart.swiftcart.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.swiftcart.swiftcart.entity.Order;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {

    public Page<Order> findAllByUser_UserId(Long userId, Pageable pageable);
    public Order findByOrderId(Long orderId);
}
