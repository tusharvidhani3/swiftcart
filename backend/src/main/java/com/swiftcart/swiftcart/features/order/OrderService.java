package com.swiftcart.swiftcart.features.order;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
import com.swiftcart.swiftcart.features.appuser.AppUserRepository;
import com.swiftcart.swiftcart.features.cart.CartItem;
import com.swiftcart.swiftcart.features.cart.CartService;
import com.swiftcart.swiftcart.features.payment.Payment;
import com.swiftcart.swiftcart.features.payment.PaymentDto;
import com.swiftcart.swiftcart.features.payment.PaymentService;
import com.swiftcart.swiftcart.features.payment.PaymentStatus;
import com.razorpay.RazorpayException;
import com.swiftcart.swiftcart.common.exception.BadRequestException;
import com.swiftcart.swiftcart.common.exception.ResourceNotFoundException;
import com.swiftcart.swiftcart.features.product.ProductService;

@Service
public class OrderService {

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
    private OrderMapper orderMapper;

    @Autowired
    private ShippingService shippingService;

    @Autowired
    private AppUserRepository userRepo;

    @Transactional
    public OrderResponse createOrder(PlaceOrderRequest placeOrderRequest, Long userId) throws RazorpayException {
        List<CartItem> cartItems = cartService.getCartItemsByUserId(userId);
        if (cartItems.isEmpty())
            throw new ResourceNotFoundException("Order cannot be placed on empty cart");
        Address shippingAddress = addressService.getAddressById(placeOrderRequest.shippingAddressId());
        if (!shippingAddress.getUser().getId().equals(userId))
            throw new AccessDeniedException("Unauthorized access to the address");
        Order order = new Order();
        order.setPlacedAt(LocalDateTime.now());
        order.setShippingAddress(addressMapper.toSnapshot(shippingAddress));
        order.setUser(shippingAddress.getUser());
        List<OrderItem> orderItems = new ArrayList<>();
        long subtotal = 0;
        for (CartItem ci : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(ci.getProduct());
            orderItem.setQuantity(ci.getQuantity());
            orderItem.setOrderItemStatus(placeOrderRequest.prepaid() ? OrderStatus.CREATED : OrderStatus.CONFIRMED);
            orderItem.setDeliveryAt(LocalDateTime.now().plusWeeks(1).withHour(20).withMinute(0).withSecond(0).withNano(0));
            orderItems.add(orderItem);
            productService.updateStock(ci.getProduct().getId(), ci.getProduct().getStock() - orderItem.getQuantity());
            subtotal += ci.getProduct().getPrice() * ci.getQuantity();
        }
        orderItems = orderItemRepo.saveAll(orderItems);
        long shippingCharge = shippingService.calculate(subtotal);
        order.setSubtotal(subtotal);
        order.setShippingCharge(shippingCharge);
        order.setTotalAmount(subtotal + shippingCharge);
        order = orderRepo.save(order);
        cartService.deleteCartItemsByUserId(userId);

        PaymentDto payment = null;
        if (placeOrderRequest.prepaid())
            payment = paymentService.createOrder(order);
        OrderResponse orderResponse = orderMapper.toResponse(order, orderItems, payment);
        return orderResponse;
    }

    public OrderResponse getOrder(Long orderId, Long userId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        AppUser user = userRepo.findById(userId).get();
        String role = user.getRole().getName();
        boolean isAdminOrSeller = role.equals("ROLE_ADMIN") || role.equals("ROLE_SELLER");
        if (!isAdminOrSeller && !order.getUser().getId().equals(userId))
            throw new AccessDeniedException("You are not authorized to access this order");
        List<OrderItem> orderItems = orderItemRepo.findByOrderId(orderId);
        PaymentDto paymentDto = paymentService.getPayment(order.getId());
        OrderResponse orderResponse = orderMapper.toResponse(order, orderItems, paymentDto);
        return orderResponse;
    }

    public Page<OrderResponse> getOrdersForAuthenticatedUser(Long userId, Pageable pageable) {
        return orderRepo.findByUserId(userId, pageable).map(order -> {
                    List<OrderItem> orderItems = orderItemRepo.findByOrderId(order.getId());
                    PaymentDto payment = paymentService.getPayment(order.getId());
                    OrderResponse orderResponse = orderMapper.toResponse(order, orderItems, payment);
                    return orderResponse;
                });
    }

    public Page<OrderResponseForSeller> getAllOrders(Pageable pageable) {
        return orderRepo.findAll(pageable)
                .map(order -> {
                    List<OrderItem> orderItems = orderItemRepo.findByOrderId(order.getId());
                    PaymentDto paymentDto = paymentService.getPayment(order.getId());
                    OrderResponseForSeller orderResponseForSeller = orderMapper.toResponseForSeller(order, orderItems, paymentDto);
                    return orderResponseForSeller;
                });
    }

