package com.swiftcart.swiftcart.features.order;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUserId(Long userId, Pageable pageable);

    Optional<Order> findById(Long id);

    @Query("""
        SELECT
            DATE(o.placedAt) AS date,
            SUM(o.totalAmount) AS revenue,
            COUNT(o.id) AS totalOrders
        FROM Order o
        WHERE o.placedAt >= :startDate
        GROUP BY DATE(o.placedAt)
        ORDER BY DATE(o.placedAt)
    """)
    List<DailyOrderStatsProjection> getDailyOrderStats(@Param("startDate") LocalDate startDate);
}
