package com.interview.lender.enums;

import java.util.Arrays;
import java.util.List;

public enum LoanStructureType {
    LUMP_SUM,
    INSTALLMENT;



    public static List<String> getLoanStructureTypes() {
        return Arrays.stream(LoanEventType.values())
                .map(Enum::name)
                .toList();
    }
}
