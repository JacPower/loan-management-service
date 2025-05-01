package com.interview.lender.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException customerNotFound(String id) {
        return new ResourceNotFoundException("CustomerRule not found with id: " + id);
    }

    public static ResourceNotFoundException loanNotFound(String id) {
        return new ResourceNotFoundException("Loan not found with id: " + id);
    }

    public static ResourceNotFoundException productNotFound(String id) {
        return new ResourceNotFoundException("Product not found with id: " + id);
    }
}
