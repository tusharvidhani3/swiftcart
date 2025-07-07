package com.swiftcart.swiftcart.features.order;

import java.time.LocalDateTime;
import java.util.List;

import com.swiftcart.swiftcart.features.address.AddressSnapshot;
import com.swiftcart.swiftcart.features.payment.PaymentDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderResponseForSeller {

    private Long orderId;
    private LocalDateTime placedAt;
    private double totalAmount;
    private PaymentDTO payment;
    private String buyerName;
    private AddressSnapshot address;
    private List<OrderItemResponse> orderItems;

}
