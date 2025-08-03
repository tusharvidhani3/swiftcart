package com.swiftcart.swiftcart.features.order;

import java.time.LocalDateTime;

import com.swiftcart.swiftcart.features.product.Product;

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
    private Product product;

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
    private OrderStatus orderItemStatus;

    @Setter
    private boolean active;

}
