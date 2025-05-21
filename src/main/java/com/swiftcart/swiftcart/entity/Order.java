package com.swiftcart.swiftcart.entity;

import java.time.LocalDate;

import com.swiftcart.swiftcart.payload.AddressSnapshot;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    @JoinColumn(name = "user_id")
    @Setter
    private User user;

    @Setter
    @Embedded
    private AddressSnapshot shippingAddress;

    @Setter
    private Double totalAmount;

    @Setter
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Setter
    private LocalDate orderDate;
}
