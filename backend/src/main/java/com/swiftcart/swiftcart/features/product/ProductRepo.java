package com.swiftcart.swiftcart.features.product;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends JpaRepository<Product,Integer> {

    public Optional<Product> findByProductId(Long productId);

    @Modifying
    public void deleteByProductId(Long productId);
    
    @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) AND p.price<=:maxPrice AND p.price>=:minPrice AND (:category = '' OR p.category=:category) AND (:inStock = FALSE OR p.stock > 0)")
    public Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable, @Param("minPrice") long minPrice, @Param("maxPrice") long maxPrice, @Param("category") String category, @Param("inStock") boolean inStock);

}
