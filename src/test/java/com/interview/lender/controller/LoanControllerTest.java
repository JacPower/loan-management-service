package com.interview.lender.controller;

import com.interview.lender.dto.*;
import com.interview.lender.services.LoanService;
import com.interview.lender.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanControllerTest {

    @Mock
    private LoanService loanService;

    @InjectMocks
    private LoanController loanController;

    private SubscriptionRequest subscriptionRequest;
    private LoanRequest loanRequest;
    private ResponseDto successSubscriptionResponse;
    private ResponseDto failureResponse;
    private ResponseDto successLoanResponse;
    private ResponseDto successStatusResponse;



    @BeforeEach
    void setUp() {
        subscriptionRequest = TestUtil.createSubscriptionRequest();
        loanRequest = TestUtil.createLoanRequest();

        SubscriptionResponse subscriptionPayload = TestUtil.createSubscriptionResponse();
        successSubscriptionResponse = TestUtil.createSuccessResponseDto("Subscription successful", subscriptionPayload);

        LoanResponse loanPayload = TestUtil.createLoanResponse();
        successLoanResponse = ResponseDto.builder()
                .success(true)
                .message("Loan application submitted successfully")
                .httpStatus(HttpStatus.CREATED)
                .data(loanPayload)
                .build();

        LoanStatusResponse statusPayload = TestUtil.createLoanStatusResponse();
        successStatusResponse = TestUtil.createSuccessResponseDto("Loan status retrieved successfully", statusPayload);

        failureResponse = TestUtil.createFailureResponseDto("Operation failed", HttpStatus.BAD_REQUEST);
    }



    @Test
    void subscribeCustomer_shouldReturnSuccessResponse_whenServiceSucceeds() {
        when(loanService.subscribeCustomer(any(SubscriptionRequest.class))).thenReturn(successSubscriptionResponse);

        var response = loanController.subscribeCustomer(subscriptionRequest);

        assertValidResponse(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Subscription successful", response.getBody().getMessage());
    }



    @Test
    void subscribeCustomer_shouldReturnFailureResponse_whenServiceFails() {
        when(loanService.subscribeCustomer(any(SubscriptionRequest.class))).thenReturn(failureResponse);

        var response = loanController.subscribeCustomer(subscriptionRequest);

        assertValidResponse(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Operation failed", response.getBody().getMessage());
    }



    @Test
    void requestLoan_shouldReturnSuccessResponse_whenServiceSucceeds() {
        when(loanService.requestLoan(any(LoanRequest.class))).thenReturn(successLoanResponse);

        var response = loanController.requestLoan(loanRequest);

        assertValidResponse(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Loan application submitted successfully", response.getBody().getMessage());
    }



    @Test
    void requestLoan_shouldReturnFailureResponse_whenServiceFails() {
        when(loanService.requestLoan(any(LoanRequest.class))).thenReturn(failureResponse);

        var response = loanController.requestLoan(loanRequest);

        assertValidResponse(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Operation failed", response.getBody().getMessage());
    }



    @Test
    void getLoanStatus_shouldReturnSuccessResponse_whenServiceSucceeds() {
        when(loanService.getLoanStatus(TestUtil.TEST_CUSTOMER_NUMBER)).thenReturn(successStatusResponse);

        var response = loanController.getLoanStatus(TestUtil.TEST_CUSTOMER_NUMBER);

        assertValidResponse(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Loan status retrieved successfully", response.getBody().getMessage());
    }



    @Test
    void getLoanStatus_shouldReturnFailureResponse_whenServiceFails() {
        ResponseDto notFoundResponse = TestUtil.createFailureResponseDto("No loan found", HttpStatus.NOT_FOUND);
        when(loanService.getLoanStatus(TestUtil.TEST_CUSTOMER_NUMBER)).thenReturn(notFoundResponse);

        var response = loanController.getLoanStatus(TestUtil.TEST_CUSTOMER_NUMBER);

        assertValidResponse(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("No loan found", response.getBody().getMessage());
    }



    private void assertValidResponse(ResponseEntity<ResponseDto> response) {
        ResponseDto body = response.getBody();
        assertAll(
                () -> assertNotNull(body),
                () -> {
                    assertNotNull(body);
                    assertNotNull(body.getMessage());
                },
                () -> assertNotNull(response.getStatusCode()),
                () -> {
                    assertNotNull(body);
                    assertNotNull(body.getHttpStatus());
                }
        );
    }
}
