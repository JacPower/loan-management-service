package com.interview.lender.services;

import com.interview.lender.config.AppConfig;
import com.interview.lender.dto.*;
import com.interview.lender.entity.Customer;
import com.interview.lender.entity.Loan;
import com.interview.lender.enums.LoanStatus;
import com.interview.lender.repository.CustomerRepository;
import com.interview.lender.repository.LoanRepository;
import com.interview.lender.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LoanService {

    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final CustomerService customerService;
    private final ScoringService scoringService;
    private final AppConfig appConfig;



    public ResponseDto subscribeCustomer(SubscriptionRequest request) {
        log.info("Processing subscription request for customer: {}", request.getCustomerNumber());

        if (customerRepository.existsByCustomerNumber(request.getCustomerNumber())) {
            log.error("Customer subscription already exist | CustomerNumber: {}", request.getCustomerNumber());
            return Util.buildErrorResponse("Customer subscription already exist", CONFLICT);
        }

        Optional<CustomerDto> optionalCustomerDto = customerService.getCustomerByNumber(request.getCustomerNumber());
        if (optionalCustomerDto.isEmpty()) {
            log.error("Customer not found | CustomerNumber: {}", request.getCustomerNumber());
            return Util.buildErrorResponse("Customer does not exist", NOT_FOUND);
        }

        CustomerDto customerDto = optionalCustomerDto.get();
        saveCustomer(customerDto);
        log.info("Customer saved to local database | CustomerNumber: {}", customerDto.getCustomerNumber());
        var response = SubscriptionResponse.builder()
                .customerNumber(customerDto.getCustomerNumber())
                .customerName(customerDto.getFirstName() + " " + customerDto.getLastName())
                .phoneNumber(customerDto.getMobile())
                .email(customerDto.getEmail())
                .build();

        return Util.buildSuccessResponse("Subscription successful", response, OK);
    }



    private void saveCustomer(CustomerDto dto) {
        Customer customer = Customer.builder()
                .customerNumber(dto.getCustomerNumber())
                .firstName(dto.getFirstName())
                .middleName(dto.getMiddleName())
                .lastName(dto.getLastName())
                .phoneNumber(dto.getMobile())
                .email(dto.getEmail())
                .gender(dto.getGender())
                .idNumber(dto.getIdNumber())
                .idNumberType(dto.getIdType())
                .monthlyIncome(dto.getMonthlyIncome())
                .isActive("ACTIVE".equalsIgnoreCase(dto.getStatus()))
                .registrationDate(dto.getCreatedAt())
                .build();

        customerRepository.save(customer);
    }



    public ResponseDto requestLoan(LoanRequest request) {
        log.info("Processing loan request | CustomerNumber: {}, amount: {}", request.getCustomerNumber(), request.getAmount());

        Optional<Customer> optionalCustomer = customerRepository.findByCustomerNumber(request.getCustomerNumber());
        if (optionalCustomer.isEmpty()) {
            log.error("Customer has not subscribed to the service | CustomerNumber: {}", request.getCustomerNumber());
            return Util.buildErrorResponse("You have not subscribed to the service.", BAD_REQUEST);
        }

        if(loanRepository.findOngoingLoanByCustomerNumber(request.getCustomerNumber()).isPresent()) {
            log.error("Customer has ongoing loan request | CustomerNumber: {}", request.getCustomerNumber());
            return Util.buildErrorResponse("You have an ongoing loan request.", BAD_REQUEST);
        }

        Customer customer = optionalCustomer.get();
        Loan loan = saveLoanRequest(request);

        String scoringToken = scoringService.initiateScoring(request.getCustomerNumber());
        updateLoanStatus(loan);
        updateCustomerScoringToken(customer, scoringToken);

        log.info("Loan application created | Loan ID: {}, scoring token: {}", loan.getId(), scoringToken);
        var response = LoanResponse.builder()
                .loanId(loan.getId())
                .customerNumber(loan.getCustomerNumber())
                .requestedAmount(loan.getRequestedAmount())
                .status(loan.getStatus().name())
                .applicationDate(loan.getApplicationDate())
                .build();

        return Util.buildSuccessResponse("Loan application submitted successfully", response, CREATED);
    }



    @Transactional(readOnly = true)
    public ResponseDto getLoanStatus(String customerNumber) {
        log.info("Getting loan status for customer: {}", customerNumber);

        List<Loan> loans = loanRepository.findByCustomerNumber(customerNumber);
        if (loans.isEmpty()) {
            log.info("No loan found | CustomerNumber: {}", customerNumber);
            return Util.buildErrorResponse("No loan found.", NOT_FOUND);
        }

        Loan loan = loans.getLast();
        var response = LoanStatusResponse.builder()
                .loanId(loan.getId())
                .customerNumber(loan.getCustomerNumber())
                .requestedAmount(loan.getRequestedAmount())
                .approvedAmount(loan.getApprovedAmount())
                .status(loan.getStatus().name())
                .creditScore(loan.getCreditScore())
                .creditLimit(loan.getCreditLimit())
                .exclusion(loan.getExclusion())
                .exclusionReason(loan.getExclusionReason())
                .applicationDate(loan.getApplicationDate())
                .approvalDate(loan.getApprovalDate())
                .build();

        return Util.buildSuccessResponse("Loan status retrieved successfully", response, OK);
    }



    public void processLoanScoring() {
        int maxRetries = appConfig.getScoringRetryMaxAttempts();
        Pageable pageable = PageRequest.of(0, 100);
        List<Loan> loansInProgress = loanRepository.findLoansForScoring(maxRetries, pageable);
        List<String> customerNumbers = loansInProgress.stream().map(Loan::getCustomerNumber).distinct().toList();

        List<Customer> customers = customerRepository.findByCustomerNumberIn(customerNumbers);

        log.info("Found {} loans to process for scoring...", loansInProgress.size());

        for (Loan loan : loansInProgress) {
            customers.stream().
                    filter(c -> Objects.equals(c.getCustomerNumber(), loan.getCustomerNumber())).
                    findFirst()
                    .ifPresent(customer -> processIndividualLoanScoring(loan, customer));
        }
    }



    private void processIndividualLoanScoring(Loan loan, Customer customer) {
        log.info("Processing scoring | loan ID: {}, token: {}", loan.getId(), customer.getScoringToken());

        Optional<ScoringResponse> scoringResponseOpt = scoringService.getScore(customer.getScoringToken());

        if (scoringResponseOpt.isPresent()) {
            updateLoanWithScoringResult(loan, scoringResponseOpt.get());
            log.info("Scoring completed | loan ID: {}, score: {}", loan.getId(), scoringResponseOpt.get().getScore());
        } else {
            loan.setRetryCount(loan.getRetryCount() + 1);
            if (loan.getRetryCount() >= appConfig.getScoringRetryMaxAttempts()) {
                loan.setStatus(LoanStatus.FAILED);
                log.warn("Scoring failed | loan ID: {} after {} retries", loan.getId(), loan.getRetryCount());
            }
            log.info("Scoring not ready | loan ID: {}, retry count: {}", loan.getId(), loan.getRetryCount());
            loanRepository.save(loan);
        }
    }



    private Loan saveLoanRequest(LoanRequest request) {
        Loan loan = new Loan();
        loan.setCustomerNumber(request.getCustomerNumber());
        loan.setRequestedAmount(request.getAmount());
        loan.setStatus(LoanStatus.PENDING);
        loan.setApplicationDate(LocalDateTime.now());
        loan.setRetryCount(0);
        return loanRepository.save(loan);
    }



    private void updateLoanStatus(Loan loan) {
        loan.setStatus(LoanStatus.SCORING_IN_PROGRESS);
        loanRepository.save(loan);
    }



    private void updateCustomerScoringToken(Customer customer, String scoringToken) {
        customer.setScoringToken(scoringToken);
        customerRepository.save(customer);
    }



    private void updateLoanWithScoringResult(Loan loan, ScoringResponse scoringResponse) {
        loan.setCreditScore(scoringResponse.getScore());
        loan.setCreditLimit(scoringResponse.getLimitAmount());
        loan.setExclusion(scoringResponse.getExclusion());
        loan.setExclusionReason(scoringResponse.getExclusionReason());

        boolean approved = "No Exclusion".equals(scoringResponse.getExclusion())
                           && scoringResponse.getScore() >= 500
                           && loan.getRequestedAmount().compareTo(scoringResponse.getLimitAmount()) <= 0;

        if (approved) {
            loan.setStatus(LoanStatus.APPROVED);
            loan.setApprovedAmount(loan.getRequestedAmount());
            loan.setApprovalDate(LocalDateTime.now());
        } else {
            loan.setStatus(LoanStatus.REJECTED);
        }

        loanRepository.save(loan);
    }
}