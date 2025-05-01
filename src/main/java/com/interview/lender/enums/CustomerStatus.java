package com.interview.lender.enums;

import java.util.Arrays;
import java.util.List;

public enum CustomerStatus {
    ACTIVE,
    INACTIVE,
    BLOCKED;

    public static List<String> getCustomerStatus() {
        return Arrays.stream(CustomerStatus.values())
                .map(Enum::name)
                .toList();
    }
}
