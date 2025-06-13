package com.swiftcart.swiftcart.payload;

import java.time.LocalDateTime;
import java.util.List;

import com.swiftcart.swiftcart.entity.OrderStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderResponse {

    private Long orderId;
    private AddressSnapshot shippingAddress;
    private double totalAmount;
    private OrderStatus orderStatus;
    private LocalDateTime placedAt;
    private List<OrderItemResponse> orderItems;
    private PaymentDTO payment;
}
