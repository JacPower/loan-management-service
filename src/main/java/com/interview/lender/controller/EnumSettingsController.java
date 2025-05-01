package com.interview.lender.controller;

import com.interview.lender.dto.ResponseDto;
import com.interview.lender.enums.*;
import com.interview.lender.util.Util;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/settings")
public class EnumSettingsController {

    @GetMapping
    public ResponseDto getSettings() {
        Map<String, Object> settings = new HashMap<>();

        settings.put("customerStatus", CustomerStatus.getCustomerStatus());
        settings.put("genders", Gender.getGenders());

        settings.put("loanStatus", LoanStatus.getLoanStatus());
        settings.put("loanEventTypes", LoanEventType.getLoanEventTypes());
        settings.put("loanStructureTypes", LoanStructureType.getLoanStructureTypes());

        settings.put("paymentStatus", PaymentStatus.getPaymentStatus());
        settings.put("paymentMethods", PaymentMethod.getPaymentMethods());
        settings.put("installmentStatus", InstallmentStatus.getInstallmentStatus());

        settings.put("feeTypes", FeeType.getPFeeTypes());
        settings.put("calculationTypes", CalculationType.getCalculationTypes());

        settings.put("productStatus", ProductStatus.getProductStatus());

        settings.put("notificationStatus", NotificationStatus.getNotificationStatus());
        settings.put("notificationChannels", NotificationChannel.getNotificationChannels());
        settings.put("notificationTypes", NotificationType.getNotificationTypes());

        settings.put("billingCycleTypes", BillingCycleType.getBillingCycleTypes());
        settings.put("billingCycles", BillingCycle.getBillingCycles());

        settings.put("applicationTimings", ApplicationTiming.getApplicationTimings());
        settings.put("tenureTypes", TenureType.getTenureTypes());
        settings.put("businessRules", BusinessRules.getBusinessRules());
        settings.put("businessRulesOperations", BusinessRulesOperations.getBusinessRulesOperations());

        return Util.buildResponse("Settings retrieved successfully", OK, settings);
    }
}
