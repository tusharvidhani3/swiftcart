package com.swiftcart.swiftcart.features.order;

public interface OrderStatsProjection {
    long getConfirmedOrderItems();
    long getShippedOrderItems();
    long getDeliveredOrderItems();
    long getReturnedOrderItems();
    long getRevenueToday();
}
