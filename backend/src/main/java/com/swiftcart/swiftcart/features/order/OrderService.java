package com.swiftcart.swiftcart.features.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.swiftcart.swiftcart.features.user.User;

public interface OrderService {

    public OrderResponse placeOrder(PlaceOrderRequest placeOrderRequest, Long userId);
    public OrderResponse getOrder(Long orderId, User user);
    public Page<OrderResponse> getOrdersForAuthenticatedUser(Long userId, Pageable pageable);
    public OrderItemResponse updateOrderItemStatus(User user, Long orderId, OrderStatus orderStatus);
    public OrderResponse placeBuyNowOrder(PlaceBuyNowOrderRequest placeBuyNowOrderRequest, User user);
    public Page<OrderResponseForSeller> getAllOrders(Pageable pageable);
    public OrderResponse cancelOrder(Long orderId);
    public OrderItemResponse cancelOrderItem(Long userId, Long orderItemId);

}
