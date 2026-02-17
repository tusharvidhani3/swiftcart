package com.swiftcart.swiftcart.features.cart;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Integer> {

    public List<CartItem> findByCartUserId(Long userId);

    public List<CartItem> findByCartId(Long cartId);

    public Optional<CartItem> findByCartUserIdAndProductId(Long userId, Long productId);

    public Optional<CartItem> findById(Long id);

    @Query("SELECT SUM(ci.quantity) FROM CartItem ci WHERE ci.cart.id = :cartId")
    public Integer sumQuantityByCartId(@Param("cartId") Long cartId);

    public void deleteByCartUserId(Long userId);
}
