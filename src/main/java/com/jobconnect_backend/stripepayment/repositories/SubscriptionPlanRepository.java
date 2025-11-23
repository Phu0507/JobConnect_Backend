package com.jobconnect_backend.stripepayment.repositories;

import com.jobconnect_backend.stripepayment.entities.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Integer> {
}
