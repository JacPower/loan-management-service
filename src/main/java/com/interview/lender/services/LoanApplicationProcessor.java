package com.interview.lender.services;

import com.interview.lender.dto.GlobalFiltersDto;
import com.interview.lender.dto.LoanRequestDto;
import com.interview.lender.dto.PaginatedResponseDto;
import com.interview.lender.dto.ResponseDto;
import com.interview.lender.entity.*;
import com.interview.lender.enums.LoanEventType;
import com.interview.lender.enums.LoanStatus;
import com.interview.lender.exception.BusinessException;
import com.interview.lender.repository.*;
import com.interview.lender.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.interview.lender.enums.BillingCycle.CONSOLIDATED;
import static com.interview.lender.enums.InstallmentStatus.UNPAID;
import static com.interview.lender.enums.LoanStructureType.INSTALLMENT;
import static org.springframework.http.HttpStatus.OK;


@Slf4j
@Service
@RequiredArgsConstructor
public class LoanApplicationProcessor {
    private final LoanUtils loanUtils;
    private final LoanRepository loanRepository;
    private final LoanFeeRepository loanFeeRepository;
    private final ProductFeeRepository productFeeRepository;
    private final InstallmentRepository installmentRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationHandler notificationHandler;



    @Transactional
    public ResponseDto createLoan(LoanRequestDto requestDto) {
        Customer customer = loanUtils.findCustomerById(requestDto.getCustomerId());
        Product product = loanUtils.findProductById(requestDto.getProductId());

        loanUtils.validateLoanAmountLimit(requestDto.getPrincipalAmount(), customer.getLoanLimit());
        loanUtils.verifyCustomerHasNoOpenLoans(customer.getId());

        var loan = buildLoanEntity(customer, product, requestDto);
        var savedLoan = loanRepository.save(loan);

        //Since there is no actual c2b transaction, by-pass application timing check
        var loanWithUpdatedBalance = applyLoanFees(savedLoan, product);

        List<Installment> installments = INSTALLMENT.name().equals(requestDto.getStructureType().name()) ? createInstallments(loanWithUpdatedBalance) : List.of();
        loanWithUpdatedBalance.setInstallments(installments);

        //Since there is no actual c2b transaction, assume the transaction was done
        notificationHandler.sendLoanEventNotification(loanWithUpdatedBalance, LoanEventType.LOAN_CREATED);

        return Util.buildResponse("Loan application successful", OK, loanWithUpdatedBalance);
    }



    private Loan buildLoanEntity(Customer customer, Product product, LoanRequestDto requestDto) {
        Loan loan = Loan.builder()
                .product(product)
                .customer(customer)
                .principalAmount(requestDto.getPrincipalAmount())
                .disbursementDate(requestDto.getDisbursementDate())
                .structureType(requestDto.getStructureType())
                .loanCode(customer.getPhone())
                .loanStatus(LoanStatus.OPEN)
                .currentBalance(requestDto.getPrincipalAmount())
                .description(requestDto.getDescription())
                .build();
        loan.setDueDate(calculateDueDate(requestDto.getDisbursementDate(), product, customer));

        return loan;
    }



    private LocalDate calculateDueDate(LocalDate disbursementDate, Product product, Customer customer) {
        LocalDate dueDate = loanUtils.addTenureToDate(disbursementDate, product);

        if (CONSOLIDATED.name().equals(customer.getBillingCycle().name()) && customer.getPreferredBillingDay() > 0) {
            return loanUtils.alignToPreferredBillingDay(dueDate, customer.getPreferredBillingDay());
        }

        return dueDate;
    }



    private Loan applyLoanFees(Loan loan, Product product) {
        List<ProductFee> productFees = productFeeRepository.findByProductId(product.getId());

        for (ProductFee productFee : productFees) {
            Fee fee = productFee.getFee();
            if (Boolean.FALSE.equals(fee.getIsActive())) continue;

            double updatedBalance = applyFeeToLoan(loan, fee);
            loan.setCurrentBalance(updatedBalance);
        }

        return loan;
    }



