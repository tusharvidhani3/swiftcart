package com.swiftcart.swiftcart.features.dashboard;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.swiftcart.swiftcart.features.appuser.AppUserService;
import com.swiftcart.swiftcart.features.appuser.CustomerStats;
import com.swiftcart.swiftcart.features.order.OrderService;
import com.swiftcart.swiftcart.features.order.OrderStats;
import com.swiftcart.swiftcart.features.product.ProductService;
import com.swiftcart.swiftcart.features.product.ProductStats;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderService orderService;

    private final ProductService productService;

    private final AppUserService userService;

    public DashboardResponse getDashboard(TimeSpan span) {
        LocalDate startDate = LocalDate.now();
        if(span == TimeSpan.WEEK)
            startDate= startDate.minusWeeks(1);
        else
            startDate = startDate.minusMonths(1);
        OrderStats orderStats = orderService.getOrderStats(startDate);
        ProductStats productStats = productService.getProductStats();
        // CustomerStats customerStats = userService.getCustomerStats();
        DashboardResponse dashboardResponse = new DashboardResponse(orderStats.confirmedOrderItems(), orderStats.shippedOrderItems(), orderStats.deliveredOrderItems(), orderStats.returnedOrderItems(), orderStats.revenueToday(), orderService.getDailyOrderStats(startDate), productStats.productsOutOfStock());
        return dashboardResponse;
    }
    
}
