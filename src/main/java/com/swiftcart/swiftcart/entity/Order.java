package com.swiftcart.swiftcart.entity;

import java.time.LocalDateTime;

import com.swiftcart.swiftcart.payload.AddressSnapshot;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @Setter
    private Customer customer;

    @Setter
    @Embedded
    private AddressSnapshot shippingAddress;

    @Setter
    private double totalAmount;

    @Setter
    private LocalDateTime placedAt;

    @Setter
    @OneToOne
    private Payment payment;
}
