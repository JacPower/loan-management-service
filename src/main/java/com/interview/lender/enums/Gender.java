package com.interview.lender.enums;

import java.util.Arrays;
import java.util.List;

public enum Gender {
    MALE, FEMALE;



    public static List<String> getGenders() {
        return Arrays.stream(Gender.values())
                .map(Enum::name)
                .toList();
    }
}
