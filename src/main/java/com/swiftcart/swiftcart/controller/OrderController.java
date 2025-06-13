package com.swiftcart.swiftcart.controller;

import org.springframework.web.bind.annotation.RestController;

import com.swiftcart.swiftcart.entity.OrderStatus;
import com.swiftcart.swiftcart.payload.OrderItemResponse;
import com.swiftcart.swiftcart.payload.OrderResponse;
import com.swiftcart.swiftcart.payload.PlaceOrderRequest;
import com.swiftcart.swiftcart.payload.UpdateOrderStatusRequest;
import com.swiftcart.swiftcart.security.UserDetailsImpl;
import com.swiftcart.swiftcart.service.OrderService;

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
    OrderService orderService;

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
        Page<OrderResponse> orders=orderService.getOrdersForLoggedInCustomer(userDetailsImpl.getUser().getUserId(), pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/items")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<OrderItemResponse>> getLoggedInCustomerOrderItems(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "order.placedAt") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<OrderItemResponse> orderItems = orderService.getOrderItemsForLoggedInCustomer(userDetailsImpl.getUser().getUserId(), pageable);
        return ResponseEntity.ok(orderItems);
    }

    @GetMapping("/seller")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Page<OrderResponse>> getLoggedInSellerOrders(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "placedAt") String sortBy) {
        Pageable pageable=PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<OrderResponse> orders=orderService.getOrdersForLoggedInSeller(userDetailsImpl.getUser().getUserId(), pageable);
        return ResponseEntity.ok(orders);
    }
    
    @PatchMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> cancelOrder(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @PathVariable Long orderId) {
        OrderResponse orderResponse=orderService.updateOrderStatus(userDetailsImpl.getUser(), orderId, OrderStatus.CANCELLED);
        return ResponseEntity.ok(orderResponse);
    }

    @PatchMapping("/{orderId}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<OrderResponse> updateOrderStatus(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @RequestBody UpdateOrderStatusRequest req) {
        OrderResponse orderResponse = orderService.updateOrderStatus(userDetailsImpl.getUser(), req.getOrderId(), req.getOrderStatus());
        return ResponseEntity.ok(orderResponse);
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        OrderResponse orderResponse=orderService.getOrder(orderId, userDetailsImpl.getUser());
        return new ResponseEntity<OrderResponse>(orderResponse, HttpStatus.OK);
    }

    @PostMapping("/checkout/buy-now/cartitem/{cartItemId}/address/{addressId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> placeBuyNowOrder(@PathVariable Long cartItemId, @PathVariable Long addressId, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        OrderResponse orderResponse = orderService.placeBuyNowOrder(cartItemId, addressId, userDetailsImpl.getUser());
        return ResponseEntity.created(URI.create("/orders/"+orderResponse.getOrderId())).body(orderResponse);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderResponse>> getAllOrders(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "placedAt") String sortBy) {
        Page<OrderResponse> orders= orderService.getAllOrders(PageRequest.of(page, size, Sort.by(sortBy).descending()));
        return ResponseEntity.ok(orders);
    }

}
