package com.interview.lender.controller;

import com.interview.lender.dto.CustomerDto;
import com.interview.lender.dto.GlobalFiltersDto;
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

import static com.interview.lender.enums.BusinessRules.CUSTOMER;
import static com.interview.lender.enums.BusinessRulesOperations.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private RuleDelegator ruleDelegator;

    @InjectMocks
    private CustomerController customerController;

    private CustomerDto customerDto;
    private GlobalFiltersDto filtersDto;
    private ResponseDto responseDto;



    @BeforeEach
    void setUp() {
        customerDto = TestUtilities.createCustomerDto();
        filtersDto = GlobalFiltersDto.builder()
                .page(0)
                .size(10)
                .build();
        responseDto = TestUtilities.createSuccessResponse("Success", null);
    }



    @Test
    void createCustomer_shouldReturnResponseEntity() {
        when(ruleDelegator.routeRequest(eq(CUSTOMER), eq(CREATE_CUSTOMER), any(CustomerDto.class))).thenReturn(responseDto);

        ResponseEntity<ResponseDto> response = customerController.createCustomer(customerDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        verify(ruleDelegator).routeRequest(CUSTOMER, CREATE_CUSTOMER, customerDto);
    }



    @Test
    void getCustomer_shouldReturnResponseEntity() {
        UUID customerId = UUID.randomUUID();
        when(ruleDelegator.routeRequest(eq(CUSTOMER), eq(GET_CUSTOMER), any(GlobalFiltersDto.class))).thenReturn(responseDto);

        ResponseEntity<ResponseDto> response = customerController.getCustomer(customerId, filtersDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }



    @Test
    void getAllCustomers_shouldReturnResponseEntity() {
        when(ruleDelegator.routeRequest(eq(CUSTOMER), eq(GET_ALL_CUSTOMERS), any(GlobalFiltersDto.class))).thenReturn(responseDto);

        ResponseEntity<ResponseDto> response = customerController.getAllCustomers(filtersDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }



    @Test
    void updateCustomer_shouldReturnResponseEntity() {
        UUID customerId = UUID.randomUUID();
        when(ruleDelegator.routeRequest(eq(CUSTOMER), eq(UPDATE_CUSTOMER), any(CustomerDto.class))).thenReturn(responseDto);

        ResponseEntity<ResponseDto> response = customerController.updateCustomer(customerId, customerDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }



    @Test
    void createCustomer_shouldReturnFailureResponse_whenRuleDelegatorFails() {
        ResponseDto failureResponse = TestUtilities.createFailureResponse("Customer creation failed");
        when(ruleDelegator.routeRequest(eq(CUSTOMER), eq(CREATE_CUSTOMER), any(CustomerDto.class))).thenReturn(failureResponse);

        ResponseEntity<ResponseDto> response = customerController.createCustomer(customerDto);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }



    @Test
    void updateCustomer_shouldSetCorrectHttpStatus() {
        UUID customerId = UUID.randomUUID();
        responseDto.setHttpStatus(HttpStatus.ACCEPTED);
        when(ruleDelegator.routeRequest(eq(CUSTOMER), eq(UPDATE_CUSTOMER), any(CustomerDto.class))).thenReturn(responseDto);

        ResponseEntity<ResponseDto> response = customerController.updateCustomer(customerId, customerDto);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }
}
