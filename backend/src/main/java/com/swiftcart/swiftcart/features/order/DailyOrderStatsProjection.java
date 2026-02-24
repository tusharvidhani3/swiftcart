package com.swiftcart.swiftcart.features.order;

import java.time.LocalDate;

public interface DailyOrderStatsProjection {
    LocalDate getDate();
    Long getRevenue();
    Long getTotalOrders();
    Long getTotalItems();
}
