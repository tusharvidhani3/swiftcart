package com.swiftcart.swiftcart.controller;

import org.springframework.web.bind.annotation.RestController;

import com.swiftcart.swiftcart.payload.OrderDTO;
import com.swiftcart.swiftcart.payload.PlaceOrderRequest;
import com.swiftcart.swiftcart.security.entity.UserDetailsImpl;
import com.swiftcart.swiftcart.service.OrderService;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderDTO> placeOrder(@RequestBody PlaceOrderRequest placeOrderRequest) {
        OrderDTO orderDTO=orderService.placeOrder(placeOrderRequest);
        return ResponseEntity.created(URI.create("/orders/"+orderDTO.getOrderId())).body(orderDTO);
    }

    @GetMapping
    public ResponseEntity<Page<OrderDTO>> getLoggedInUserOrders(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "productId") String sortBy) {
        Pageable pageable=PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<OrderDTO> orders=orderService.getLoggedInUserOrders(userDetailsImpl.getUser().getUserId(), pageable);
        return ResponseEntity.ok(orders);
    }
    
    @PutMapping("/{orderId}")
    public ResponseEntity<OrderDTO> cancelOrder(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @PathVariable Long orderId) {
        OrderDTO orderDTO=orderService.cancelOrder(userDetailsImpl.getUser().getUserId(),orderId);
        return ResponseEntity.ok(orderDTO);
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long orderId, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        OrderDTO orderDTO=orderService.getOrder(orderId, userDetailsImpl.getUser().getUserId());
        return new ResponseEntity<OrderDTO>(orderDTO, HttpStatus.OK);
    }

    @PostMapping("/checkout/buy-now/cartitem/{cartItemId}/address/{addressId}")
    public ResponseEntity<OrderDTO> placeBuyNowOrder(@PathVariable Long cartItemId, @PathVariable Long addressId, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        OrderDTO orderDTO = orderService.placeBuyNowOrder(cartItemId, addressId, userDetailsImpl.getUser());
        return ResponseEntity.created(URI.create("/orders/"+orderDTO.getOrderId())).body(orderDTO);
    }

}
