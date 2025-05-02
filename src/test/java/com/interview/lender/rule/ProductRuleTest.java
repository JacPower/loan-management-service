package com.interview.lender.rule;

import com.interview.lender.dto.FeeDto;
import com.interview.lender.dto.GlobalFiltersDto;
import com.interview.lender.dto.ProductDto;
import com.interview.lender.dto.ResponseDto;
import com.interview.lender.enums.BusinessRulesOperations;
import com.interview.lender.exception.BusinessException;
import com.interview.lender.services.ProductProcessor;
import com.interview.lender.util.TestUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.interview.lender.enums.BusinessRules.PRODUCT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductRuleTest {

    @Mock
    private ProductProcessor productProcessor;

    @InjectMocks
    private ProductRule productRule;

    private ProductDto productDto;
    private FeeDto feeDto;
    private GlobalFiltersDto filtersDto;
    private ResponseDto responseDto;



    @BeforeEach
    void setUp() {
        productDto = TestUtilities.createProductDto();
        feeDto = TestUtilities.createFeeDto();
        filtersDto = GlobalFiltersDto.builder().build();
        responseDto = TestUtilities.createSuccessResponse("Success", null);
    }



    @Test
    void canExecute_shouldReturnTrue_whenValidBusinessRule() {
        boolean result = productRule.canExecute(PRODUCT);

        assertTrue(result);
    }



    @Test
    void canExecute_shouldReturnFalse_whenInvalidBusinessRule() {
        boolean result = productRule.canExecute("INVALID_RULE");

        assertFalse(result);
    }



    @Test
    void execute_shouldCreateProduct_whenCreateProductOperation() {
        when(productProcessor.createProduct(any(ProductDto.class))).thenReturn(responseDto);

        ResponseDto result = productRule.execute(BusinessRulesOperations.CREATE_PRODUCT, productDto);

        assertEquals(responseDto, result);
        verify(productProcessor).createProduct(productDto);
    }



    @Test
    void execute_shouldUpdateProduct_whenUpdateProductOperation() {
        when(productProcessor.updateProduct(any(ProductDto.class))).thenReturn(responseDto);

        ResponseDto result = productRule.execute(BusinessRulesOperations.UPDATE_PRODUCT, productDto);

        assertEquals(responseDto, result);
    }



    @Test
    void execute_shouldGetProduct_whenGetProductOperation() {
        when(productProcessor.getProductById(any(GlobalFiltersDto.class))).thenReturn(responseDto);

        ResponseDto result = productRule.execute(BusinessRulesOperations.GET_PRODUCT, filtersDto);

        assertEquals(responseDto, result);
    }



    @Test
    void execute_shouldGetAllProducts_whenGetAllProductsOperation() {
        when(productProcessor.getAllProducts(any(GlobalFiltersDto.class))).thenReturn(responseDto);

        ResponseDto result = productRule.execute(BusinessRulesOperations.GET_ALL_PRODUCTS, filtersDto);

        assertEquals(responseDto, result);
    }



    @Test
    void execute_shouldCreateFee_whenCreateFeeOperation() {
        when(productProcessor.createFee(any(FeeDto.class))).thenReturn(responseDto);

        ResponseDto result = productRule.execute(BusinessRulesOperations.CREATE_FEE, feeDto);

        assertEquals(responseDto, result);
    }



    @Test
    void execute_shouldCreateProductFee_whenCreateProductFeeOperation() {
        when(productProcessor.createProductFee(any(GlobalFiltersDto.class))).thenReturn(responseDto);

        ResponseDto result = productRule.execute(BusinessRulesOperations.CREATE_PRODUCT_FEE, filtersDto);

        assertEquals(responseDto, result);
    }



    @Test
    void execute_shouldUpdateFee_whenUpdateFeeOperation() {
        when(productProcessor.updateFee(any(FeeDto.class))).thenReturn(responseDto);

        ResponseDto result = productRule.execute(BusinessRulesOperations.UPDATE_FEE, feeDto);

        assertEquals(responseDto, result);
    }



    @Test
    void execute_shouldGetFee_whenGetFeeOperation() {
        when(productProcessor.getFeeById(any(GlobalFiltersDto.class))).thenReturn(responseDto);

        ResponseDto result = productRule.execute(BusinessRulesOperations.GET_FEE, filtersDto);

        assertEquals(responseDto, result);
    }



    @Test
    void execute_shouldGetAllFees_whenGetAllFeesOperation() {
        when(productProcessor.getAllFees(any(GlobalFiltersDto.class))).thenReturn(responseDto);

        ResponseDto result = productRule.execute(BusinessRulesOperations.GET_ALL_FEES, filtersDto);

        assertEquals(responseDto, result);
    }



    @Test
    void execute_shouldThrowException_whenUnknownOperation() {
        assertThrows(BusinessException.class, () -> {
            productRule.execute("UNKNOWN_OPERATION", productDto);
        });
    }
}
