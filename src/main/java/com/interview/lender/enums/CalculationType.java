package com.interview.lender.enums;

import java.util.Arrays;
import java.util.List;

public enum CalculationType {
    FIXED,
    PERCENTAGE;



    public static List<String> getCalculationTypes() {
        return Arrays.stream(CalculationType.values())
                .map(Enum::name)
                .toList();
    }
}
