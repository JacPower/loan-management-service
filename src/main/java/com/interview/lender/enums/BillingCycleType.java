package com.interview.lender.enums;

import java.util.Arrays;
import java.util.List;

public enum BillingCycleType {
    INDIVIDUAL,
    CONSOLIDATED;



    public static List<String> getBillingCycleTypes() {
        return Arrays.stream(BillingCycleType.values())
                .map(Enum::name)
                .toList();
    }
}
