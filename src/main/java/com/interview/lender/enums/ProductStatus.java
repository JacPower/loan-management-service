package com.interview.lender.enums;

import java.util.Arrays;
import java.util.List;

public enum ProductStatus {
    ACTIVE,
    INACTIVE,
    DEPRECATED;



    public static List<String> getProductStatus() {
        return Arrays.stream(ProductStatus.values())
                .map(Enum::name)
                .toList();

    }
}
