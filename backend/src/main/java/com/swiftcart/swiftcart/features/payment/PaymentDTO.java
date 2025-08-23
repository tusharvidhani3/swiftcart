package com.swiftcart.swiftcart.features.payment;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentDTO {

    private String paymentOrderId;
    private String paymentId;
    private PaymentStatus paymentStatus;
}
