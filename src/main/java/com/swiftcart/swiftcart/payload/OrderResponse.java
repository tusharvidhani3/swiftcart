package com.swiftcart.swiftcart.payload;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderResponse {

    private Long orderId;
    private AddressSnapshot shippingAddress;
    private double totalAmount;
    private LocalDateTime placedAt;
    private List<OrderItemResponse> orderItems;
    private PaymentDTO payment;
}
