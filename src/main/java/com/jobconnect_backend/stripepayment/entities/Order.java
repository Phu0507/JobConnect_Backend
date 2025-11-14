package com.jobconnect_backend.stripepayment.entities;

import com.jobconnect_backend.entities.User;
import com.jobconnect_backend.stripepayment.entities.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Double paymentServiceFees;
    private Double totalPriceLessFees;
    private Double totalPrice;

    private PaymentStatus status;

    private LocalDateTime createdAt;
    private boolean isPay;

    @ManyToOne
    @JoinColumn(name = "credit_card_payment_info_id")
    private CreditCardPaymentInfo creditCardPaymentInfo;

    @ManyToOne
    @JoinColumn(name = "subscription_plan_id", nullable = false)
    private SubscriptionPlan subscriptionPlan;
    private String paymentIntentId;
}
