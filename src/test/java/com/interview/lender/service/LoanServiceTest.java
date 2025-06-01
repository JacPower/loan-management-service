package com.interview.lender.service;

import com.interview.lender.config.AppConfig;
import com.interview.lender.dto.CustomerDto;
import com.interview.lender.dto.LoanRequest;
import com.interview.lender.dto.ScoringResponse;
import com.interview.lender.dto.SubscriptionRequest;
import com.interview.lender.entity.Customer;
import com.interview.lender.entity.Loan;
import com.interview.lender.enums.LoanStatus;
import com.interview.lender.repository.CustomerRepository;
import com.interview.lender.repository.LoanRepository;
import com.interview.lender.services.CustomerService;
import com.interview.lender.services.LoanService;
import com.interview.lender.services.ScoringService;
import com.interview.lender.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CustomerService customerService;
    @Mock
    private ScoringService scoringService;
    @Mock
    private AppConfig appConfig;

    @InjectMocks
    private LoanService loanService;

    private SubscriptionRequest subscriptionRequest;
    private LoanRequest loanRequest;
    private CustomerDto customerDto;
    private Customer customer;
    private Loan loan;
    private ScoringResponse scoringResponse;



    @BeforeEach
    void setUp() {
        subscriptionRequest = TestUtil.createSubscriptionRequest();
        loanRequest = TestUtil.createLoanRequest();
        customerDto = TestUtil.createCustomerDto();
        customer = TestUtil.createCustomerEntity();
        loan = TestUtil.createLoanEntity();
        scoringResponse = TestUtil.createScoringResponse();
    }



    @Test
    void subscribeCustomer_shouldReturnSuccessResponse_whenCustomerExists() {
        when(customerRepository.existsByCustomerNumber(TestUtil.TEST_CUSTOMER_NUMBER)).thenReturn(false);
        when(customerService.getCustomerByNumber(TestUtil.TEST_CUSTOMER_NUMBER)).thenReturn(Optional.of(customerDto));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        var response = loanService.subscribeCustomer(subscriptionRequest);

        TestUtil.assertSuccessResponse(response);
        assertEquals("Subscription successful", response.getMessage());
        verify(customerRepository).save(any(Customer.class));
    }



    @Test
    void subscribeCustomer_shouldReturnFailureResponse_whenCustomerAlreadyExists() {
        when(customerRepository.existsByCustomerNumber(TestUtil.TEST_CUSTOMER_NUMBER)).thenReturn(true);

        var response = loanService.subscribeCustomer(subscriptionRequest);

        TestUtil.assertFailureResponse(response);
        assertEquals("Customer subscription already exist", response.getMessage());
        verify(customerService, never()).getCustomerByNumber(anyString());
    }



    @Test
    void subscribeCustomer_shouldReturnFailureResponse_whenCustomerNotFoundInKyc() {
        when(customerRepository.existsByCustomerNumber(TestUtil.TEST_CUSTOMER_NUMBER)).thenReturn(false);
        when(customerService.getCustomerByNumber(TestUtil.TEST_CUSTOMER_NUMBER)).thenReturn(Optional.empty());

        var response = loanService.subscribeCustomer(subscriptionRequest);

        TestUtil.assertFailureResponse(response);
        assertEquals("Customer does not exist", response.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }



    @Test
    void requestLoan_shouldReturnSuccessResponse_whenValidRequest() {
        when(customerRepository.findByCustomerNumber(TestUtil.TEST_CUSTOMER_NUMBER)).thenReturn(Optional.of(customer));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        when(scoringService.initiateScoring(TestUtil.TEST_CUSTOMER_NUMBER)).thenReturn(TestUtil.TEST_SCORING_TOKEN);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        var response = loanService.requestLoan(loanRequest);

        TestUtil.assertSuccessResponse(response);
        assertEquals("Loan application submitted successfully", response.getMessage());
        verify(loanRepository, times(2)).save(any(Loan.class));
        verify(scoringService).initiateScoring(TestUtil.TEST_CUSTOMER_NUMBER);
    }



    @Test
    void requestLoan_shouldReturnFailureResponse_whenCustomerNotSubscribed() {
        when(customerRepository.findByCustomerNumber(TestUtil.TEST_CUSTOMER_NUMBER)).thenReturn(Optional.empty());

        var response = loanService.requestLoan(loanRequest);

        assertFalse(response.isSuccess());
        verify(loanRepository, never()).save(any(Loan.class));
    }



    @Test
    void getLoanStatus_shouldReturnSuccessResponse_whenLoanExists() {
        Loan approvedLoan = TestUtil.createApprovedLoanEntity();
        when(loanRepository.findByCustomerNumber(TestUtil.TEST_CUSTOMER_NUMBER)).thenReturn(List.of(approvedLoan));

        var response = loanService.getLoanStatus(TestUtil.TEST_CUSTOMER_NUMBER);

        TestUtil.assertSuccessResponse(response);
        assertEquals("Loan status retrieved successfully", response.getMessage());
        assertNotNull(response.getData());
    }



    @Test
    void getLoanStatus_shouldReturnFailureResponse_whenNoLoanExists() {
        when(loanRepository.findByCustomerNumber(TestUtil.TEST_CUSTOMER_NUMBER)).thenReturn(List.of());

        var response = loanService.getLoanStatus(TestUtil.TEST_CUSTOMER_NUMBER);

        TestUtil.assertFailureResponse(response);
        assertEquals("No loan found.", response.getMessage());
    }



    @Test
    void processLoanScoring_shouldUpdateLoanToApproved_whenScoringIsSuccessful() {
        Loan scoringLoan = TestUtil.createScoringInProgressLoanEntity();
        when(appConfig.getScoringRetryMaxAttempts()).thenReturn(5);
        when(loanRepository.findLoansForScoring(5, PageRequest.of(0, 100))).thenReturn(List.of(scoringLoan));
        when(customerRepository.findByCustomerNumberIn(anyList())).thenReturn(List.of(customer));
        when(scoringService.getScore(TestUtil.TEST_SCORING_TOKEN)).thenReturn(Optional.of(scoringResponse));
        when(loanRepository.save(any(Loan.class))).thenReturn(scoringLoan);

        loanService.processLoanScoring();

        verify(loanRepository).save(argThat(savedLoan ->
                savedLoan.getStatus() == LoanStatus.APPROVED &&
                savedLoan.getCreditScore().equals(TestUtil.TEST_CREDIT_SCORE)
        ));
    }



    @Test
    void processLoanScoring_shouldUpdateLoanToFailed_whenMaxRetriesReached() {
        Loan scoringLoan = TestUtil.createScoringInProgressLoanEntity();
        scoringLoan.setRetryCount(4);
        when(appConfig.getScoringRetryMaxAttempts()).thenReturn(5);
        when(loanRepository.findLoansForScoring(5, PageRequest.of(0, 100))).thenReturn(List.of(scoringLoan));
        when(customerRepository.findByCustomerNumberIn(anyList())).thenReturn(List.of(customer));
        when(scoringService.getScore(TestUtil.TEST_SCORING_TOKEN)).thenReturn(Optional.empty());
        when(loanRepository.save(any(Loan.class))).thenReturn(scoringLoan);

        loanService.processLoanScoring();

        verify(loanRepository).save(argThat(savedLoan ->
                savedLoan.getRetryCount().equals(5) &&
                savedLoan.getStatus() == LoanStatus.FAILED
        ));
    }
}
