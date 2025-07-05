package com.swiftcart.swiftcart.payload;

import com.swiftcart.swiftcart.entity.PaymentMethod;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentDTO {

    private Long paymentId;
    private PaymentMethod paymentMethod;
}
