package com.swiftcart.swiftcart.features.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {

    public Payment findByPaymentOrderId(String paymentOrderId);
    public Payment findByOrder_OrderId(Long orderId);
}
