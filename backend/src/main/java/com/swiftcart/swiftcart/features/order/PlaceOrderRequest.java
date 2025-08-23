package com.swiftcart.swiftcart.features.order;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlaceOrderRequest {

    private Boolean prepaid;
    private Long shippingAddressId;
}
