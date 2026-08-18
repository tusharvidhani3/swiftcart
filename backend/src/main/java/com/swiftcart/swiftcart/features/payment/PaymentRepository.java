package com.swiftcart.swiftcart.features.payment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Payment findByPaymentOrderId(String paymentOrderId);
    Payment findByOrderId(Long orderId);
}
