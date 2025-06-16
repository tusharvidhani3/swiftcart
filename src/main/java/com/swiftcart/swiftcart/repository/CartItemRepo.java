package com.swiftcart.swiftcart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.swiftcart.swiftcart.entity.CartItem;

@Repository
public interface CartItemRepo extends JpaRepository<CartItem,Integer> {

    public List<CartItem> findAllByCart_User_UserId(Long userId);

    public List<CartItem> findAllByCart_CartId(Long cartId);

    public Optional<CartItem> findByCart_User_UserIdAndProduct_ProductId(Long userId, Long productId);

    public int deleteByCartItemId(Long cartItemId);

    public Optional<CartItem> findByCartItemId(Long cartItemId);

    public void deleteAllByCart_CartId(Long cartId);

    @Modifying
    @Query("UPDATE CartItem c SET c.quantity = :quantity WHERE c.cartItemId = :cartItemId")
    public void updateQuantity(@Param("cartItemId") Long cartItemId, @Param("quantity") int quantity);

    @Query("SELECT SUM(ci.quantity) FROM CartItem ci WHERE ci.cart.cartId = :cartId")
    public Integer getTotalQuantityByCartId(@Param("cartId") Long cartId);
}
