package com.swiftcart.swiftcart.features.order;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShippingService {
    
    private final ShippingProperties shippingProperties;

    public long calculate(long subtotal) {
        return subtotal < shippingProperties.freeThreshold() ? shippingProperties.flatRate() : 0;
    }

}
