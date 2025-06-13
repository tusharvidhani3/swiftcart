package com.swiftcart.swiftcart.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.swiftcart.swiftcart.entity.OrderStatus;
import com.swiftcart.swiftcart.entity.User;
import com.swiftcart.swiftcart.payload.OrderItemResponse;
import com.swiftcart.swiftcart.payload.OrderResponse;
import com.swiftcart.swiftcart.payload.PlaceOrderRequest;

public interface OrderService {

    OrderResponse placeOrder(PlaceOrderRequest placeOrderRequest, Long userId);
    OrderResponse getOrder(Long orderId, User user);
    Page<OrderResponse> getOrdersForLoggedInCustomer(Long userId, Pageable pageable);
    Page<OrderResponse> getOrdersForLoggedInSeller(Long userId, Pageable pageable);
    OrderResponse updateOrderStatus(User user, Long orderId, OrderStatus orderStatus);
    OrderResponse placeBuyNowOrder(Long cartItemId, Long addressId, User user);
    Page<OrderResponse> getAllOrders(Pageable pageable);
    OrderResponse cancelOrder(Long userId, Long orderId);
    Page<OrderItemResponse> getOrderItemsForLoggedInCustomer(Long userId, Pageable pageable);
}
