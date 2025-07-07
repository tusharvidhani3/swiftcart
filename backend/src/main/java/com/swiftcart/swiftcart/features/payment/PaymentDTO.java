package com.swiftcart.swiftcart.features.payment;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentDTO {

    private Long paymentId;
    private PaymentMethod paymentMethod;
}
