package com.swiftcart.swiftcart.features.order;

import java.time.LocalDateTime;

import com.swiftcart.swiftcart.features.address.AddressSnapshot;
import com.swiftcart.swiftcart.features.appuser.AppUser;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Setter
    private AppUser user;

    @Setter
    @Embedded
    private AddressSnapshot shippingAddress;

    @Setter
    private long totalAmount;

    @Setter
    private LocalDateTime placedAt;

}
