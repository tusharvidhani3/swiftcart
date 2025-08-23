package com.swiftcart.swiftcart.features.payment;

import com.razorpay.RazorpayException;
import com.swiftcart.swiftcart.features.order.Order;

public interface PaymentService {

    public PaymentDTO createOrder(Order order) throws RazorpayException;
    public void verifyPaymentAndConfirmOrder(String payload, String signature) throws RazorpayException;
    public PaymentDTO getPayment(Long orderId);
}
