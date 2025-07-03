package com.swiftcart.swiftcart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.swiftcart.swiftcart.entity.Seller;

@Repository
public interface SellerRepo extends JpaRepository<Seller, Long> {

    public Optional<Seller> findByUser_UserId(Long userId);
}
