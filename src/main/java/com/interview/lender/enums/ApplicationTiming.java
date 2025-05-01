package com.interview.lender.enums;

import java.util.Arrays;
import java.util.List;

public enum ApplicationTiming {
    ORIGINATION,
    POST_DISBURSEMENT;



    public static List<String> getApplicationTimings() {
        return Arrays.stream(ApplicationTiming.values())
                .map(Enum::name)
                .toList();
    }
}
