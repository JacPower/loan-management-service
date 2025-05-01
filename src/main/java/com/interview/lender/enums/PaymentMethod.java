package com.interview.lender.enums;

import java.util.Arrays;
import java.util.List;

public enum PaymentMethod {
    BANK_TRANSFER,
    CARD,
    MOBILE_MONEY,
    CASH;



    public static List<String> getPaymentMethods() {
        return Arrays.stream(PaymentMethod.values())
                .map(Enum::name)
                .toList();

    }
}
