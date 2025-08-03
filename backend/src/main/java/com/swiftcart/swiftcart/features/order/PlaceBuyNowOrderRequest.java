package com.swiftcart.swiftcart.features.order;

import com.swiftcart.swiftcart.features.payment.PaymentMethod;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlaceBuyNowOrderRequest {

    private Long cartItemId, shippingAddressId;
    private PaymentMethod paymentMethod;
}
