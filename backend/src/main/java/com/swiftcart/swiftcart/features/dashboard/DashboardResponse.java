package com.swiftcart.swiftcart.features.dashboard;

import java.util.List;

import com.swiftcart.swiftcart.features.order.DailyOrderStats;

public record DashboardResponse(
    long confirmedOrderItems,
    long shippedOrderItems,
    long deliveredOrderItems,
    long returnedOrderItems,
    long revenueToday,
    List<DailyOrderStats> dailyOrderStats,
    long productsOutOfStock
) {}