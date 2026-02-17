package com.swiftcart.swiftcart.features.cart;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart,Integer> {

    public Optional<Cart> findByUserId(Long userId);
    public Cart findById(Long id);
}
