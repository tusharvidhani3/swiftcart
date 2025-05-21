package com.swiftcart.swiftcart.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BuyNowPreview {

    private CartItemDTO cartItemDTO;
    private AddressDTO defaultAddress;
}
