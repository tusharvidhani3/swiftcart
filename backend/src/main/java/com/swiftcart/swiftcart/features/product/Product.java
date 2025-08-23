package com.swiftcart.swiftcart.features.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    @Setter
    private String productName;
    @Setter
    private double mrp;
    @Setter
    private double price;
    @Setter
    private String category;
    @Setter
    @Column(columnDefinition = "TEXT")
    private String description;
    @Setter
    private Integer stock;

    // private int minOrderQty;

    // @Setter
    // private LocalDate listedAt;
}
