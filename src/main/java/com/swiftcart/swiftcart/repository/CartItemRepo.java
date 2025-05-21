package com.swiftcart.swiftcart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.swiftcart.swiftcart.entity.CartItem;

import jakarta.transaction.Transactional;

@Repository
public interface CartItemRepo extends JpaRepository<CartItem,Integer> {

    public List<CartItem> findAllByCart_User_UserId(Long userId);

    public List<CartItem> findAllByCart_CartId(Long cartId);

    public Optional<CartItem> findByCart_User_UserIdAndProduct_ProductId(Long userId, Long productId);

    @Modifying
    @Transactional
    public int deleteByCartItemId(Long cartItemId);

    public Optional<CartItem> findByCartItemId(Long cartItemId);

    public void deleteByCart_CartId(Long cartId);
}
