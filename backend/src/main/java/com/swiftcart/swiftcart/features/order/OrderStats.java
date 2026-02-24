package com.swiftcart.swiftcart.features.order;

import java.util.List;

public record OrderStats(
    long confirmedOrderItems,
    long shippedOrderItems,
    long deliveredOrderItems,
    long returnedOrderItems,
    long revenueToday,
    List<DailyOrderStats> dailyOrderStats
) {}