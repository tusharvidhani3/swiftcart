package com.swiftcart.swiftcart.entity;

import java.time.LocalDateTime;

import com.swiftcart.swiftcart.payload.ProductSnapshot;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
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

    @Embedded
    @Setter
    private ProductSnapshot product;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @Setter
    private Order order;

    @Setter
    private int quantity;

    @Setter
    private LocalDateTime deliveredAt;

}