    @Transactional
    public OrderResponse updateOrderItemStatus(Long userId, Long orderItemId, OrderStatus orderStatus) {
        OrderItem orderItem = orderItemRepo.findById(orderItemId).orElseThrow(() -> new ResourceNotFoundException("Order Item not found"));
        Order order = orderItem.getOrder();
        if(!order.getUser().getId().equals(userId))
            throw new AccessDeniedException("You are not authorized to access this order");
        if (orderStatus == OrderStatus.CANCELLED) {
            productService.updateStock(orderItem.getProduct().getId(), orderItem.getProduct().getStock() + orderItem.getQuantity());
        }
        orderItem.setOrderItemStatus(orderStatus);
        orderItem = orderItemRepo.save(orderItem);
        List<OrderItem> orderItems = orderItemRepo.findByOrderId(order.getId());
        PaymentDto paymentDto = paymentService.getPayment(order.getId());
        OrderResponse orderResponse = orderMapper.toResponse(order, orderItems, paymentDto);
        return orderResponse;
    }

    @Transactional
    public OrderResponse cancelOrderItem(Long userId, Long orderItemId) {
        OrderItem orderItem = orderItemRepo.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Item not found"));
        Order order = orderItem.getOrder();
        if (!orderItem.getOrder().getUser().getId().equals(userId))
            throw new AccessDeniedException("You are not allowed to perform this action");
        if (orderItem.getOrderItemStatus() == OrderStatus.CONFIRMED || orderItem.getOrderItemStatus() == OrderStatus.OUT_FOR_DELIVERY || orderItem.getOrderItemStatus() == OrderStatus.SHIPPED) {
            orderItem.setOrderItemStatus(OrderStatus.CANCELLED);
            productService.updateStock(orderItem.getProduct().getId(), orderItem.getProduct().getStock() + orderItem.getQuantity());
            orderItem = orderItemRepo.save(orderItem);
        } else {
            throw new BadRequestException("Cannot cancel an order item that is already delivered");
        }
        List<OrderItem> orderItems = orderItemRepo.findByOrderId(order.getId());
        PaymentDto paymentDto = paymentService.getPayment(order.getId());
        OrderResponse orderResponse = orderMapper.toResponse(order, orderItems, paymentDto);
        return orderResponse;
    }

    @Transactional
    public OrderResponse placeBuyNowOrder(PlaceBuyNowOrderRequest placeBuyNowOrderRequest, Long userId) throws RazorpayException {
        CartItem cartItem = cartService.getCartItemByCartItemId(placeBuyNowOrderRequest.cartItemId());
        Address shippingAddress = addressService.getAddressById(placeBuyNowOrderRequest.shippingAddressId());
        if (cartItem.getCart().getUser().getId() != userId || shippingAddress.getUser().getId() != userId)
            throw new AccessDeniedException("Access Denied: Something went wrong");
        Order order = new Order();
        order.setPlacedAt(LocalDateTime.now());
        order.setShippingAddress(addressMapper.toSnapshot(shippingAddress));
        order.setSubtotal(cartItem.getProduct().getPrice());
        order.setShippingCharge(order.getSubtotal());
        order.setTotalAmount(order.getSubtotal() + order.getShippingCharge());
        order.setUser(userRepo.getReferenceById(userId));
        order = orderRepo.save(order);
        Payment payment = new Payment();
        payment.setPaymentStatus(placeBuyNowOrderRequest.prepaid() ? PaymentStatus.PENDING : PaymentStatus.COD);
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(cartItem.getProduct());
        orderItem.setOrder(order);
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setOrderItemStatus(OrderStatus.CREATED);
        orderItem.setDeliveryAt(LocalDateTime.now().plusWeeks(1).withHour(20).withMinute(0).withSecond(0).withNano(0));
        orderItem = orderItemRepo.save(orderItem);
        productService.updateStock(cartItem.getProduct().getId(), orderItem.getProduct().getStock() - orderItem.getQuantity());
        PaymentDto paymentDto = null;
        if (placeBuyNowOrderRequest.prepaid())
            paymentDto = paymentService.createOrder(order);
        OrderResponse orderResponse = orderMapper.toResponse(order, List.of(orderItem), paymentDto);
        return orderResponse;
    }

    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        Order order = orderRepo.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        List<OrderItem> orderItems = orderItemRepo.findByOrderId(orderId);
        orderItems.stream().forEach(orderItem -> {
            if (orderItem.getOrderItemStatus() == OrderStatus.CONFIRMED
                    || orderItem.getOrderItemStatus() == OrderStatus.OUT_FOR_DELIVERY
                    || orderItem.getOrderItemStatus() == OrderStatus.SHIPPED) {
                orderItem.setOrderItemStatus(OrderStatus.CANCELLED);
                productService.updateStock(orderItem.getProduct().getId(), orderItem.getProduct().getStock() + orderItem.getQuantity());
                orderItem = orderItemRepo.save(orderItem);
            } else {
                throw new BadRequestException("Cannot cancel order, one or more item(s) delivered");
            }
        });
        PaymentDto payment = paymentService.getPayment(orderId);
        return orderMapper.toResponse(order, orderItems, payment);
    }

    public OrderStats getOrderStats(LocalDate startDate) {
        OrderStats orderStats = orderItemRepo.getOrderStats();
        orderMapper.toStats(orderStats);
        return orderStats;
    }

    public List<DailyOrderStats> getDailyOrderStats(LocalDate startDate) {
        List<DailyOrderStats> dailyOrderStats = orderItemRepo.getDailyOrderStats(startDate.atStartOfDay());
        return dailyOrderStats;
    }

}
