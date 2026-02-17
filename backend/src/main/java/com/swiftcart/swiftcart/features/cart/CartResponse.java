package com.swiftcart.swiftcart.features.cart;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartResponse {

    private Long id;
    private double totalPrice;
    private List<CartItemResponse> cartItems;
}
