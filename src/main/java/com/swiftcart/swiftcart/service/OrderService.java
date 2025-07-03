package com.swiftcart.swiftcart.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.swiftcart.swiftcart.entity.OrderStatus;
import com.swiftcart.swiftcart.entity.User;
import com.swiftcart.swiftcart.payload.OrderItemResponse;
import com.swiftcart.swiftcart.payload.OrderResponse;
import com.swiftcart.swiftcart.payload.OrderResponseForSeller;
import com.swiftcart.swiftcart.payload.PlaceOrderRequest;

public interface OrderService {

    public OrderResponse placeOrder(PlaceOrderRequest placeOrderRequest, Long userId);
    public OrderResponse getOrder(Long orderId, User user);
    public Page<OrderResponse> getOrdersForAuthenticatedUser(Long userId, Pageable pageable);
    public OrderItemResponse updateOrderItemStatus(User user, Long orderId, OrderStatus orderStatus);
    public OrderResponse placeBuyNowOrder(Long cartItemId, Long addressId, User user);
    public Page<OrderResponseForSeller> getAllOrders(Pageable pageable);
    public OrderResponse cancelOrder(Long orderId);
    public OrderItemResponse cancelOrderItem(Long userId, Long orderItemId);

}
