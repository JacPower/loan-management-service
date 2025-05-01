package com.interview.lender.enums;

import java.util.Arrays;
import java.util.List;

public enum NotificationType {
    LOAN_CREATION,
    LOAN_OVERDUE,
    PAYMENT_RECEIVED,
    PAYMENT_REMINDER,
    LOAN_WRITTEN_OFF,
    INSTALLMENT_REMINDER,
    LOAN_CLOSED,
    LOAN_DEFAULT,
    FEE_APPLIED;



    public static List<String> getNotificationTypes() {
        return Arrays.stream(NotificationType.values())
                .map(Enum::name)
                .toList();

    }
}
