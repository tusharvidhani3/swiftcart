package com.swiftcart.swiftcart.features.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.razorpay.RazorpayException;
import com.swiftcart.swiftcart.features.appuser.AppUser;

public interface OrderService {
    OrderResponse createOrder(PlaceOrderRequest placeOrderRequest, Long userId) throws RazorpayException;
    OrderResponse getOrder(Long orderId, AppUser user);
    Page<OrderResponse> getOrdersForAuthenticatedUser(Long userId, Pageable pageable);
    OrderItemResponse updateOrderItemStatus(AppUser user, Long orderId, OrderStatus orderStatus);
    OrderResponse placeBuyNowOrder(PlaceBuyNowOrderRequest placeBuyNowOrderRequest, AppUser user) throws RazorpayException;
    Page<OrderResponseForSeller> getAllOrders(Pageable pageable);
    OrderResponse cancelOrder(Long orderId);
    OrderItemResponse cancelOrderItem(Long userId, Long orderItemId);
}
