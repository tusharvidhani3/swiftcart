package com.swiftcart.swiftcart.features.cart;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartResponse {

    private Long cartId;
    private double totalPrice;
    private List<CartItemDTO> cartItems;
}
