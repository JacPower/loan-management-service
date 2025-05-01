package com.interview.lender.enums;

import java.util.Arrays;
import java.util.List;

public enum NotificationStatus {
    PENDING,
    SENT,
    FAILED;



    public static List<String> getNotificationStatus() {
        return Arrays.stream(NotificationStatus.values())
                .map(Enum::name)
                .toList();

    }
}
