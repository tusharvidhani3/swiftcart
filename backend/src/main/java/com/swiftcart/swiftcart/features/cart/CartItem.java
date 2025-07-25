package com.swiftcart.swiftcart.features.cart;

import com.swiftcart.swiftcart.features.product.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;

    @ManyToOne
    @Setter
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @Setter
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @Setter
    private int quantity;

}
