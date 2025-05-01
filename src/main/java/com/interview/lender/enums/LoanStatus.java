package com.interview.lender.enums;

import java.util.Arrays;
import java.util.List;

public enum LoanStatus {
    PENDING,
    OPEN,
    CLOSED,
    CANCELLED,
    OVERDUE,
    WRITTEN_OFF,
    DEFAULTED;

    public static List<String> getLoanStatus() {
        return Arrays.stream(LoanStatus.values())
                .map(Enum::name)
                .toList();
    }
}
