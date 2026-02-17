package com.swiftcart.swiftcart.features.order;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    public Page<Order> findByUser_Id(Long userId, Pageable pageable);
    public Optional<Order> findById(Long id);
}
