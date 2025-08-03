package com.swiftcart.swiftcart.features.order;

import java.time.LocalDateTime;

import com.swiftcart.swiftcart.features.address.AddressSnapshot;
import com.swiftcart.swiftcart.features.payment.Payment;
import com.swiftcart.swiftcart.features.user.User;

import jakarta.persistence.CascadeType;
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
    @JoinColumn(name = "user_id")
    @Setter
    private User user;

    @Setter
    @Embedded
    private AddressSnapshot shippingAddress;

    @Setter
    private double totalAmount;

    @Setter
    private LocalDateTime placedAt;

    @Setter
    @OneToOne(cascade = CascadeType.ALL)
    private Payment payment;
}
