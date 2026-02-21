package com.swiftcart.swiftcart.features.product;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
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

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    private List<ProductImage> images;

    // private int minOrderQty;

    // @Setter
    // private LocalDate listedAt;
}
