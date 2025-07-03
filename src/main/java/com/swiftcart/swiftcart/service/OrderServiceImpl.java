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
import com.swiftcart.swiftcart.entity.Customer;
import com.swiftcart.swiftcart.entity.Order;
import com.swiftcart.swiftcart.entity.OrderItem;
import com.swiftcart.swiftcart.entity.OrderStatus;
import com.swiftcart.swiftcart.entity.User;
import com.swiftcart.swiftcart.exception.BadRequestException;
import com.swiftcart.swiftcart.exception.ResourceNotFoundException;
import com.swiftcart.swiftcart.payload.AddressDTO;
import com.swiftcart.swiftcart.payload.AddressSnapshot;
import com.swiftcart.swiftcart.payload.OrderItemResponse;
import com.swiftcart.swiftcart.payload.OrderItemResponseForSeller;
import com.swiftcart.swiftcart.payload.OrderResponse;
import com.swiftcart.swiftcart.payload.PaymentDTO;
import com.swiftcart.swiftcart.payload.PlaceOrderRequest;
import com.swiftcart.swiftcart.payload.SellerProductResponse;
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
    private SellerProductService sellerProductService;

    @Autowired
    private CartService cartService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AddressService addressService;

    @Override
    @Transactional
    public OrderResponse placeOrder(PlaceOrderRequest placeOrderRequest, Long userId) {
        Customer customer = customerService.getCustomerByUserId(userId);
        List<CartItem> cartItems = cartService.getCartItemsByCustomerId(customer.getCustomerId());
        if(cartItems.isEmpty())
        throw new ResourceNotFoundException("Order cannot be placed on empty cart");
        Address shippingAddress = addressService.getAddressByAddressId(placeOrderRequest.getShippingAddressId());
        if(!shippingAddress.getUser().getUserId().equals(userId))
        throw new AccessDeniedException("Unauthorized access to the address");
        Order order = new Order();
        order.setPlacedAt(LocalDateTime.now());
        order.setShippingAddress(modelMapper.map(shippingAddress, AddressSnapshot.class));
        order.setCustomer(customer);
        List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0;
        for (CartItem ci : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setSellerProduct(ci.getSellerProduct());
            orderItem.setQuantity(ci.getQuantity());
            orderItem.setOrderStatus(OrderStatus.PROCESSING);
            orderItems.add(orderItem);
            sellerProductService.updateStock(ci.getSellerProduct().getSellerProductId(), -orderItem.getQuantity());
            totalAmount += ci.getSellerProduct().getPrice()*ci.getQuantity();
        }
        order.setTotalAmount(totalAmount);
        orderItemRepo.saveAll(orderItems);
        order = orderRepo.save(order);
        cartService.deleteCartItemsByCustomerId(customer.getCustomerId());
        return modelMapper.map(order, OrderResponse.class);
    }

    @Override
    public OrderResponse getOrder(Long orderId, User user) {
        // Allow admin but allow customer or seller to view only if it belongs to them i.e. ordered by that customer or in multi-seller system allow access to orderitems that belongs to the seller not the full order 
        Order order = orderRepo.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
        if (!isAdmin && !order.getCustomer().getUser().getUserId().equals(user.getUserId())) // since this is currently single seller system, seller-orderItem.product relationship is not established and the seller is allowed to view all the orders without verification
            throw new AccessDeniedException("You are not authorized to access this order");
        List<OrderItemResponse> orderItems = orderItemRepo.findAllByOrder_OrderId(orderId).stream().map(orderItem -> {
            OrderItemResponse orderItemResponse = new OrderItemResponse();
            orderItemResponse.setOrderId(orderId);
            orderItemResponse.setOrderItemId(orderItem.getOrderItemId());
            orderItemResponse.setPlacedAt(order.getPlacedAt());
            orderItemResponse.setSellerProduct(modelMapper.map(orderItem.getSellerProduct(), SellerProductResponse.class));
            orderItemResponse.setQuantity(orderItem.getQuantity());
            return orderItemResponse;
        })
        .collect(Collectors.toList());
        OrderResponse orderResponse = modelMapper.map(order, OrderResponse.class);
        orderResponse.setOrderItems(orderItems);
        return orderResponse;
    }

    @Override
    public Page<OrderResponse> getOrdersForAuthenticatedCustomer(Long userId, Pageable pageable) {
        return orderRepo.findAllByCustomer_User_UserId(userId, pageable)
                .map(order -> modelMapper.map(order, OrderResponse.class));
    }

    @Override
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepo.findAll(pageable)
                .map(order -> modelMapper.map(order, OrderResponse.class));
    }

    @Override
    @Transactional
    public OrderItemResponse updateOrderItemStatus(User user, Long orderItemId, OrderStatus orderStatus) {
        OrderItem orderItem = orderItemRepo.findByOrderItemId(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Item not found"));

                boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
        if (!isAdmin && !orderItem.getSellerProduct().getSeller().getUser().getUserId().equals(user.getUserId()))
            throw new AccessDeniedException("You are not allowed to perform this action");
        if (orderStatus == OrderStatus.CANCELLED) {
            sellerProductService.updateStock(orderItem.getSellerProduct().getSellerProductId(), orderItem.getQuantity());
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
        if (!orderItem.getOrder().getCustomer().getUser().getUserId().equals(userId))
            throw new AccessDeniedException("You are not allowed to perform this action");
        if(orderItem.getOrderStatus() == OrderStatus.PENDING || orderItem.getOrderStatus() == OrderStatus.PROCESSING || orderItem.getOrderStatus() == OrderStatus.SHIPPED) {
            orderItem.setOrderStatus(OrderStatus.CANCELLED);
            sellerProductService.updateStock(orderItem.getSellerProduct().getSellerProductId(), orderItem.getQuantity());
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
        Customer customer = cartItem.getCart().getCustomer();
        if (customer.getUser().getUserId() != user.getUserId() || shippingAddress.getUser().getUserId() != user.getUserId())
            throw new AccessDeniedException("Access Denied: Something went wrong");
        Order order = new Order();
        order.setPlacedAt(LocalDateTime.now());
        order.setShippingAddress(modelMapper.map(shippingAddress, AddressSnapshot.class));
        order.setTotalAmount(cartItem.getSellerProduct().getPrice());
        order.setCustomer(customer);
        OrderResponse orderResponse = modelMapper.map(orderRepo.save(order), OrderResponse.class);
        OrderItem orderItem = new OrderItem();
        orderItem.setSellerProduct(cartItem.getSellerProduct());
        orderItem.setOrder(order);
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setOrderStatus(OrderStatus.PROCESSING);
        orderItemRepo.save(orderItem);
        sellerProductService.updateStock(cartItem.getSellerProduct().getSellerProductId(), -orderItem.getQuantity());
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
                sellerProductService.updateStock(orderItem.getSellerProduct().getSellerProductId(), orderItem.getQuantity());
                orderItem = orderItemRepo.save(orderItem);
                }
                else {
                    throw new BadRequestException("Cannot cancel order, one or more item(s) delivered");
                }
        });
        return modelMapper.map(order, OrderResponse.class);
    }

    @Override
    public Page<OrderItemResponse> getOrderItemsForAuthenticatedCustomer(Long userId, Pageable pageable) {
        Page<OrderItemResponse> orderItems = orderItemRepo.findAllByOrder_Customer_User_UserId(userId, pageable)
                                                     .map(orderItem -> {
                                                        OrderItemResponse orderItemResponse = new OrderItemResponse();
                                                        orderItemResponse.setOrderId(orderItem.getOrder().getOrderId());
                                                        orderItemResponse.setOrderItemId(orderItem.getOrderItemId());
                                                        orderItemResponse.setSellerProduct(modelMapper.map(orderItem.getSellerProduct(), SellerProductResponse.class));
                                                        orderItemResponse.setQuantity(orderItem.getQuantity());
                                                        orderItemResponse.setPlacedAt(orderItem.getOrder().getPlacedAt());
                                                        return orderItemResponse;
                                                     });
        return orderItems;

    }

    public Page<OrderItemResponseForSeller> getOrderItemsForAuthenticatedSeller(Long userId, Pageable pageable) {
        Page<OrderItemResponseForSeller> orderItems = orderItemRepo.findAllBySellerProduct_Seller_User_UserId(userId, pageable).map(orderItem -> {
            OrderItemResponseForSeller orderItemResponse = new OrderItemResponseForSeller();
            Order order = orderItem.getOrder();
            modelMapper.map(orderItem, OrderItemResponseForSeller.class);
            orderItemResponse.setBuyerName(order.getCustomer().getFirstName()+" "+orderItem.getOrder().getCustomer().getLastName());
            orderItemResponse.setAddress(modelMapper.map(order.getShippingAddress(), AddressDTO.class)); //AddressResponse
            orderItemResponse.setOrderDate(order.getPlacedAt());
            orderItemResponse.setPayment(modelMapper.map(order.getPayment(), PaymentDTO.class));
            return orderItemResponse;
        });
        return orderItems;
    }

}
