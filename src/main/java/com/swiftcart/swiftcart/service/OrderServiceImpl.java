package com.swiftcart.swiftcart.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swiftcart.swiftcart.entity.Address;
import com.swiftcart.swiftcart.entity.Cart;
import com.swiftcart.swiftcart.entity.CartItem;
import com.swiftcart.swiftcart.entity.Order;
import com.swiftcart.swiftcart.entity.OrderItem;
import com.swiftcart.swiftcart.entity.OrderStatus;
import com.swiftcart.swiftcart.entity.User;
import com.swiftcart.swiftcart.exception.ResourceNotFoundException;
import com.swiftcart.swiftcart.payload.AddressSnapshot;
import com.swiftcart.swiftcart.payload.OrderResponse;
import com.swiftcart.swiftcart.payload.PlaceOrderRequest;
import com.swiftcart.swiftcart.payload.ProductSnapshot;
import com.swiftcart.swiftcart.repository.AddressRepo;
import com.swiftcart.swiftcart.repository.CartItemRepo;
import com.swiftcart.swiftcart.repository.CartRepo;
import com.swiftcart.swiftcart.repository.OrderItemRepo;
import com.swiftcart.swiftcart.repository.OrderRepo;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderItemRepo orderItemRepo;

    @Autowired
    OrderRepo orderRepo;

    @Autowired
    CartRepo cartRepo;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CartItemRepo cartItemRepo;

    @Autowired
    ProductService productService;

    @Autowired
    AddressRepo addressRepo;

    @Autowired
    CartService cartService;

    @Override
    @Transactional
    public OrderResponse placeOrder(PlaceOrderRequest placeOrderRequest, Long userId) {
        Cart cart = cartRepo.findByUser_UserId(userId)
        .orElseThrow(() -> new ResourceNotFoundException("Cart empty"));
        Address shippingAddress = addressRepo.findByAddressId(placeOrderRequest.getShippingAddressId())
        .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        if(!shippingAddress.getUser().getUserId().equals(userId))
        throw new AccessDeniedException("Unauthorized access to the address");
        Order order = new Order();
        order.setOrderStatus(OrderStatus.PROCESSING);
        order.setPlacedAt(LocalDateTime.now());
        order.setShippingAddress(modelMapper.map(shippingAddress, AddressSnapshot.class));
        order.setUser(cart.getUser());
        List<CartItem> cartItems = cartItemRepo.findAllByCart_CartId(cart.getCartId());
        List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0;
        for (CartItem ci : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(modelMapper.map(ci.getProduct(), ProductSnapshot.class));
            orderItem.setQuantity(ci.getQuantity());
            orderItems.add(orderItem);
            productService.updateStock(ci.getProduct().getProductId(), -orderItem.getQuantity());
            totalAmount += ci.getProduct().getPrice();
        }
        order.setTotalAmount(totalAmount);
        orderItemRepo.saveAll(orderItems);
        order = orderRepo.save(order);
        cartItemRepo.deleteAllByCart_CartId(cart.getCartId());
        return modelMapper.map(order, OrderResponse.class);
    }

    @Override
    public OrderResponse getOrder(Long orderId, User user) {
        // Allow admin but allow customer or seller to view only if it belongs to them i.e. ordered by that customer or in multi-seller system allow access to orderitems that belongs to the seller not the full order 
        Order order = orderRepo.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        boolean isAdminOrSeller = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN") || role.getName().equals("ROLE_SELLER"));
        if (!isAdminOrSeller && !order.getUser().getUserId().equals(user.getUserId())) // since this is currently single seller system, seller-orderItem.product relationship is not established and the seller is allowed to view all the orders without verification
            throw new AccessDeniedException("You are not authorized to access this order");
        return modelMapper.map(order, OrderResponse.class);
    }

    @Override
    public Page<OrderResponse> getOrdersForLoggedInCustomer(Long userId, Pageable pageable) {
        return orderRepo.findAllByUser_UserId(userId, pageable)
                .map(order -> modelMapper.map(order, OrderResponse.class));
    }

    @Override
    public Page<OrderResponse> getOrdersForLoggedInSeller(Long userId, Pageable pageable) {
        return getAllOrders(pageable) // since there is single seller all orders belong to him
                .map(order -> modelMapper.map(order, OrderResponse.class));
    }

    @Override
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepo.findAll(pageable)
                .map(order -> modelMapper.map(order, OrderResponse.class));
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(User user, Long orderId, OrderStatus orderStatus) {
        Order order = orderRepo.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

                boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
        if (!isAdmin && !order.getUser().getUserId().equals(user.getUserId()))
            throw new AccessDeniedException("You are not allowed to perform this action");
        if (orderStatus == OrderStatus.CANCELLED) {
            List<OrderItem> orderItems = orderItemRepo.findAllByOrder_OrderId(orderId);
            orderItems.stream().forEach(orderItem -> {
                productService.updateStock(orderItem.getProduct().getProductId(), orderItem.getQuantity());
            });
        }
        order.setOrderStatus(orderStatus);
        order = orderRepo.save(order);
        return modelMapper.map(order, OrderResponse.class);
    }

    @Override
    @Transactional
    public OrderResponse placeBuyNowOrder(Long cartItemId, Long shippingAddressId, User user) {
        CartItem cartItem = cartItemRepo.findByCartItemId(cartItemId).get();
        Address shippingAddress = addressRepo.findByAddressId(shippingAddressId)
        .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        if (cartItem.getCart().getUser().getUserId() != user.getUserId()
                || shippingAddress.getUser().getUserId() != user.getUserId())
            throw new AccessDeniedException("Access Denied: Something went wrong");
        Order order = new Order();
        order.setPlacedAt(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.PROCESSING);
        order.setShippingAddress(modelMapper.map(shippingAddress, AddressSnapshot.class));
        order.setTotalAmount(cartItem.getProduct().getPrice());
        order.setUser(user);
        OrderResponse orderResponse = modelMapper.map(orderRepo.save(order), OrderResponse.class);
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(modelMapper.map(cartItem.getProduct(), ProductSnapshot.class));
        orderItem.setOrder(order);
        orderItem.setQuantity(cartItem.getQuantity());
        orderItemRepo.save(orderItem);
        productService.updateStock(cartItem.getProduct().getProductId(), -orderItem.getQuantity());
        return orderResponse;
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long userId, Long orderId) {
        Order order = orderRepo.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getUserId().equals(userId))
            throw new AccessDeniedException("You are not allowed to perform this action");

        List<OrderItem> orderItems = orderItemRepo.findAllByOrder_OrderId(orderId);
        orderItems.stream().forEach(orderItem -> {
            productService.updateStock(orderItem.getProduct().getProductId(), orderItem.getQuantity());
        });
        order.setOrderStatus(OrderStatus.CANCELLED);
        order = orderRepo.save(order);
        return modelMapper.map(order, OrderResponse.class);
    }

}
