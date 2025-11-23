package com.jobconnect_backend.stripepayment;

import com.jobconnect_backend.stripepayment.entities.SubscriptionPlan;

import java.util.List;

public interface ISubscriptionPlanService {
    SubscriptionPlan getSubscriptionPlan(Integer planId);

    List<SubscriptionPlan> listAllSubscriptionPlans();
}
