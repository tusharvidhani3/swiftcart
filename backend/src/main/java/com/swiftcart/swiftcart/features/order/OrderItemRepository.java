package com.swiftcart.swiftcart.features.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    Optional<OrderItem> findById(Long id);
    List<OrderItem> findByOrderId(Long orderId);

    @Query("""
                SELECT
                    SUM(CASE WHEN oi.orderItemStatus = 'CONFIRMED' THEN oi.quantity ELSE 0 END) AS confirmedOrderItems,
                    SUM(CASE WHEN oi.orderItemStatus = 'SHIPPED' THEN oi.quantity ELSE 0 END) AS shippedOrderItems,
                    SUM(CASE WHEN oi.orderItemStatus = 'DELIVERED' THEN oi.quantity ELSE 0 END) AS deliveredOrderItems,
                    SUM(CASE WHEN oi.orderItemStatus = 'RETURNED' THEN oi.quantity ELSE 0 END) AS returnedOrderItems,
                    SUM(CASE WHEN FUNCTION(DATE, oi.order.placedAt) = CURRENT_DATE THEN oi.product.price * oi.quantity ELSE 0 END) AS revenueToday
                FROM OrderItem oi
            """)
    OrderStatsProjection getOrderStats();

    @Query("""
                SELECT
                    DATE(oi.order.placedAt) AS date,
                    SUM(oi.product.price * oi.quantity) AS revenue,
                    COUNT(DISTINCT oi.order.id) AS orders,
                    SUM(oi.quantity) AS orderItems
                FROM OrderItem oi
                WHERE oi.order.placedAt >= :startDate
                GROUP BY DATE(oi.order.placedAt)
                ORDER BY DATE(oi.order.placedAt)
            """)
    List<DailyOrderStatsProjection> getDailyOrderStats(@Param("startDate") LocalDateTime startDate);
}
