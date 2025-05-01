package com.interview.lender.services;

import com.interview.lender.entity.Customer;
import com.interview.lender.entity.Loan;
import com.interview.lender.entity.Product;
import com.interview.lender.enums.LoanStatus;
import com.interview.lender.exception.BusinessException;
import com.interview.lender.repository.CustomerRepository;
import com.interview.lender.repository.LoanRepository;
import com.interview.lender.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanUtils {
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final LoanRepository loanRepository;



    public void validateLoanAmountLimit(double principalAmount, double loanLimit) {
        if (principalAmount > loanLimit) {
            String message = String.format("Loan amount %.2f exceeds customer's loan limit %.2f", principalAmount, loanLimit);
            log.error(message);
            throw BusinessException.loanLimitExceeded(message);
        }
    }



    public void verifyCustomerHasNoOpenLoans(UUID customerId) {
        var hasOpenLoan = loanRepository.existsByCustomerIdAndLoanStatus(customerId, LoanStatus.OPEN);
        if (hasOpenLoan) throw BusinessException.hasOpenLoan("Customer has an existing loan");
    }



    public Loan getLoan(String code, LoanStatus status) {
        return loanRepository.findByLoanCodeAndLoanStatus(code, status)
                .orElseThrow(() -> BusinessException.notFound("Loan not found with code: " + code));
    }



    public Customer findCustomerById(UUID customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.error("Customer not found with ID: {}", customerId);
                    return new ResourceNotFoundException("Customer account does not exist");
                });
    }



    public Product findProductById(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product not found with ID: {}", productId);
                    return new ResourceNotFoundException("Product does not exist");
                });
    }



    public LocalDate addTenureToDate(LocalDate startDate, Product product) {
        return switch (product.getTenureType()) {
            case DAYS -> startDate.plusDays(product.getTenureValue());
            case MONTHS -> startDate.plusMonths(product.getTenureValue());
            case YEARS -> startDate.plusYears(product.getTenureValue());
        };
    }



    public LocalDate alignToPreferredBillingDay(LocalDate tenureBasedDueDate, int preferredDay) {
        int maxDaysInMonth = tenureBasedDueDate.getMonth().length(tenureBasedDueDate.isLeapYear());
        int actualDay = Math.min(preferredDay, maxDaysInMonth);

        LocalDate alignedDate = tenureBasedDueDate.withDayOfMonth(actualDay);

        if (alignedDate.isBefore(tenureBasedDueDate)) {
            LocalDate nextMonth = tenureBasedDueDate.plusMonths(1);
            maxDaysInMonth = nextMonth.getMonth().length(nextMonth.isLeapYear());
            actualDay = Math.min(preferredDay, maxDaysInMonth);
            alignedDate = nextMonth.withDayOfMonth(actualDay);
        }

        return alignedDate;
    }
}
