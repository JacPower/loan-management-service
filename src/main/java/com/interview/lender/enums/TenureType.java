package com.interview.lender.enums;

import java.util.Arrays;
import java.util.List;

public enum TenureType {
    DAYS,
    MONTHS,
    YEARS;



    public static List<String> getTenureTypes() {
        return Arrays.stream(TenureType.values())
                .map(Enum::name)
                .toList();

    }
}
