package com.swiftcart.swiftcart.features.order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swiftcart.swiftcart.features.address.Address;
import com.swiftcart.swiftcart.features.address.AddressMapper;
import com.swiftcart.swiftcart.features.address.AddressService;
import com.swiftcart.swiftcart.features.appuser.AppUser;
import com.swiftcart.swiftcart.features.cart.CartItem;
import com.swiftcart.swiftcart.features.cart.CartService;
import com.swiftcart.swiftcart.features.payment.Payment;
import com.swiftcart.swiftcart.features.payment.PaymentDto;
import com.swiftcart.swiftcart.features.payment.PaymentService;
import com.swiftcart.swiftcart.features.payment.PaymentStatus;
import com.razorpay.RazorpayException;
import com.swiftcart.swiftcart.common.exception.BadRequestException;
import com.swiftcart.swiftcart.common.exception.ResourceNotFoundException;
import com.swiftcart.swiftcart.features.product.ProductMapper;
import com.swiftcart.swiftcart.features.product.ProductResponse;
import com.swiftcart.swiftcart.features.product.ProductService;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderItemRepository orderItemRepo;

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    @Transactional
    public OrderResponse createOrder(PlaceOrderRequest placeOrderRequest, Long userId) throws RazorpayException {
        List<CartItem> cartItems = cartService.getCartItemsByUserId(userId);
        if (cartItems.isEmpty())
            throw new ResourceNotFoundException("Order cannot be placed on empty cart");
        Address shippingAddress = addressService.getAddressById(placeOrderRequest.getShippingAddressId());
        if (!shippingAddress.getUser().getId().equals(userId))
            throw new AccessDeniedException("Unauthorized access to the address");
        Order order = new Order();
        order.setPlacedAt(LocalDateTime.now());
        order.setShippingAddress(addressMapper.toSnapshot(shippingAddress));
        order.setUser(shippingAddress.getUser());
        List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0;
        for (CartItem ci : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(ci.getProduct());
            orderItem.setQuantity(ci.getQuantity());
            orderItem.setOrderItemStatus(placeOrderRequest.getPrepaid() ? OrderStatus.CREATED : OrderStatus.CONFIRMED);
            orderItem.setDeliveryAt(LocalDateTime.now().plusWeeks(1).withHour(20).withMinute(0).withSecond(0).withNano(0));
            orderItems.add(orderItem);
            productService.updateStock(ci.getProduct().getId(), -orderItem.getQuantity());
            totalAmount += ci.getProduct().getPrice() * ci.getQuantity();
        }
        order.setTotalAmount(totalAmount);
        orderItemRepo.saveAll(orderItems);
        order = orderRepo.save(order);
        cartService.deleteCartItemsByUserId(userId);
        OrderResponse orderResponse = orderMapper.toResponse(order);
        if (placeOrderRequest.getPrepaid())
            orderResponse.setPayment(paymentService.createOrder(order));
        return orderResponse;
    }

    @Override
    public OrderResponse getOrder(Long orderId, AppUser user) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        String role = user.getRole().getName();
        boolean isAdminOrSeller = role.equals("ROLE_ADMIN") || role.equals("ROLE_SELLER");
        if (!isAdminOrSeller && !order.getUser().getId().equals(user.getId()))
            throw new AccessDeniedException("You are not authorized to access this order");
        List<OrderItemResponse> orderItems = orderItemRepo.findByOrderId(orderId).stream().map(orderItem -> {
            OrderItemResponse orderItemResponse = new OrderItemResponse();
            orderItemResponse.setId(orderItem.getId());
            ProductResponse productResponse = productMapper.toResponse(orderItem.getProduct());
            productResponse.setImageUrls(productService.getProductImages(orderItem.getProduct().getId()));
            orderItemResponse.setProduct(productResponse);
            orderItemResponse.setQuantity(orderItem.getQuantity());
            orderItemResponse.setOrderItemStatus(orderItem.getOrderItemStatus());
            orderItemResponse.setDeliveryAt(orderItem.getDeliveryAt());
            return orderItemResponse;
        })
                .collect(Collectors.toList());
        OrderResponse orderResponse = orderMapper.toResponse(order);
        orderResponse.setOrderItems(orderItems);
        PaymentDto paymentDto = paymentService.getPayment(order.getId());
        if(paymentDto != null)
            orderResponse.setPayment(paymentDto);
        return orderResponse;
    }

    @Override
    public Page<OrderResponse> getOrdersForAuthenticatedUser(Long userId, Pageable pageable) {
        return orderRepo.findByUserId(userId, pageable)
                .map(order -> {
                    OrderResponse orderResponse = orderMapper.toResponse(order);
                    List<OrderItem> orderItems = orderItemRepo.findByOrderId(order.getId());
                    List<OrderItemResponse> orderItemResponseList = orderItems.stream().map(orderItem -> {
                        ProductResponse productResponse = productMapper.toResponse(orderItem.getProduct());
                        productResponse.setImageUrls(productService.getProductImages(orderItem.getProduct().getId()));
                        OrderItemResponse orderItemResponse = orderItemMapper.toResponse(orderItem);
                        orderItemResponse.setProduct(productResponse);
                        return orderItemResponse;
                    }).collect(Collectors.toList());
                    orderResponse.setOrderItems(orderItemResponseList);
                    PaymentDto paymentDto = paymentService.getPayment(order.getId());
                    if(paymentDto != null)
                        orderResponse.setPayment(paymentDto);
                    return orderResponse;
                });
    }

    @Override
    public Page<OrderResponseForSeller> getAllOrders(Pageable pageable) {
        return orderRepo.findAll(pageable)
                .map(order -> {
                    OrderResponseForSeller orderResponseForSeller = orderMapper.toResponseForSeller(order);
                    List<OrderItem> orderItems = orderItemRepo.findByOrderId(order.getId());
                    List<OrderItemResponse> orderItemResponseList = orderItems.stream().map(orderItem -> {
                        ProductResponse productResponse = productMapper.toResponse(orderItem.getProduct());
                        productResponse.setImageUrls(productService.getProductImages(orderItem.getProduct().getId()));
                        OrderItemResponse orderItemResponse = orderItemMapper.toResponse(orderItem);
                        orderItemResponse.setProduct(productResponse);
                        return orderItemResponse;
                    }).collect(Collectors.toList());
                    orderResponseForSeller.setOrderItems(orderItemResponseList);
                    PaymentDto paymentDto = paymentService.getPayment(order.getId());
                    if(paymentDto != null)
                        orderResponseForSeller.setPayment(paymentDto);
                    return orderResponseForSeller;
                });
    }

    @Override
    @Transactional
    public OrderItemResponse updateOrderItemStatus(AppUser user, Long orderItemId, OrderStatus orderStatus) {
        OrderItem orderItem = orderItemRepo.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Item not found"));
        if (orderStatus == OrderStatus.CANCELLED) {
            productService.updateStock(orderItem.getProduct().getId(), orderItem.getQuantity());
        }
        orderItem.setOrderItemStatus(orderStatus);
        orderItem = orderItemRepo.save(orderItem);
        return orderItemMapper.toResponse(orderItem);
    }

    @Override
    @Transactional
    public OrderItemResponse cancelOrderItem(Long userId, Long orderItemId) {
        OrderItem orderItem = orderItemRepo.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Item not found"));
        if (!orderItem.getOrder().getUser().getId().equals(userId))
            throw new AccessDeniedException("You are not allowed to perform this action");
        if (orderItem.getOrderItemStatus() == OrderStatus.CONFIRMED || orderItem.getOrderItemStatus() == OrderStatus.OUT_FOR_DELIVERY || orderItem.getOrderItemStatus() == OrderStatus.SHIPPED) {
            orderItem.setOrderItemStatus(OrderStatus.CANCELLED);
            productService.updateStock(orderItem.getProduct().getId(), orderItem.getQuantity());
            orderItem = orderItemRepo.save(orderItem);
        }
        else {
            throw new BadRequestException("Cannot cancel an order item that is already delivered");
        }
        return orderItemMapper.toResponse(orderItem);
    }

    @Override
    @Transactional
    public OrderResponse placeBuyNowOrder(PlaceBuyNowOrderRequest placeBuyNowOrderRequest, AppUser user) throws RazorpayException {
        CartItem cartItem = cartService.getCartItemByCartItemId(placeBuyNowOrderRequest.getCartItemId());
        Address shippingAddress = addressService.getAddressById(placeBuyNowOrderRequest.getShippingAddressId());
        if (cartItem.getCart().getUser().getId() != user.getId() || shippingAddress.getUser().getId() != user.getId())
            throw new AccessDeniedException("Access Denied: Something went wrong");
        Order order = new Order();
        order.setPlacedAt(LocalDateTime.now());
        order.setShippingAddress(addressMapper.toSnapshot(shippingAddress));
        order.setTotalAmount(cartItem.getProduct().getPrice());
        order.setUser(user);
        order = orderRepo.save(order);
        Payment payment = new Payment();
        payment.setPaymentStatus(placeBuyNowOrderRequest.getPrepaid() ? PaymentStatus.PENDING : PaymentStatus.COD);
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(cartItem.getProduct());
        orderItem.setOrder(order);
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setOrderItemStatus(OrderStatus.CREATED);
        orderItem.setDeliveryAt(LocalDateTime.now().plusWeeks(1).withHour(20).withMinute(0).withSecond(0).withNano(0));
        orderItemRepo.save(orderItem);
        productService.updateStock(cartItem.getProduct().getId(), -orderItem.getQuantity());
        OrderResponse orderResponse = orderMapper.toResponse(order);
        if (placeBuyNowOrderRequest.getPrepaid())
            orderResponse.setPayment(paymentService.createOrder(order));
        return orderResponse;
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        List<OrderItem> orderItems = orderItemRepo.findByOrderId(orderId);
        orderItems.stream().forEach(orderItem -> {
            if (orderItem.getOrderItemStatus() == OrderStatus.CONFIRMED || orderItem.getOrderItemStatus() == OrderStatus.OUT_FOR_DELIVERY || orderItem.getOrderItemStatus() == OrderStatus.SHIPPED) {
                orderItem.setOrderItemStatus(OrderStatus.CANCELLED);
                productService.updateStock(orderItem.getProduct().getId(), orderItem.getQuantity());
                orderItem = orderItemRepo.save(orderItem);
            }
            else {
                throw new BadRequestException("Cannot cancel order, one or more item(s) delivered");
            }
        });
        return orderMapper.toResponse(order);
    }

}
