package com.swiftcart.swiftcart.features.order;

import java.time.LocalDateTime;
import java.util.List;

import com.swiftcart.swiftcart.features.address.AddressSnapshot;
import com.swiftcart.swiftcart.features.payment.PaymentDto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderResponseForSeller {

    private Long id;
    private LocalDateTime placedAt;
    private double totalAmount;
    private PaymentDto payment;
    private String buyerName;
    private AddressSnapshot address;
    private List<OrderItemResponse> orderItems;

}