    private double applyFeeToLoan(Loan loan, Fee fee) {
        double feeAmount = calculateFeeAmount(loan, fee);

        LoanFee loanFee = LoanFee.builder()
                .loan(loan)
                .fee(fee)
                .amount(feeAmount)
                .appliedAt(LocalDate.now().atStartOfDay())
                .build();

        loanFeeRepository.save(loanFee);
        loan.setCurrentBalance(loan.getCurrentBalance() + feeAmount);

        return loanRepository.save(loan).getCurrentBalance();
    }



    private double calculateFeeAmount(Loan loan, Fee fee) {
        return switch (fee.getCalculationType()) {
            case FIXED -> fee.getFeeValue();
            case PERCENTAGE -> loan.getPrincipalAmount() * fee.getFeeValue();
        };
    }



    private List<Installment> createInstallments(Loan loan) {
        Product product = loan.getProduct();
        Customer customer = loan.getCustomer();

        int count = calculateNumberOfInstallments(product);
        double amount = calculateInstallmentAmount(loan.getCurrentBalance(), count);

        List<Installment> installmentList = new ArrayList<>();
        LocalDate dueDate = loan.getDueDate();

        for (int i = 0; i < count; i++) {
            if (i > 0) dueDate = getNextInstallmentDate(dueDate, customer);

            Installment installment = Installment.builder()
                    .loan(loan)
                    .amount(amount)
                    .amountPaid(0.00)
                    .dueDate(dueDate)
                    .installmentStatus(UNPAID)
                    .build();

            installmentList.add(installmentRepository.save(installment));
        }
        installmentList.sort(Comparator.comparing(Installment::getDueDate));

        return installmentList;
    }



    private int calculateNumberOfInstallments(Product product) {
        return switch (product.getTenureType()) {
            case DAYS -> Math.max(1, product.getTenureValue() / 30);
            case MONTHS -> product.getTenureValue();
            case YEARS -> product.getTenureValue() * 12;
        };
    }



    private LocalDate getNextInstallmentDate(LocalDate current, Customer customer) {
        if (CONSOLIDATED.name().equals(customer.getBillingCycle().name()) && customer.getPreferredBillingDay() > 0) {
            return loanUtils.alignToPreferredBillingDay(current.minusMonths(1), customer.getPreferredBillingDay());
        }

        return current.minusMonths(1);
    }



    private double calculateInstallmentAmount(double amount, int count) {
        return Util.roundToTwoDecimals(amount / count);
    }



    public ResponseDto getLoan(GlobalFiltersDto filtersDto) {
        var loan = loanRepository.findById(filtersDto.getId())
                .orElseThrow(() -> BusinessException.notFound("Loan not found with id: " + filtersDto.getId()));

        return Util.buildResponse("Customer found", OK, loan);
    }



    public ResponseDto getAllLoans(GlobalFiltersDto filtersDto) {
        Pageable pageable = PageRequest.of(filtersDto.getPage(), filtersDto.getSize());
        List<Loan> list = loanRepository.findAll(pageable).getContent();
        String message = !list.isEmpty() ? "Loans found" : "Loan records not found";
        PaginatedResponseDto paginatedResponse = Util.buildPaginatedResponse(list, pageable);

        return Util.buildResponse(message, OK, paginatedResponse);
    }



    public ResponseDto getLoanPayments(GlobalFiltersDto filtersDto) {
        Pageable pageable = PageRequest.of(filtersDto.getPage(), filtersDto.getSize());
        List<Payment> paymentList = paymentRepository.findByLoanId(filtersDto.getId());
        String message = !paymentList.isEmpty() ? "Loan payments found" : "Loan payment records not found";
        PaginatedResponseDto paginatedResponse = Util.buildPaginatedResponse(paymentList, pageable);

        return Util.buildResponse(message, OK, paginatedResponse);
    }
}