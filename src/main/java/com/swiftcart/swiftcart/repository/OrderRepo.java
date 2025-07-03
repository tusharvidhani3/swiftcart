package com.swiftcart.swiftcart.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.swiftcart.swiftcart.entity.Order;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {

    public Page<Order> findAllByCustomer_User_UserId(Long userId, Pageable pageable);
    public Optional<Order> findByOrderId(Long orderId);
}
