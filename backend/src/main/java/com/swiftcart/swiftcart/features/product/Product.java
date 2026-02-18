package com.swiftcart.swiftcart.features.product;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    private String name;
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

    @OneToMany(mappedBy = "product")
    private List<ProductImage> images;

    // private int minOrderQty;

    // @Setter
    // private LocalDate listedAt;
}
