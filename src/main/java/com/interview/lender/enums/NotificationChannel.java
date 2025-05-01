package com.interview.lender.enums;

import java.util.Arrays;
import java.util.List;

public enum NotificationChannel {
    EMAIL,
    SMS,
    PUSH;



    public static List<String> getNotificationChannels() {
        return Arrays.stream(NotificationChannel.values())
                .map(Enum::name)
                .toList();

    }
}
