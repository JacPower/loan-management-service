package com.interview.lender.enums;

import java.util.Arrays;
import java.util.List;

public enum LoanEventType {
    FEE_APPLIED,
    LOAN_CREATED,
    PAYMENT_DUE,
    PAYMENT_RECEIVED,
    LOAN_OVERDUE,
    LOAN_DEFAULTED,
    LOAN_WRITTEN_OFF,
    LOAN_CLOSED;



    public static List<String> getLoanEventTypes() {
        return Arrays.stream(LoanEventType.values())
                .map(Enum::name)
                .toList();
    }
}
