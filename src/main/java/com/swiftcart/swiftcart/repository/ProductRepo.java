package com.swiftcart.swiftcart.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.swiftcart.swiftcart.entity.Product;
import com.swiftcart.swiftcart.payload.ProductResponse;

@Repository
public interface ProductRepo extends JpaRepository<Product,Integer> {

    public Optional<Product> findByProductId(Long productId);

    @Modifying
    public void deleteByProductId(Long productId);
    
    @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    public Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);
    public Page<ProductResponse> findByCategory(String category, Pageable pageable);

    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock + :change WHERE p.productId = :productId AND p.stock + :change >= 0")
    public void updateStock(@Param("productId") Long productId, @Param("change") int change);
}
