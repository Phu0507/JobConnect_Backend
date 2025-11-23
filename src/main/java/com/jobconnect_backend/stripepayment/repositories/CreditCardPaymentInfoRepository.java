package com.jobconnect_backend.stripepayment.repositories;

import com.jobconnect_backend.stripepayment.entities.CreditCardPaymentInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditCardPaymentInfoRepository extends JpaRepository<CreditCardPaymentInfo, Integer> {
}
