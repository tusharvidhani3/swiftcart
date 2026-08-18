package com.swiftcart.swiftcart.features.order;

import java.time.LocalDate;

public record DailyOrderStats(
    LocalDate date,
    Long revenue,
    Long orders,
    Long orderItems
) {}