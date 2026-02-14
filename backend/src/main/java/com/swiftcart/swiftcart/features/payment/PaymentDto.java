package com.swiftcart.swiftcart.features.payment;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentDto {

    private String paymentOrderId;
    private String paymentId;
    private PaymentStatus paymentStatus;
}
