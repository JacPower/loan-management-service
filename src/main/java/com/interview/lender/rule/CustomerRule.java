package com.interview.lender.rule;

import com.interview.lender.dto.ResponseDto;
import com.interview.lender.enums.BusinessRulesOperations;
import com.interview.lender.exception.BusinessException;
import com.interview.lender.dto.GlobalFiltersDto;
import com.interview.lender.dto.CustomerDto;
import com.interview.lender.services.CustomerProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.interview.lender.enums.BusinessRules.CUSTOMER;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerRule implements IRule {
    private final CustomerProcessor customerProcessor;



    @Override
    public boolean canExecute(Object businessRule) {
        return businessRule.equals(CUSTOMER);
    }



    @Override
    public ResponseDto execute(Object operation, Object requestDto) {
        return switch (BusinessRulesOperations.ofValue(operation)) {
            case CREATE_CUSTOMER -> customerProcessor.createCustomer((CustomerDto) requestDto);
            case UPDATE_CUSTOMER -> customerProcessor.updateCustomer((CustomerDto) requestDto);
            case GET_CUSTOMER -> customerProcessor.getCustomerById((GlobalFiltersDto) requestDto);
            case GET_ALL_CUSTOMERS -> customerProcessor.getAllCustomers((GlobalFiltersDto) requestDto);
            default -> {
                String message = "Customer operation not supported: " + operation;
                log.error(message);
                throw BusinessException.unknownOperation(message);
            }
        };
    }
}
