package com.swiftcart.swiftcart.features.cart;

import com.swiftcart.swiftcart.features.product.ProductResponse;

public record CartItemResponse(
    Long id,
    ProductResponse product,
    int quantity
) {}
