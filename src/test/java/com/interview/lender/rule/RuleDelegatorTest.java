package com.interview.lender.rule;

import com.interview.lender.dto.ResponseDto;
import com.interview.lender.exception.BusinessException;
import com.interview.lender.util.TestUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.interview.lender.enums.BusinessRules.*;
import static com.interview.lender.enums.BusinessRulesOperations.CREATE_CUSTOMER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RuleDelegatorTest {

    @Mock
    private IRule customerRule;
    @Mock
    private IRule productRule;
    @Mock
    private IRule loanRule;

    @InjectMocks
    private RuleDelegator ruleDelegator;

    private ResponseDto responseDto;
    private Object requestPayload;



    @BeforeEach
    void setUp() {
        responseDto = TestUtilities.createSuccessResponse("Success", null);
        requestPayload = new Object();

        ruleDelegator = new RuleDelegator(List.of(customerRule, productRule, loanRule));
    }



    @Test
    void routeRequest_shouldDelegateToCorrectRule_whenCustomerRule() {
        when(customerRule.canExecute(CUSTOMER)).thenReturn(true);
        when(customerRule.execute(CREATE_CUSTOMER, requestPayload)).thenReturn(responseDto);

        ResponseDto result = ruleDelegator.routeRequest(CUSTOMER, CREATE_CUSTOMER, requestPayload);

        assertNotNull(result);
        assertEquals(responseDto, result);
    }



    @Test
    void routeRequest_shouldDelegateToCorrectRule_whenProductRule() {
        when(customerRule.canExecute(PRODUCT)).thenReturn(false);
        when(productRule.canExecute(PRODUCT)).thenReturn(true);
        when(productRule.execute(CREATE_CUSTOMER, requestPayload)).thenReturn(responseDto);

        ResponseDto result = ruleDelegator.routeRequest(PRODUCT, CREATE_CUSTOMER, requestPayload);

        assertNotNull(result);
        assertEquals(responseDto, result);
    }



    @Test
    void routeRequest_shouldDelegateToCorrectRule_whenLoanRule() {
        when(customerRule.canExecute(LOAN)).thenReturn(false);
        when(productRule.canExecute(LOAN)).thenReturn(false);
        when(loanRule.canExecute(LOAN)).thenReturn(true);
        when(loanRule.execute(CREATE_CUSTOMER, requestPayload)).thenReturn(responseDto);

        ResponseDto result = ruleDelegator.routeRequest(LOAN, CREATE_CUSTOMER, requestPayload);

        assertNotNull(result);
        assertEquals(responseDto, result);
    }



    @Test
    void routeRequest_shouldThrowException_whenNoRuleCanHandle() {
        when(customerRule.canExecute("UNKNOWN")).thenReturn(false);
        when(productRule.canExecute("UNKNOWN")).thenReturn(false);
        when(loanRule.canExecute("UNKNOWN")).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> ruleDelegator.routeRequest("UNKNOWN", CREATE_CUSTOMER, requestPayload));

        assertTrue(exception.getMessage().contains("No rule found for request type"));
    }



    @Test
    void routeRequest_shouldCallCanExecuteOnAllRules_untilMatch() {
        when(customerRule.canExecute(LOAN)).thenReturn(false);
        when(productRule.canExecute(LOAN)).thenReturn(false);
        when(loanRule.canExecute(LOAN)).thenReturn(true);
        when(loanRule.execute(CREATE_CUSTOMER, requestPayload)).thenReturn(responseDto);

        ResponseDto result = ruleDelegator.routeRequest(LOAN, CREATE_CUSTOMER, requestPayload);

        assertNotNull(result);
        verify(customerRule).canExecute(LOAN);
        verify(productRule).canExecute(LOAN);
        verify(loanRule).canExecute(LOAN);
    }
}
