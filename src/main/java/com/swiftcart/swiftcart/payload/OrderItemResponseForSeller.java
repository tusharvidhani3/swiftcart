package com.swiftcart.swiftcart.payload;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderItemResponseForSeller {

    private Long orderItemId;
    private Long orderId;
    private LocalDateTime placedAt;
    private LocalDateTime deliveryAt;
    private SellerProductResponse sellerProduct;
    private int quantity;

    private PaymentDTO payment;
    private String buyerName;
    private LocalDateTime orderDate;
    private AddressDTO address;

}
