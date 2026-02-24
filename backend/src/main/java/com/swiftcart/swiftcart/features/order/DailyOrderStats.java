package com.swiftcart.swiftcart.features.order;

import java.time.LocalDate;

public record DailyOrderStats(
    LocalDate date,
    long revenue,
    long orderItems,
    long orders
) {}