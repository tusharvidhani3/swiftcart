package com.swiftcart.swiftcart.features.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepo extends JpaRepository<ProductImage, Long> {

    public List<ProductImage> findAllByProduct_ProductId(Long productId);

    public void deleteAllByProduct_ProductId(Long productId);
}
