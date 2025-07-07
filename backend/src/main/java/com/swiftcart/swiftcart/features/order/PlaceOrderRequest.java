package com.swiftcart.swiftcart.features.order;

import com.swiftcart.swiftcart.features.payment.PaymentMethod;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlaceOrderRequest {

    private PaymentMethod paymentMethod;
    private Long shippingAddressId;
}
