package com.swiftcart.swiftcart.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.swiftcart.swiftcart.entity.Address;
import com.swiftcart.swiftcart.entity.Cart;
import com.swiftcart.swiftcart.entity.CartItem;
import com.swiftcart.swiftcart.entity.Order;
import com.swiftcart.swiftcart.entity.OrderItem;
import com.swiftcart.swiftcart.entity.OrderStatus;
import com.swiftcart.swiftcart.entity.User;
import com.swiftcart.swiftcart.exception.AccessDeniedException;
import com.swiftcart.swiftcart.payload.AddressSnapshot;
import com.swiftcart.swiftcart.payload.OrderDTO;
import com.swiftcart.swiftcart.payload.PlaceOrderRequest;
import com.swiftcart.swiftcart.repository.AddressRepo;
import com.swiftcart.swiftcart.repository.CartItemRepo;
import com.swiftcart.swiftcart.repository.CartRepo;
import com.swiftcart.swiftcart.repository.OrderItemRepo;
import com.swiftcart.swiftcart.repository.OrderRepo;
import com.swiftcart.swiftcart.repository.ProductRepo;

import jakarta.transaction.Transactional;

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
    ProductRepo productRepo;

    @Autowired
    AddressRepo addressRepo;

    @Autowired
    CartService cartService;

    @Override
    @Transactional
    public OrderDTO placeOrder(PlaceOrderRequest placeOrderRequest) {
        Cart cart=cartRepo.findByCartId(placeOrderRequest.getCartId());
        Address shippingAddress=addressRepo.findByAddressId(placeOrderRequest.getShippingAddressId());
        Order order=new Order();
        order.setOrderStatus(OrderStatus.PROCESSING);
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderDate(LocalDate.now());
        order.setShippingAddress(modelMapper.map(shippingAddress, AddressSnapshot.class));
        order.setUser(cart.getUser());
        List<CartItem> cartItems = cartItemRepo.findAllByCart_CartId(cart.getCartId());
        List<OrderItem> orderItems = new ArrayList<>();
        for(CartItem ci : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(ci.getProduct());
            orderItem.setQuantity(ci.getQuantity());
            orderItems.add(orderItem);
        }
        orderItemRepo.saveAll(orderItems);
        order=orderRepo.save(order);
        cartItemRepo.deleteByCart_CartId(cart.getCartId());
        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    public OrderDTO getOrder(Long orderId, Long userId) {
        Order order=orderRepo.findByOrderId(orderId);
        if(!order.getUser().getUserId().equals(userId))
        throw new AccessDeniedException("You are not authorized to access this order");
        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    public Page<OrderDTO> getLoggedInUserOrders(Long userId, Pageable pageable) {
        return orderRepo.findAllByUser_UserId(userId, pageable)
        .map(order -> modelMapper.map(order, OrderDTO.class));
    }

    @Override
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus orderStatus) {
        Order order=orderRepo.findByOrderId(orderId);
        order.setOrderStatus(orderStatus);
        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    public OrderDTO cancelOrder(Long userId, Long orderId) {
        Order order = orderRepo.findByOrderId(orderId);
        if(!order.getUser().getUserId().equals(userId))
        throw new AccessDeniedException("You are not allowed to perform this action.");
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepo.save(order);
        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    @Transactional
    public OrderDTO placeBuyNowOrder(Long cartItemId, Long shippingAddressId, User user) {
        CartItem cartItem = cartItemRepo.findByCartItemId(cartItemId).get();
        Address shippingAddress = addressRepo.findByAddressId(shippingAddressId);
        if(cartItem.getCart().getUser().getUserId() != user.getUserId() || shippingAddress.getUser().getUserId() != user.getUserId())
        throw new AccessDeniedException("Access Denied: Something went wrong");
        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        order.setOrderStatus(OrderStatus.PROCESSING);
        order.setShippingAddress(modelMapper.map(shippingAddress, AddressSnapshot.class));;
        order.setTotalAmount(cartItem.getProduct().getPrice());
        order.setUser(user);
        OrderDTO orderDTO = modelMapper.map(orderRepo.save(order), OrderDTO.class);
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(cartItem.getProduct());
        orderItem.setOrder(order);
        orderItem.setQuantity(cartItem.getQuantity());
        orderItemRepo.save(orderItem);
        return orderDTO;
    }

}
