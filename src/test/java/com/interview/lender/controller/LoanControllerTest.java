package com.interview.lender.controller;

import com.interview.lender.dto.GlobalFiltersDto;
import com.interview.lender.dto.LoanRequestDto;
import com.interview.lender.dto.PaymentDto;
import com.interview.lender.dto.ResponseDto;
import com.interview.lender.rule.RuleDelegator;
import com.interview.lender.util.TestUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static com.interview.lender.enums.BusinessRules.LOAN;
import static com.interview.lender.enums.BusinessRulesOperations.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanControllerTest {

    @Mock
    private RuleDelegator ruleDelegator;

    @InjectMocks
    private LoanController loanController;

    private LoanRequestDto loanRequestDto;
    private PaymentDto paymentDto;
    private GlobalFiltersDto filtersDto;
    private ResponseDto responseDto;



    @BeforeEach
    void setUp() {
        loanRequestDto = TestUtilities.createLoanRequestDto();
        paymentDto = TestUtilities.createPaymentDto();
        filtersDto = GlobalFiltersDto.builder()
                .page(0)
                .size(10)
                .build();
        responseDto = TestUtilities.createSuccessResponse("Success", null);
    }



    @Test
    void createLoan_shouldReturnResponseEntity() {
        when(ruleDelegator.routeRequest(eq(LOAN), eq(CREATE_LOAN), any(LoanRequestDto.class))).thenReturn(responseDto);

        ResponseEntity<ResponseDto> response = loanController.createLoan(loanRequestDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }



    @Test
    void payLoan_shouldReturnResponseEntity() {
        when(ruleDelegator.routeRequest(eq(LOAN), eq(PAY_LOAN), any(PaymentDto.class))).thenReturn(responseDto);

        ResponseEntity<ResponseDto> response = loanController.payLoan(paymentDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }



    @Test
    void getAllLoans_shouldReturnResponseEntity() {
        when(ruleDelegator.routeRequest(eq(LOAN), eq(GET_ALL_LOANS), any(GlobalFiltersDto.class))).thenReturn(responseDto);

        ResponseEntity<ResponseDto> response = loanController.getAllLoans(filtersDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }



    @Test
    void getLoan_shouldReturnResponseEntity() {
        UUID loanId = UUID.randomUUID();
        when(ruleDelegator.routeRequest(eq(LOAN), eq(GET_LOAN), any(GlobalFiltersDto.class))).thenReturn(responseDto);

        ResponseEntity<ResponseDto> response = loanController.getLoan(loanId, filtersDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }



    @Test
    void getLoanPayments_shouldReturnResponseEntity() {
        UUID loanId = UUID.randomUUID();
        when(ruleDelegator.routeRequest(eq(LOAN), eq(GET_LOAN_PAYMENTS), any(GlobalFiltersDto.class))).thenReturn(responseDto);

        ResponseEntity<ResponseDto> response = loanController.getLoanPayments(loanId, filtersDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }



    @Test
    void createLoan_shouldReturnFailureResponse_whenRuleDelegatorFails() {
        ResponseDto failureResponse = TestUtilities.createFailureResponse("Loan creation failed");
        when(ruleDelegator.routeRequest(eq(LOAN), eq(CREATE_LOAN), any(LoanRequestDto.class))).thenReturn(failureResponse);

        ResponseEntity<ResponseDto> response = loanController.createLoan(loanRequestDto);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }



    @Test
    void payLoan_shouldSetCorrectHttpStatus() {
        responseDto.setHttpStatus(HttpStatus.CREATED);
        when(ruleDelegator.routeRequest(eq(LOAN), eq(PAY_LOAN), any(PaymentDto.class))).thenReturn(responseDto);

        ResponseEntity<ResponseDto> response = loanController.payLoan(paymentDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}
