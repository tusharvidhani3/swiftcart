package com.swiftcart.swiftcart.features.cart;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepo extends JpaRepository<CartItem,Integer> {

    public List<CartItem> findAllByCart_User_UserId(Long userId);

    public List<CartItem> findAllByCart_CartId(Long cartId);

    public Optional<CartItem> findByCart_User_UserIdAndProduct_ProductId(Long userId, Long productId);

    public Optional<CartItem> findByCartItemId(Long cartItemId);

    @Query("SELECT SUM(ci.quantity) FROM CartItem ci WHERE ci.cart.cartId = :cartId")
    public Integer getTotalQuantityByCartId(@Param("cartId") Long cartId);

    public void deleteAllByCart_User_UserId(Long userId);
}
