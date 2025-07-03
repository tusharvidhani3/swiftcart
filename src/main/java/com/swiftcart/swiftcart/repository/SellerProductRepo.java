package com.swiftcart.swiftcart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swiftcart.swiftcart.entity.SellerProduct;

public interface SellerProductRepo extends JpaRepository<SellerProduct, Long> {

    public Optional<SellerProduct> findBySellerProductId(Long sellerProductId);
    public Long findUser_UserIdBySellerProductId(Long sellerProductId);
    public List<SellerProduct> findAllByProduct_ProductId(Long productId);

    // @Modifying
    // @Query("UPDATE SellerProduct sp SET sp.stock = sp.stock + :change WHERE sp.sellerProductId = :sellerProductId AND sp.stock + :change >= 0")
    // public void updateStock(@Param("sellerProductId") Long sellerProductId, @Param("change") int change);
}
