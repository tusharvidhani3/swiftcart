package com.swiftcart.swiftcart.entity;

import java.util.List;

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
    private List<String> imageUrls;
    @Setter
    private String category;
    @Setter
    private String description;

}
