package com.swiftcart.swiftcart.features.cart;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Integer> {

    List<CartItem> findByCartUserId(Long userId);

    List<CartItem> findByCartId(Long cartId);

    Optional<CartItem> findByCartUserIdAndProductId(Long userId, Long productId);

    Optional<CartItem> findById(Long id);

    @Query("SELECT SUM(ci.quantity) FROM CartItem ci WHERE ci.cart.id = :cartId")
    Integer sumQuantityByCartId(@Param("cartId") Long cartId);

    void deleteByCartUserId(Long userId);
}
