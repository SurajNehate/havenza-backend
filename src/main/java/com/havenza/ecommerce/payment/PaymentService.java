package com.havenza.ecommerce.payment;

import com.havenza.ecommerce.order.OrderEntity;
import com.havenza.ecommerce.order.OrderRepository;
import com.havenza.ecommerce.order.OrderStatus;
import com.havenza.ecommerce.payment.dto.PaymentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public PaymentDto processPayment(Long orderId, Long userId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized");
        }

        if (paymentRepository.findByOrderId(orderId).isPresent()) {
            throw new RuntimeException("Payment already processing or completed");
        }

        PaymentEntity payment = PaymentEntity.builder()
                .order(order)
                .method(order.getPaymentMethod())
                .amount(order.getTotalAmount().subtract(order.getDiscountAmount()))
                .status(PaymentStatus.PENDING)
                .build();

        // Simulate payment gateway delay and result
        if (order.getPaymentMethod() == PaymentMethod.COD) {
            payment.setStatus(PaymentStatus.PENDING); // Will be collected later
            order.setStatus(OrderStatus.CONFIRMED);
        } else {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            order.setStatus(OrderStatus.CONFIRMED);
        }

        orderRepository.save(order);
        return PaymentDto.fromEntity(paymentRepository.save(payment));
    }

    @Transactional(readOnly = true)
    public PaymentDto getPaymentStatus(Long orderId, Long userId) {
        PaymentEntity payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order"));

        if (!payment.getOrder().getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized");
        }

        return PaymentDto.fromEntity(payment);
    }
}
