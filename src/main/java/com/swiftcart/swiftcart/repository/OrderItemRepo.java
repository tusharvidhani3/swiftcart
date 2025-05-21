package com.swiftcart.swiftcart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.swiftcart.swiftcart.entity.OrderItem;

@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem, Integer> {

    public List<OrderItem> findAllByOrder_OrderId(Long orderId);
}
