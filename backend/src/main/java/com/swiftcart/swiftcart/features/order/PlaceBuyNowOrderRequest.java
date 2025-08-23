package com.swiftcart.swiftcart.features.order;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlaceBuyNowOrderRequest {

    private Long cartItemId, shippingAddressId;
    private Boolean prepaid;
}
