package com.interview.lender.rule;

import com.interview.lender.dto.CustomerDto;
import com.interview.lender.dto.GlobalFiltersDto;
import com.interview.lender.dto.ResponseDto;
import com.interview.lender.enums.BusinessRulesOperations;
import com.interview.lender.exception.BusinessException;
import com.interview.lender.services.CustomerProcessor;
import com.interview.lender.util.TestUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.interview.lender.enums.BusinessRules.CUSTOMER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerRuleTest {

    @Mock
    private CustomerProcessor customerProcessor;

    @InjectMocks
    private CustomerRule customerRule;

    private CustomerDto customerDto;
    private GlobalFiltersDto filtersDto;
    private ResponseDto responseDto;



    @BeforeEach
    void setUp() {
        customerDto = TestUtilities.createCustomerDto();
        filtersDto = GlobalFiltersDto.builder().build();
        responseDto = TestUtilities.createSuccessResponse("Success", null);
    }



    @Test
    void canExecute_shouldReturnTrue_whenValidBusinessRule() {
        boolean result = customerRule.canExecute(CUSTOMER);

        assertTrue(result);
    }



    @Test
    void canExecute_shouldReturnFalse_whenInvalidBusinessRule() {
        boolean result = customerRule.canExecute("INVALID_RULE");

        assertFalse(result);
    }



    @Test
    void execute_shouldCreateCustomer_whenCreateOperation() {
        when(customerProcessor.createCustomer(any(CustomerDto.class))).thenReturn(responseDto);

        ResponseDto result = customerRule.execute(BusinessRulesOperations.CREATE_CUSTOMER, customerDto);

        assertEquals(responseDto, result);
    }



    @Test
    void execute_shouldUpdateCustomer_whenUpdateOperation() {
        when(customerProcessor.updateCustomer(any(CustomerDto.class))).thenReturn(responseDto);

        ResponseDto result = customerRule.execute(BusinessRulesOperations.UPDATE_CUSTOMER, customerDto);

        assertEquals(responseDto, result);
    }



    @Test
    void execute_shouldGetCustomer_whenGetOperation() {
        when(customerProcessor.getCustomerById(any(GlobalFiltersDto.class))).thenReturn(responseDto);

        ResponseDto result = customerRule.execute(BusinessRulesOperations.GET_CUSTOMER, filtersDto);

        assertEquals(responseDto, result);
    }



    @Test
    void execute_shouldGetAllCustomers_whenGetAllOperation() {
        when(customerProcessor.getAllCustomers(any(GlobalFiltersDto.class))).thenReturn(responseDto);

        ResponseDto result = customerRule.execute(BusinessRulesOperations.GET_ALL_CUSTOMERS, filtersDto);

        assertEquals(responseDto, result);
    }



    @Test
    void execute_shouldThrowException_whenUnknownOperation() {
        assertThrows(BusinessException.class, () -> {
            customerRule.execute("UNKNOWN_OPERATION", customerDto);
        });
    }
}
