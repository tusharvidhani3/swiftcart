package com.swiftcart.swiftcart.features.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepo extends JpaRepository<ProductImage, Long> {

    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.productId = :productId ORDER BY pi.sortOrder")
    public List<ProductImage> findAllByProduct_ProductId(@Param("productId") Long productId);

    public void deleteAllByProduct_ProductId(Long productId);
}
