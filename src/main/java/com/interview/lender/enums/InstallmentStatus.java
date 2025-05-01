package com.interview.lender.enums;

import java.util.Arrays;
import java.util.List;

public enum InstallmentStatus {
    UNPAID,
    PARTIALLY_PAID,
    PAID,
    OVERDUE;



    public static List<String> getInstallmentStatus() {
        return Arrays.stream(InstallmentStatus.values())
                .map(Enum::name)
                .toList();
    }
}
