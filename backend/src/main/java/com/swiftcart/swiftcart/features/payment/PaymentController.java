package com.swiftcart.swiftcart.features.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.razorpay.RazorpayException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @PostMapping("/webhook")
    public ResponseEntity<?> handleWebhook(@RequestBody String payload, @RequestHeader("X-Razorpay-Signature") String signature) throws RazorpayException {
        paymentService.verifyPaymentAndConfirmOrder(payload, signature);
        return ResponseEntity.ok().build();
    }
}
