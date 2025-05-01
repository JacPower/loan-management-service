package com.interview.lender.enums;

import java.util.Arrays;
import java.util.List;

public enum PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED,
    REFUNDED;



    public static List<String> getPaymentStatus() {
        return Arrays.stream(PaymentStatus.values())
                .map(Enum::name)
                .toList();

    }
}
