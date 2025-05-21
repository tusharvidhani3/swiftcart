package com.swiftcart.swiftcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.swiftcart.swiftcart.entity.Cart;
import com.swiftcart.swiftcart.entity.User;

@Repository
public interface CartRepo extends JpaRepository<Cart,Integer> {

    public Cart findByUser_UserId(Long userId);
    public Cart findByCartId(Long cartId);
    public User findUserByCartId(Long cartId);
    public Cart findByUser(User user);
}
