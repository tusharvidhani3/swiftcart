package com.swiftcart.swiftcart.features.order;

import java.time.LocalDateTime;
import java.util.List;

import com.swiftcart.swiftcart.features.address.AddressSnapshot;
import com.swiftcart.swiftcart.features.payment.PaymentDto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderResponse {

    private Long id;
    private AddressSnapshot shippingAddress;
    private double totalAmount;
    private LocalDateTime placedAt;
    private List<OrderItemResponse> orderItems;
    private PaymentDto payment;
}
