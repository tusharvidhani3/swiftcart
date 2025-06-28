package com.swiftcart.swiftcart.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

@Entity
@Getter
@Table(
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"seller_id", "product_id"})
    }
)
public class SellerProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sellerProductId;

    @ManyToOne
    private Product product;

    @ManyToOne
    private Seller seller;

    private double price;

    private int stock;

    private boolean isActive;

    // private String sellerSku;
}
