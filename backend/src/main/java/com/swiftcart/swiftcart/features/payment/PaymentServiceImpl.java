package com.swiftcart.swiftcart.features.payment;

import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.swiftcart.swiftcart.features.order.Order;
import com.swiftcart.swiftcart.features.order.OrderItem;
import com.swiftcart.swiftcart.features.order.OrderItemRepo;
import com.swiftcart.swiftcart.features.order.OrderStatus;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${RAZORPAY_KEY_ID}")
    private String keyId;

    @Value("${RAZORPAY_KEY_SECRET}")
    private String keySecret;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private OrderItemRepo orderItemRepo;

    @Override
    public PaymentDTO createOrder(Order order) throws RazorpayException {
        RazorpayClient razorpay = new RazorpayClient(keyId, keySecret);
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", order.getTotalAmount()*100);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + System.currentTimeMillis());
        com.razorpay.Order paymentOrder = razorpay.orders.create(orderRequest);
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentOrderId(paymentOrder.get("id").toString());
        payment = paymentRepo.save(payment);
        return modelMapper.map(payment, PaymentDTO.class);
    }

    @Override
    public void verifyPaymentAndConfirmOrder(String payload, String signature) throws RazorpayException {
        String secret = System.getenv("RAZORPAY_WEBHOOK_SECRET");
        boolean isValid = Utils.verifyWebhookSignature(payload, signature, secret);
        if (isValid) {
            JSONObject json = new JSONObject(payload);
            JSONObject paymentEntity = json.getJSONObject("payload")
                    .getJSONObject("payment")
                    .getJSONObject("entity");
            
            String paymentOrderId = paymentEntity.getString("order_id");
            System.out.println(paymentEntity.getString("method"));

            Payment payment = paymentRepo.findByPaymentOrderId(paymentOrderId);
            String paymentId = paymentEntity.getString("id");
            payment.setPaymentId(paymentId);
            if(json.getString("event").equals("payment.captured")) {
                payment.setPaymentStatus(PaymentStatus.PAID);
                List<OrderItem> orderItems = orderItemRepo.findAllByOrder_OrderId(payment.getOrder().getOrderId());
                orderItems = orderItems.stream().map(orderItem -> { 
                    orderItem.setOrderItemStatus(OrderStatus.CONFIRMED);
                    return orderItem;
                }).collect(Collectors.toList());
                orderItemRepo.saveAll(orderItems);
            }
            else {
                payment.setPaymentStatus(PaymentStatus.FAILED);
            }
            paymentRepo.save(payment);
        }
        else {
            throw new RazorpayException("Invalid signature");
        }
    }

    public PaymentDTO getPayment(Long orderId) {
        Payment payment = paymentRepo.findByOrder_OrderId(orderId);
        if(payment == null)
        return null;
        return modelMapper.map(payment, PaymentDTO.class);
    }

}
