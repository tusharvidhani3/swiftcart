package com.swiftcart.swiftcart.features.dashboard;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.swiftcart.swiftcart.features.appuser.AppUserService;
import com.swiftcart.swiftcart.features.order.OrderService;
import com.swiftcart.swiftcart.features.product.ProductService;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private AppUserService userService;

    @Override
    public DashboardResponse getDashboard(TimeSpan span) {
        DashboardResponse dashboardResponse = new DashboardResponse(0l, 0l, 0l, 0l, 0l, null, 0l);
        LocalDate startDate = LocalDate.now();
        if(span == TimeSpan.WEEK)
            startDate.minusWeeks(1);
        else
            startDate.minusMonths(1);
        // modelMapper.map(orderService.getOrderStats(startDate), dashboardResponse);
        // modelMapper.map(productService.getProductStats(), dashboardResponse);
        // modelMapper.map(userService.getCustomerStats(), dashboardResponse);
        return dashboardResponse;
    }
    
}
