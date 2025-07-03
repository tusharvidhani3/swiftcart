package com.swiftcart.swiftcart.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swiftcart.swiftcart.entity.Address;
import com.swiftcart.swiftcart.entity.CartItem;
import com.swiftcart.swiftcart.entity.Order;
import com.swiftcart.swiftcart.entity.OrderItem;
import com.swiftcart.swiftcart.entity.OrderStatus;
import com.swiftcart.swiftcart.entity.User;
import com.swiftcart.swiftcart.exception.BadRequestException;
import com.swiftcart.swiftcart.exception.ResourceNotFoundException;
import com.swiftcart.swiftcart.payload.AddressSnapshot;
import com.swiftcart.swiftcart.payload.OrderItemResponse;
import com.swiftcart.swiftcart.payload.OrderResponseForSeller;
import com.swiftcart.swiftcart.payload.OrderResponse;
import com.swiftcart.swiftcart.payload.PlaceOrderRequest;
import com.swiftcart.swiftcart.payload.ProductResponse;
import com.swiftcart.swiftcart.repository.OrderItemRepo;
import com.swiftcart.swiftcart.repository.OrderRepo;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderItemRepo orderItemRepo;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    @Autowired
    private AddressService addressService;

    @Override
    @Transactional
    public OrderResponse placeOrder(PlaceOrderRequest placeOrderRequest, Long userId) {
        List<CartItem> cartItems = cartService.getCartItemsByUserId(userId);
        if(cartItems.isEmpty())
        throw new ResourceNotFoundException("Order cannot be placed on empty cart");
        Address shippingAddress = addressService.getAddressByAddressId(placeOrderRequest.getShippingAddressId());
        if(!shippingAddress.getUser().getUserId().equals(userId))
        throw new AccessDeniedException("Unauthorized access to the address");
        Order order = new Order();
        order.setPlacedAt(LocalDateTime.now());
        order.setShippingAddress(modelMapper.map(shippingAddress, AddressSnapshot.class));
        order.setUser(shippingAddress.getUser());
        List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0;
        for (CartItem ci : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(ci.getProduct());
            orderItem.setQuantity(ci.getQuantity());
            orderItem.setOrderStatus(OrderStatus.PROCESSING);
            orderItems.add(orderItem);
            productService.updateStock(ci.getProduct().getProductId(), -orderItem.getQuantity());
            totalAmount += ci.getProduct().getPrice()*ci.getQuantity();
        }
        order.setTotalAmount(totalAmount);
        orderItemRepo.saveAll(orderItems);
        order = orderRepo.save(order);
        cartService.deleteCartItemsByUserId(userId);
        return modelMapper.map(order, OrderResponse.class);
    }

    @Override
    public OrderResponse getOrder(Long orderId, User user) {
        Order order = orderRepo.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        String role = user.getRole().getName();
        boolean isAdminOrSeller = role.equals("ROLE_ADMIN") || role.equals("ROLE_SELLER");
        if (!isAdminOrSeller && !order.getUser().getUserId().equals(user.getUserId()))
            throw new AccessDeniedException("You are not authorized to access this order");
        List<OrderItemResponse> orderItems = orderItemRepo.findAllByOrder_OrderId(orderId).stream().map(orderItem -> {
            OrderItemResponse orderItemResponse = new OrderItemResponse();
            orderItemResponse.setOrderItemId(orderItem.getOrderItemId());
            orderItemResponse.setPlacedAt(order.getPlacedAt());
            orderItemResponse.setProduct(modelMapper.map(orderItem.getProduct(), ProductResponse.class));
            orderItemResponse.setQuantity(orderItem.getQuantity());
            return orderItemResponse;
        })
        .collect(Collectors.toList());
        OrderResponse orderResponse = modelMapper.map(order, OrderResponse.class);
        orderResponse.setOrderItems(orderItems);
        return orderResponse;
    }

    @Override
    public Page<OrderResponse> getOrdersForAuthenticatedUser(Long userId, Pageable pageable) {
        return orderRepo.findAllByUser_UserId(userId, pageable)
                .map(order -> modelMapper.map(order, OrderResponse.class));
    }

    @Override
    public Page<OrderResponseForSeller> getAllOrders(Pageable pageable) {
        return orderRepo.findAll(pageable)
                .map(order -> modelMapper.map(order, OrderResponseForSeller.class));
    }

    @Override
    @Transactional
    public OrderItemResponse updateOrderItemStatus(User user, Long orderItemId, OrderStatus orderStatus) {
        OrderItem orderItem = orderItemRepo.findByOrderItemId(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Item not found"));
        if (orderStatus == OrderStatus.CANCELLED) {
            productService.updateStock(orderItem.getProduct().getProductId(), orderItem.getQuantity());
        }
        orderItem.setOrderStatus(orderStatus);
        orderItem = orderItemRepo.save(orderItem);
        return modelMapper.map(orderItem, OrderItemResponse.class);
    }

    @Override
    @Transactional
    public OrderItemResponse cancelOrderItem(Long userId, Long orderItemId) {
        OrderItem orderItem = orderItemRepo.findByOrderItemId(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Item not found"));
        if (!orderItem.getOrder().getUser().getUserId().equals(userId))
            throw new AccessDeniedException("You are not allowed to perform this action");
        if(orderItem.getOrderStatus() == OrderStatus.PENDING || orderItem.getOrderStatus() == OrderStatus.PROCESSING || orderItem.getOrderStatus() == OrderStatus.SHIPPED) {
            orderItem.setOrderStatus(OrderStatus.CANCELLED);
            productService.updateStock(orderItem.getProduct().getProductId(), orderItem.getQuantity());
            orderItem = orderItemRepo.save(orderItem);
        }
        else {
            throw new BadRequestException("Cannot cancel an order item that is already delivered");
        }
        return modelMapper.map(orderItem, OrderItemResponse.class);
    }

    @Override
    @Transactional
    public OrderResponse placeBuyNowOrder(Long cartItemId, Long shippingAddressId, User user) {
        CartItem cartItem = cartService.getCartItemByCartItemId(cartItemId);
        Address shippingAddress = addressService.getAddressByAddressId(shippingAddressId);
        if (cartItem.getCart().getUser().getUserId() != user.getUserId() || shippingAddress.getUser().getUserId() != user.getUserId())
            throw new AccessDeniedException("Access Denied: Something went wrong");
        Order order = new Order();
        order.setPlacedAt(LocalDateTime.now());
        order.setShippingAddress(modelMapper.map(shippingAddress, AddressSnapshot.class));
        order.setTotalAmount(cartItem.getProduct().getPrice());
        order.setUser(user);
        OrderResponse orderResponse = modelMapper.map(orderRepo.save(order), OrderResponse.class);
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(cartItem.getProduct());
        orderItem.setOrder(order);
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setOrderStatus(OrderStatus.PROCESSING);
        orderItemRepo.save(orderItem);
        productService.updateStock(cartItem.getProduct().getProductId(), -orderItem.getQuantity());
        return orderResponse;
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        Order order = orderRepo.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        List<OrderItem> orderItems = orderItemRepo.findAllByOrder_OrderId(orderId);
        orderItems.stream().forEach(orderItem -> {
                if(orderItem.getOrderStatus() == OrderStatus.PENDING || orderItem.getOrderStatus() == OrderStatus.PROCESSING || orderItem.getOrderStatus() == OrderStatus.SHIPPED) {
                orderItem.setOrderStatus(OrderStatus.CANCELLED);
                productService.updateStock(orderItem.getProduct().getProductId(), orderItem.getQuantity());
                orderItem = orderItemRepo.save(orderItem);
                }
                else {
                    throw new BadRequestException("Cannot cancel order, one or more item(s) delivered");
                }
        });
        return modelMapper.map(order, OrderResponse.class);
    }

}
