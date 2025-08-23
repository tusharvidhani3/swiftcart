package com.swiftcart.swiftcart.features.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.razorpay.RazorpayException;
import com.swiftcart.swiftcart.features.user.User;

public interface OrderService {

    public OrderResponse createOrder(PlaceOrderRequest placeOrderRequest, Long userId) throws RazorpayException;
    public OrderResponse getOrder(Long orderId, User user);
    public Page<OrderResponse> getOrdersForAuthenticatedUser(Long userId, Pageable pageable);
    public OrderItemResponse updateOrderItemStatus(User user, Long orderId, OrderStatus orderStatus);
    public OrderResponse placeBuyNowOrder(PlaceBuyNowOrderRequest placeBuyNowOrderRequest, User user) throws RazorpayException;
    public Page<OrderResponseForSeller> getAllOrders(Pageable pageable);
    public OrderResponse cancelOrder(Long orderId);
    public OrderItemResponse cancelOrderItem(Long userId, Long orderItemId);

}
