package com.interview.lender.enums;

import java.util.Arrays;
import java.util.List;

public enum BillingCycle {
    INDIVIDUAL,
    CONSOLIDATED;



    public static List<String> getBillingCycles() {
        return Arrays.stream(BillingCycle.values())
                .map(Enum::name)
                .toList();
    }
}
