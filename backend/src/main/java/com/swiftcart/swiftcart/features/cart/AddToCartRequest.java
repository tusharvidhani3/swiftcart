package com.swiftcart.swiftcart.features.cart;

public record AddToCartRequest(Long productId, int quantity) {
    public AddToCartRequest {
        if(quantity == 0)
            quantity = 1;
    }
}