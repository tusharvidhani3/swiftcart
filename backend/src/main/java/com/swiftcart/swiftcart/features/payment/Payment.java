package com.swiftcart.swiftcart.features.payment;

import com.swiftcart.swiftcart.features.order.Order;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue
    private Long id;

    private String paymentOrderId;

    private String paymentId;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private PaymentStatus paymentStatus;
}
