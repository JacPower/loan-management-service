package com.interview.lender.controller;

import com.interview.lender.dto.FeeDto;
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

import static com.interview.lender.enums.BusinessRules.PRODUCT;
import static com.interview.lender.enums.BusinessRulesOperations.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeeControllerTest {

    @Mock
    private RuleDelegator ruleDelegator;

    @InjectMocks
    private FeeController feeController;

    private FeeDto feeDto;
    private GlobalFiltersDto filtersDto;
    private ResponseDto responseDto;



    @BeforeEach
    void setUp() {
        feeDto = TestUtilities.createFeeDto();
        filtersDto = GlobalFiltersDto.builder()
                .page(0)
                .size(10)
                .build();
        responseDto = TestUtilities.createSuccessResponse("Success", null);
    }



    @Test
    void createFee_shouldReturnResponseEntity() {
        when(ruleDelegator.routeRequest(eq(PRODUCT), eq(CREATE_FEE), any(FeeDto.class))).thenReturn(responseDto);

        ResponseEntity<ResponseDto> response = feeController.createFee(feeDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        verify(ruleDelegator).routeRequest(PRODUCT, CREATE_FEE, feeDto);
    }



    @Test
    void getFee_shouldReturnResponseEntity() {
        UUID feeId = UUID.randomUUID();
        when(ruleDelegator.routeRequest(eq(PRODUCT), eq(GET_FEE), any(GlobalFiltersDto.class))).thenReturn(responseDto);

        ResponseEntity<ResponseDto> response = feeController.getFee(feeId, filtersDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }



    @Test
    void getAllFees_shouldReturnResponseEntity() {
        when(ruleDelegator.routeRequest(eq(PRODUCT), eq(GET_ALL_FEES), any(GlobalFiltersDto.class))).thenReturn(responseDto);

        ResponseEntity<ResponseDto> response = feeController.getAllFees(filtersDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }



    @Test
    void updateFee_shouldReturnResponseEntity() {
        UUID feeId = UUID.randomUUID();
        when(ruleDelegator.routeRequest(eq(PRODUCT), eq(UPDATE_FEE), any(FeeDto.class))).thenReturn(responseDto);

        ResponseEntity<ResponseDto> response = feeController.updateFee(feeId, feeDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }



    @Test
    void createFee_shouldReturnFailureResponse_whenRuleDelegatorFails() {
        ResponseDto failureResponse = TestUtilities.createFailureResponse("Fee creation failed");
        when(ruleDelegator.routeRequest(eq(PRODUCT), eq(CREATE_FEE), any(FeeDto.class))).thenReturn(failureResponse);

        ResponseEntity<ResponseDto> response = feeController.createFee(feeDto);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }
}
