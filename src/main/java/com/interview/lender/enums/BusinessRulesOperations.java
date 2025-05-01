package com.interview.lender.enums;

import java.util.Arrays;
import java.util.List;

public enum BusinessRulesOperations {
    CREATE_LOAN("CREATE_LOAN"),
    PAY_LOAN("PAY_LOAN"),
    MARK_LOANS_OVERDUE("MARK_LOANS_OVERDUE"),
    MARK_LOANS_DEFAULTED("MARK_LOANS_DEFAULTED"),
    MARK_LOANS_WRITE_OFF("MARK_LOANS_WRITE_OFF"),
    GET_LOAN("GET_LOAN"),
    GET_ALL_LOANS("GET_ALL_LOANS"),
    GET_LOAN_PAYMENTS("GET_LOAN_PAYMENTS"),
    GET_CUSTOMER_LOANS("GET_CUSTOMER_LOANS"),
    GENERATE_REPORT("GENERATE_REPORT"),
    SEND_PAYMENT_DUE_REMINDER("SEND_PAYMENT_DUE_REMINDER"),
    SEND_INSTALLMENT_DUE_REMINDER("SEND_INSTALLMENT_DUE_REMINDER"),
    APPLY_LATE_FEES("APPLY_LATE_FEES"),

    CREATE_CUSTOMER("CREATE_CUSTOMER"),
    UPDATE_CUSTOMER("UPDATE_CUSTOMER"),
    GET_CUSTOMER("GET_CUSTOMER"),
    GET_ALL_CUSTOMERS("GET_ALL_CUSTOMERS"),

    CREATE_PRODUCT("CREATE_PRODUCT"),
    CREATE_PRODUCT_FEE("CREATE_PRODUCT_FEE"),
    UPDATE_PRODUCT("UPDATE_PRODUCT"),
    GET_PRODUCT("GET_PRODUCT"),
    GET_ALL_PRODUCTS("GET_ALL_PRODUCTS"),

    CREATE_FEE("CREATE_FEE"),
    UPDATE_FEE("UPDATE_FEE"),
    GET_FEE("GET_FEE"),
    GET_ALL_FEES("GET_ALL_FEES"),


    UPDATE_BILLING_PREFERENCES("UPDATE_BILLING_PREFERENCES"),
    NO_OPERATION("NO_OPERATION");

    private final String value;



    BusinessRulesOperations(String value) {
        this.value = value;
    }



    public static BusinessRulesOperations ofValue(Object value) {
        for (BusinessRulesOperations type : BusinessRulesOperations.values()) {
            if (type.value.equalsIgnoreCase(value.toString().toUpperCase())) return type;
        }
        return NO_OPERATION;
    }



    public static List<String> getBusinessRulesOperations() {
        return Arrays.stream(BusinessRulesOperations.values())
                .map(Enum::name)
                .toList();
    }
}
