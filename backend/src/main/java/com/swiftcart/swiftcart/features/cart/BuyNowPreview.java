package com.swiftcart.swiftcart.features.cart;

import com.swiftcart.swiftcart.features.address.AddressDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BuyNowPreview {

    private CartItemDTO cartItemDTO;
    private AddressDTO defaultAddress;
}
