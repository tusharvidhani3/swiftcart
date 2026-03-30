package com.swiftcart.swiftcart.features.order;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ShippingService {
    
    private final long flatRate;

    private final long freeThreshold;

    public ShippingService(@Value("${app.shipping.flat-rate}") long flatRate, @Value("${app.shipping.free-threshold}") long freeThreshold) {
        this.flatRate = flatRate * 100; // conversion from rupees to paise
        this.freeThreshold = flatRate * 100;
    }

    public long calculate(long subtotal) {
        return subtotal < freeThreshold ? flatRate : 0;
    }

}
