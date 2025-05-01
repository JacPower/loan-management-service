package com.interview.lender.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }

    public static BusinessException loanLimitExceeded(String message) {
        return new BusinessException(message);
    }

    public static BusinessException hasOpenLoan(String message) {
        return new BusinessException(message);
    }

    public static BusinessException notFound(String message) {
        return new BusinessException(message);
    }

    public static BusinessException alreadyExist(String message) {
        return new BusinessException(message);
    }

    public static BusinessException unknownOperation(String message) {
        return new BusinessException(message);
    }
}
