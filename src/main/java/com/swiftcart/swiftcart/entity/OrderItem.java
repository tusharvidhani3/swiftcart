package com.swiftcart.swiftcart.entity;

// import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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

    @OneToOne
    @JoinColumn(name = "product_id")
    @Setter
    private Product product;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @Setter
    private Order order;

    @Setter
    private int quantity;

    // @Setter
    // private LocalDate deliveredAt;

    public OrderItem(CartItem cartItem, Order order) {
        product = cartItem.getProduct();
        quantity = cartItem.getQuantity();
        this.order = order;
    }
}
