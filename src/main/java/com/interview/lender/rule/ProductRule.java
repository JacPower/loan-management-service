package com.interview.lender.rule;

import com.interview.lender.dto.ResponseDto;
import com.interview.lender.enums.BusinessRulesOperations;
import com.interview.lender.exception.BusinessException;
import com.interview.lender.dto.FeeDto;
import com.interview.lender.dto.GlobalFiltersDto;
import com.interview.lender.dto.ProductDto;
import com.interview.lender.services.ProductProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.interview.lender.enums.BusinessRules.PRODUCT;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductRule implements IRule {
    private final ProductProcessor productProcessor;



    @Override
    public boolean canExecute(Object businessRule) {
        return businessRule.equals(PRODUCT);
    }



    @Override
    public ResponseDto execute(Object operation, Object requestDto) {
        return switch (BusinessRulesOperations.ofValue(operation)) {
            case CREATE_PRODUCT -> productProcessor.createProduct((ProductDto) requestDto);
            case UPDATE_PRODUCT -> productProcessor.updateProduct((ProductDto) requestDto);
            case GET_PRODUCT -> productProcessor.getProductById((GlobalFiltersDto) requestDto);
            case GET_ALL_PRODUCTS -> productProcessor.getAllProducts((GlobalFiltersDto) requestDto);
            case CREATE_FEE -> productProcessor.createFee((FeeDto) requestDto);
            case CREATE_PRODUCT_FEE -> productProcessor.createProductFee((GlobalFiltersDto) requestDto);
            case UPDATE_FEE -> productProcessor.updateFee((FeeDto) requestDto);
            case GET_FEE -> productProcessor.getFeeById((GlobalFiltersDto) requestDto);
            case GET_ALL_FEES -> productProcessor.getAllFees((GlobalFiltersDto) requestDto);
            default -> {
                String message = "Product operation not supported: " + operation;
                log.error(message);
                throw BusinessException.unknownOperation(message);
            }
        };
    }
}
