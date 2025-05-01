package com.interview.lender.enums;

import java.util.Arrays;
import java.util.List;

public enum BusinessRules {
    CUSTOMER, PRODUCT, FEE, LOAN, NOTIFICATION;



    public static List<String> getBusinessRules() {
        return Arrays.stream(BusinessRules.values())
                .map(Enum::name)
                .toList();
    }
}
