package com.swiftcart.swiftcart.features.order;

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

import com.swiftcart.swiftcart.features.address.Address;
import com.swiftcart.swiftcart.features.address.AddressService;
import com.swiftcart.swiftcart.features.address.AddressSnapshot;
import com.swiftcart.swiftcart.features.cart.CartItem;
import com.swiftcart.swiftcart.features.cart.CartService;
import com.swiftcart.swiftcart.common.exception.BadRequestException;
import com.swiftcart.swiftcart.common.exception.ResourceNotFoundException;
import com.swiftcart.swiftcart.features.product.ProductResponse;
import com.swiftcart.swiftcart.features.product.ProductService;
import com.swiftcart.swiftcart.features.user.User;

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
        Address shippingAddress = addressService.getAddressById(placeOrderRequest.getShippingAddressId());
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
            orderItem.setOrderItemStatus(OrderStatus.PROCESSING);
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
            orderItemResponse.setProduct(modelMapper.map(orderItem.getProduct(), ProductResponse.class));
            orderItemResponse.setQuantity(orderItem.getQuantity());
            orderItemResponse.setOrderItemStatus(orderItem.getOrderItemStatus());
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
                .map(order -> {
                    OrderResponse orderResponse = modelMapper.map(order, OrderResponse.class);
                    List<OrderItem> orderItems = orderItemRepo.findAllByOrder_OrderId(order.getOrderId());
                    List<OrderItemResponse> orderItemResponseList = orderItems.stream().map(orderItem -> modelMapper.map(orderItem, OrderItemResponse.class)).collect(Collectors.toList());
                    orderResponse.setOrderItems(orderItemResponseList);
                    return orderResponse;
                });
    }

    @Override
    public Page<OrderResponseForSeller> getAllOrders(Pageable pageable) {
        return orderRepo.findAll(pageable)
                .map(order -> {
                    OrderResponseForSeller orderResponseForSeller = modelMapper.map(order, OrderResponseForSeller.class);
                    List<OrderItem> orderItems = orderItemRepo.findAllByOrder_OrderId(order.getOrderId());
                    List<OrderItemResponse> orderItemResponseList = orderItems.stream().map(orderItem -> modelMapper.map(orderItem, OrderItemResponse.class)).collect(Collectors.toList());
                    orderResponseForSeller.setOrderItems(orderItemResponseList);
                    return orderResponseForSeller;
                });
    }

    @Override
    @Transactional
    public OrderItemResponse updateOrderItemStatus(User user, Long orderItemId, OrderStatus orderStatus) {
        OrderItem orderItem = orderItemRepo.findByOrderItemId(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Item not found"));
        if (orderStatus == OrderStatus.CANCELLED) {
            productService.updateStock(orderItem.getProduct().getProductId(), orderItem.getQuantity());
        }
        orderItem.setOrderItemStatus(orderStatus);
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
        if(orderItem.getOrderItemStatus() == OrderStatus.PENDING || orderItem.getOrderItemStatus() == OrderStatus.PROCESSING || orderItem.getOrderItemStatus() == OrderStatus.SHIPPED) {
            orderItem.setOrderItemStatus(OrderStatus.CANCELLED);
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
    public OrderResponse placeBuyNowOrder(PlaceBuyNowOrderRequest placeBuyNowOrderRequest, User user) {
        CartItem cartItem = cartService.getCartItemByCartItemId(placeBuyNowOrderRequest.getCartItemId());
        Address shippingAddress = addressService.getAddressById(placeBuyNowOrderRequest.getShippingAddressId());
        if (cartItem.getCart().getUser().getUserId() != user.getUserId() || shippingAddress.getUser().getUserId() != user.getUserId())
            throw new AccessDeniedException("Access Denied: Something went wrong");
        Order order = new Order();
        order.setPlacedAt(LocalDateTime.now());
        order.setShippingAddress(modelMapper.map(shippingAddress, AddressSnapshot.class));
        order.setTotalAmount(cartItem.getProduct().getPrice());
        order.setUser(user);
        order = orderRepo.save(order);
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(cartItem.getProduct());
        orderItem.setOrder(order);
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setOrderItemStatus(OrderStatus.PROCESSING);
        orderItemRepo.save(orderItem);
        productService.updateStock(cartItem.getProduct().getProductId(), -orderItem.getQuantity());
        OrderResponse orderResponse = modelMapper.map(order, OrderResponse.class);
        return orderResponse;
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        Order order = orderRepo.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        List<OrderItem> orderItems = orderItemRepo.findAllByOrder_OrderId(orderId);
        orderItems.stream().forEach(orderItem -> {
                if(orderItem.getOrderItemStatus() == OrderStatus.PENDING || orderItem.getOrderItemStatus() == OrderStatus.PROCESSING || orderItem.getOrderItemStatus() == OrderStatus.SHIPPED) {
                    orderItem.setOrderItemStatus(OrderStatus.CANCELLED);
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
