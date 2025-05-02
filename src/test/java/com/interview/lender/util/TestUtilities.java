package com.interview.lender.util;

import com.interview.lender.dto.*;
import com.interview.lender.entity.*;
import com.interview.lender.enums.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

public class TestUtilities {

    public static final UUID SAMPLE_CUSTOMER_ID = UUID.randomUUID();
    public static final UUID SAMPLE_PRODUCT_ID = UUID.randomUUID();
    public static final UUID SAMPLE_LOAN_ID = UUID.randomUUID();
    public static final UUID SAMPLE_FEE_ID = UUID.randomUUID();
    public static final String SAMPLE_PHONE = "+254712345678";
    public static final String SAMPLE_EMAIL = "test@example.com";
    public static final String SAMPLE_ID_NUMBER = "12345678";
    public static final String SAMPLE_LOAN_CODE = "LOAN-001";
    public static final String SAMPLE_PAYMENT_CODE = "PAY-001";



    public static Customer createCustomer() {
        var customer = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .email(SAMPLE_EMAIL)
                .phone(SAMPLE_PHONE)
                .idNumber(SAMPLE_ID_NUMBER)
                .dob(LocalDate.now().minusYears(30))
                .gender(Gender.MALE)
                .loanLimit(100000.00)
                .billingCycle(BillingCycle.INDIVIDUAL)
                .preferredBillingDay(15)
                .accountStatus(CustomerStatus.ACTIVE)
                .build();
        customer.setId(SAMPLE_CUSTOMER_ID);

        return customer;
    }



    public static Product createProduct() {
        var product = Product.builder()
                .productName("Personal Loan")
                .description("Standard personal loan product")
                .tenureType(TenureType.MONTHS)
                .tenureValue(12)
                .daysAfterDueForLateFee(5)
                .productStatus(ProductStatus.ACTIVE)
                .isFixedTerm(true)
                .isNotificationEnabled(true)
                .build();
        product.setId(SAMPLE_PRODUCT_ID);

        return product;
    }



    public static Fee createFee() {
        var fee = Fee.builder()
                .feeName("Processing Fee")
                .feeType(FeeType.DAILY)
                .calculationType(CalculationType.PERCENTAGE)
                .feeValue(0.02)
                .applicationTiming(ApplicationTiming.ORIGINATION)
                .description("Standard processing fee")
                .isActive(true)
                .build();
        fee.setId(SAMPLE_FEE_ID);

        return fee;
    }



    public static Loan createLoan(Customer customer, Product product) {
        var loan = Loan.builder()
                .customer(customer)
                .product(product)
                .loanCode(SAMPLE_LOAN_CODE)
                .principalAmount(50000.00)
                .currentBalance(50000.00)
                .amountPaid(0.00)
                .disbursementDate(LocalDate.now())
                .dueDate(LocalDate.now().plusMonths(product.getTenureValue()))
                .structureType(LoanStructureType.INSTALLMENT)
                .loanStatus(LoanStatus.OPEN)
                .lateFeeApplied(0.00)
                .description("Personal loan")
                .installments(new ArrayList<>())
                .build();
        loan.setId(SAMPLE_LOAN_ID);

        return loan;
    }



    public static Installment createInstallment(Loan loan, int seq, double amount) {
        var installment = Installment.builder()
                .loan(loan)
                .amount(amount)
                .amountPaid(0.00)
                .dueDate(LocalDate.now().plusMonths(seq))
                .installmentStatus(InstallmentStatus.UNPAID)
                .build();
        installment.setId(UUID.randomUUID());

        return installment;
    }



    public static Payment createPayment(Loan loan) {
        var payment = Payment.builder()
                .loan(loan)
                .amount(5000.00)
                .paymentCode(SAMPLE_PAYMENT_CODE)
                .paymentStatus(PaymentStatus.COMPLETED)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .build();
        payment.setId(UUID.randomUUID());

        return payment;
    }



    public static Notification createNotification(Customer customer, Loan loan) {
        var notification = Notification.builder()
                .customer(customer)
                .loan(loan)
                .notificationType(NotificationType.LOAN_CREATION)
                .content("Loan created successfully")
                .channel(NotificationChannel.EMAIL)
                .status(NotificationStatus.PENDING)
                .build();

        notification.setId(UUID.randomUUID());

        return notification;
    }



    public static CustomerDto createCustomerDto() {
        return CustomerDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email(SAMPLE_EMAIL)
                .phone(SAMPLE_PHONE)
                .idNumber(SAMPLE_ID_NUMBER)
                .dob(LocalDate.now().minusYears(30))
                .gender(Gender.MALE)
                .loanLimit(100000.00)
                .billingCycle(BillingCycle.INDIVIDUAL)
                .preferredBillingDay(15)
                .status(CustomerStatus.ACTIVE)
                .build();
    }



    public static ProductDto createProductDto() {
        return ProductDto.builder()
                .productName("Personal Loan")
                .description("Standard personal loan product")
                .tenureType(TenureType.MONTHS)
                .tenureValue(12)
                .daysAfterDueForLateFee(5)
                .productStatus(ProductStatus.ACTIVE)
                .isFixedTerm(true)
                .isNotificationEnabled(true)
                .build();
    }



    public static FeeDto createFeeDto() {
        return FeeDto.builder()
                .feeName("Processing Fee")
                .feeType(FeeType.SERVICE)
                .calculationType(CalculationType.PERCENTAGE)
                .feeValue(0.02)
                .applicationTiming(ApplicationTiming.ORIGINATION)
                .description("Standard processing fee")
                .isActive(true)
                .build();
    }



    public static LoanRequestDto createLoanRequestDto() {
        return LoanRequestDto.builder()
                .customerId(SAMPLE_CUSTOMER_ID)
                .productId(SAMPLE_PRODUCT_ID)
                .principalAmount(50000.00)
                .disbursementDate(LocalDate.now())
                .structureType(LoanStructureType.INSTALLMENT)
                .description("Personal loan request")
                .build();
    }



    public static PaymentDto createPaymentDto() {
        return PaymentDto.builder()
                .loanCode(SAMPLE_LOAN_CODE)
                .amount(5000.00)
                .paymentCode(SAMPLE_PAYMENT_CODE)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .build();
    }



    public static ResponseDto createSuccessResponse(String message, Object data) {
        return ResponseDto.builder()
                .success(true)
                .message(message)
                .data(data)
                .httpStatus(HttpStatus.OK)
                .build();
    }



    public static ResponseDto createFailureResponse(String message) {
        return ResponseDto.builder()
                .success(false)
                .message(message)
                .httpStatus(HttpStatus.BAD_REQUEST)
                .build();
    }



    public static LoanJobDto createLoanJobDto() {
        return LoanJobDto.builder()
                .executionDate(LocalDate.now())
                .overdueThreshold(1)
                .defaultThresholdDays(30)
                .writeOffThresholdDays(90)
                .reminderDays(3)
                .build();
    }
}
