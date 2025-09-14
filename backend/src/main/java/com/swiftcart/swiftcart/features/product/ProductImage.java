package com.swiftcart.swiftcart.features.product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = { "product_id", "sort_order" })
})
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String imageUrl;

    @Setter
    private int sortOrder;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
