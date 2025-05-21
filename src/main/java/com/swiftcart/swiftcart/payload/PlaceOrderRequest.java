package com.swiftcart.swiftcart.payload;

import com.swiftcart.swiftcart.entity.PaymentMethod;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlaceOrderRequest {

    private Long cartId;
    private PaymentMethod paymentMethod;
    private Long shippingAddressId;
}
