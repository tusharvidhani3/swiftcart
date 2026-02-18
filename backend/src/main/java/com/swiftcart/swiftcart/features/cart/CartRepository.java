package com.swiftcart.swiftcart.features.cart;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart,Integer> {

    Optional<Cart> findByUserId(Long userId);
    Cart findById(Long id);
}
