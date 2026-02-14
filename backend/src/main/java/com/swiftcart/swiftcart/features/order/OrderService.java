package com.swiftcart.swiftcart.features.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.razorpay.RazorpayException;
import com.swiftcart.swiftcart.features.appuser.AppUser;

public interface OrderService {

    public OrderResponse createOrder(PlaceOrderRequest placeOrderRequest, Long userId) throws RazorpayException;
    public OrderResponse getOrder(Long orderId, AppUser user);
    public Page<OrderResponse> getOrdersForAuthenticatedUser(Long userId, Pageable pageable);
    public OrderItemResponse updateOrderItemStatus(AppUser user, Long orderId, OrderStatus orderStatus);
    public OrderResponse placeBuyNowOrder(PlaceBuyNowOrderRequest placeBuyNowOrderRequest, AppUser user) throws RazorpayException;
    public Page<OrderResponseForSeller> getAllOrders(Pageable pageable);
    public OrderResponse cancelOrder(Long orderId);
    public OrderItemResponse cancelOrderItem(Long userId, Long orderItemId);

}
