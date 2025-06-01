package com.interview.lender.controller;

import com.interview.lender.dto.ResponseDto;
import com.interview.lender.dto.TransactionDto;
import com.interview.lender.services.TransactionService;
import com.interview.lender.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionDataControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionDataController transactionDataController;

    private ResponseDto successResponse;
    private ResponseDto failureResponse;



    @BeforeEach
    void setUp() {
        TransactionDto transactionDto = TestUtil.createTransactionDto();
        successResponse = TestUtil.createSuccessResponseDto("Transaction history data", List.of(transactionDto));
        failureResponse = TestUtil.createFailureResponseDto("Failed to retrieve transaction data", HttpStatus.INTERNAL_SERVER_ERROR);
    }



    @Test
    void getTransactionData_shouldReturnSuccessResponse_whenServiceSucceeds() {
        when(transactionService.getTransactionHistory(TestUtil.TEST_CUSTOMER_NUMBER)).thenReturn(successResponse);

        var response = transactionDataController.getTransactionData(TestUtil.TEST_CUSTOMER_NUMBER);

        assertValidResponse(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Transaction history data", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        verify(transactionService).getTransactionHistory(TestUtil.TEST_CUSTOMER_NUMBER);
    }



    @Test
    void getTransactionData_shouldReturnFailureResponse_whenServiceFails() {
        when(transactionService.getTransactionHistory(TestUtil.TEST_CUSTOMER_NUMBER)).thenReturn(failureResponse);

        var response = transactionDataController.getTransactionData(TestUtil.TEST_CUSTOMER_NUMBER);

        assertValidResponse(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Failed to retrieve transaction data", response.getBody().getMessage());
        verify(transactionService).getTransactionHistory(TestUtil.TEST_CUSTOMER_NUMBER);
    }



    @Test
    void getTransactionData_shouldCallServiceWithCorrectCustomerNumber() {
        when(transactionService.getTransactionHistory(TestUtil.TEST_CUSTOMER_NUMBER_2)).thenReturn(successResponse);

        transactionDataController.getTransactionData(TestUtil.TEST_CUSTOMER_NUMBER_2);

        verify(transactionService).getTransactionHistory(TestUtil.TEST_CUSTOMER_NUMBER_2);
    }



    @Test
    void getTransactionData_shouldHandleEmptyTransactionList() {
        ResponseDto emptyResponse = TestUtil.createSuccessResponseDto("Transaction history data", List.of());
        when(transactionService.getTransactionHistory(TestUtil.TEST_CUSTOMER_NUMBER)).thenReturn(emptyResponse);

        var response = transactionDataController.getTransactionData(TestUtil.TEST_CUSTOMER_NUMBER);

        assertValidResponse(response);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Transaction history data", response.getBody().getMessage());
        verify(transactionService).getTransactionHistory(TestUtil.TEST_CUSTOMER_NUMBER);
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

