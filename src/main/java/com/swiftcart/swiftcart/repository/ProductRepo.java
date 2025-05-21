package com.swiftcart.swiftcart.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.swiftcart.swiftcart.entity.Product;
import com.swiftcart.swiftcart.payload.ProductDTO;

@Repository
public interface ProductRepo extends JpaRepository<Product,Integer> {

    public Product findByProductId(Long productId);
    public void deleteByProductId(Long productId);
    
    @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    public Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);
    public Page<ProductDTO> findByCategory(String category, Pageable pageable);
}
