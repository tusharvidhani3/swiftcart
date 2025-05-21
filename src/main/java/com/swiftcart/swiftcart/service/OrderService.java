package com.swiftcart.swiftcart.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.swiftcart.swiftcart.entity.OrderStatus;
import com.swiftcart.swiftcart.entity.User;
import com.swiftcart.swiftcart.payload.OrderDTO;
import com.swiftcart.swiftcart.payload.PlaceOrderRequest;

public interface OrderService {

    OrderDTO placeOrder(PlaceOrderRequest placeOrderRequest);
    OrderDTO getOrder(Long orderId, Long userId);
    Page<OrderDTO> getLoggedInUserOrders(Long userId, Pageable pageable);
    OrderDTO updateOrderStatus(Long orderId, OrderStatus orderStatus);
    OrderDTO cancelOrder(Long userId,Long orderId);
    OrderDTO placeBuyNowOrder(Long cartItemId, Long addressId, User user);
}
