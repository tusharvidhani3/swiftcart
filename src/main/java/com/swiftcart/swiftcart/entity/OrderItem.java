package com.swiftcart.swiftcart.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    @Setter
    @ManyToOne
    private SellerProduct sellerProduct;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @Setter
    private Order order;

    @Setter
    private int quantity;

    @Setter
    private LocalDateTime deliveryAt;

    @Setter
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

}
