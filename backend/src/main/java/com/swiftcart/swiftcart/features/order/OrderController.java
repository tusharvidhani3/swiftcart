package com.swiftcart.swiftcart.features.order;

import org.springframework.web.bind.annotation.RestController;

import com.swiftcart.swiftcart.common.security.UserDetailsImpl;

import jakarta.validation.Valid;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/checkout")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody @Valid PlaceOrderRequest placeOrderRequest, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        OrderResponse orderResponse=orderService.placeOrder(placeOrderRequest, userDetailsImpl.getUser().getUserId());
        return ResponseEntity.created(URI.create("/orders/"+orderResponse.getOrderId())).body(orderResponse);
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<OrderResponse>> getLoggedInCustomerOrders(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "placedAt") String sortBy) {
        Pageable pageable=PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<OrderResponse> orders=orderService.getOrdersForAuthenticatedUser(userDetailsImpl.getUser().getUserId(), pageable);
        return ResponseEntity.ok(orders);
    }
    
    @PatchMapping("/items/{orderItemId}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderItemResponse> cancelOrderItem(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @PathVariable Long orderId) {
        OrderItemResponse orderItemResponse=orderService.cancelOrderItem(userDetailsImpl.getUser().getUserId(), orderId);
        return ResponseEntity.ok(orderItemResponse);
    }

    @PatchMapping("/items/{orderItemId}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<OrderItemResponse> updateOrderItemStatus(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @RequestBody UpdateOrderStatusRequest req) {
        OrderItemResponse orderItemResponse = orderService.updateOrderItemStatus(userDetailsImpl.getUser(), req.getOrderId(), req.getOrderStatus());
        return ResponseEntity.ok(orderItemResponse);
    }
    
    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','SELLER','ADMIN')")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        OrderResponse orderResponse = orderService.getOrder(orderId, userDetailsImpl.getUser());
        return new ResponseEntity<OrderResponse>(orderResponse, HttpStatus.OK);
    }

    @PostMapping("/checkout/buy-now")
    @PreAuthorize("hasRole('CUSTOMER')") // Can allow Admin also to place order on user's behalf
    public ResponseEntity<OrderResponse> placeBuyNowOrder(@RequestBody PlaceBuyNowOrderRequest placeBuyNowOrderRequest, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        OrderResponse orderResponse = orderService.placeBuyNowOrder(placeBuyNowOrderRequest, userDetailsImpl.getUser());
        return ResponseEntity.created(URI.create("/orders/"+orderResponse.getOrderId())).body(orderResponse);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    public ResponseEntity<Page<OrderResponseForSeller>> getAllOrders(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "placedAt") String sortBy) {
        Page<OrderResponseForSeller> orders = orderService.getAllOrders(PageRequest.of(page, size, Sort.by(sortBy).descending()));
        return ResponseEntity.ok(orders);
    }

    @PatchMapping("/{orderId}/cancel-full")
    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long orderId) {
        OrderResponse orderResponse = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(orderResponse);
    }

}
