package com.swiftcart.swiftcart.features.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    Optional<OrderItem> findById(Long id);

    List<OrderItem> findByOrderId(Long orderId);

    @Query("""
                SELECT new com.swiftcart.swiftcart.features.order.OrderStats(
                    SUM(CASE WHEN oi.orderItemStatus = 'CONFIRMED' THEN oi.quantity ELSE 0 END) AS confirmedOrderItems,
                    SUM(CASE WHEN oi.orderItemStatus = 'SHIPPED' THEN oi.quantity ELSE 0 END) AS shippedOrderItems,
                    SUM(CASE WHEN oi.orderItemStatus = 'DELIVERED' THEN oi.quantity ELSE 0 END) AS deliveredOrderItems,
                    SUM(CASE WHEN oi.orderItemStatus = 'RETURNED' THEN oi.quantity ELSE 0 END) AS returnedOrderItems,
                    SUM(CASE WHEN oi.order.placedAt = CURRENT_DATE THEN oi.product.price * oi.quantity ELSE 0 END) AS revenueToday
                )
                FROM OrderItem oi
            """)
    OrderStats getOrderStats();

    @Query("""
                SELECT new com.swiftcart.swiftcart.features.order.DailyOrderStats(
                    CAST(oi.order.placedAt AS LocalDate),
                    SUM(oi.product.price * oi.quantity),
                    COUNT(DISTINCT oi.order.id),
                    SUM(oi.quantity)
                )
                FROM OrderItem oi
                WHERE oi.order.placedAt >= :start
                GROUP BY CAST(oi.order.placedAt AS date)
                ORDER BY CAST(oi.order.placedAt AS date)
            """)
    List<DailyOrderStats> getDailyOrderStats(@Param("start") LocalDateTime start);
}
