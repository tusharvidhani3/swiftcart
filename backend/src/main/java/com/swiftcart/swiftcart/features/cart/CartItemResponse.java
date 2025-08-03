package com.swiftcart.swiftcart.features.cart;

import com.swiftcart.swiftcart.features.product.ProductResponse;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartItemResponse {

    private Long cartItemId;
    private ProductResponse product;
    private int quantity;
}