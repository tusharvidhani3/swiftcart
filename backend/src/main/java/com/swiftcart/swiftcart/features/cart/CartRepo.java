package com.swiftcart.swiftcart.features.cart;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepo extends JpaRepository<Cart,Integer> {

    public Optional<Cart> findByUser_UserId(Long userId);
    public Cart findByCartId(Long cartId);
}
