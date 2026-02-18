package com.swiftcart.swiftcart.features.payment;

import com.razorpay.RazorpayException;
import com.swiftcart.swiftcart.features.order.Order;

public interface PaymentService {
    PaymentDto createOrder(Order order) throws RazorpayException;
    void verifyPaymentAndConfirmOrder(String payload, String signature) throws RazorpayException;
    PaymentDto getPayment(Long orderId);
}
