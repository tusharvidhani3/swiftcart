package com.swiftcart.swiftcart.features.order;

public record OrderStats(
    Long confirmedOrderItems,
    Long shippedOrderItems,
    Long deliveredOrderItems,
    Long returnedOrderItems,
    Long revenueToday
) {}