package com.interview.lender.enums;

import java.util.Arrays;
import java.util.List;

public enum FeeType {
    SERVICE,
    DAILY,
    LATE_PAYMENT;



    public static List<String> getPFeeTypes() {
        return Arrays.stream(FeeType.values())
                .map(Enum::name)
                .toList();
    }
}
