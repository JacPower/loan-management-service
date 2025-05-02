package com.interview.lender.controller;

import com.interview.lender.dto.GlobalFiltersDto;
import com.interview.lender.dto.ProductDto;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private RuleDelegator ruleDelegator;

    @InjectMocks
    private ProductController productController;

    private ProductDto productDto;
    private GlobalFiltersDto filtersDto;
    private ResponseDto responseDto;



    @BeforeEach
    void setUp() {
        productDto = TestUtilities.createProductDto();
        filtersDto = GlobalFiltersDto.builder()
                .page(0)
                .size(10)
                .build();
        responseDto = TestUtilities.createSuccessResponse("Success", null);
    }



    @Test
    void createProductFee_shouldReturnResponseEntity() {
        UUID productId = UUID.randomUUID();
        UUID feeId = UUID.randomUUID();
        when(ruleDelegator.routeRequest(eq(PRODUCT), eq(CREATE_PRODUCT_FEE), any(GlobalFiltersDto.class))).thenReturn(responseDto);

        ResponseEntity<ResponseDto> response = productController.createProductFee(productId, feeId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }



    @Test
    void getProduct_shouldReturnResponseEntity() {
        UUID productId = UUID.randomUUID();
        when(ruleDelegator.routeRequest(eq(PRODUCT), eq(GET_PRODUCT), any(GlobalFiltersDto.class))).thenReturn(responseDto);

        ResponseEntity<ResponseDto> response = productController.getProduct(productId, filtersDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }



    @Test
    void getAllProducts_shouldReturnResponseEntity() {
        when(ruleDelegator.routeRequest(eq(PRODUCT), eq(GET_ALL_PRODUCTS), any(GlobalFiltersDto.class))).thenReturn(responseDto);

        ResponseEntity<ResponseDto> response = productController.getAllProducts(filtersDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }



    @Test
    void updateProduct_shouldReturnResponseEntity() {
        UUID productId = UUID.randomUUID();
        when(ruleDelegator.routeRequest(eq(PRODUCT), eq(UPDATE_PRODUCT), any(ProductDto.class))).thenReturn(responseDto);

        ResponseEntity<ResponseDto> response = productController.updateProduct(productId, productDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }



    @Test
    void createProduct_shouldReturnFailureResponse_whenRuleDelegatorFails() {
        ResponseDto failureResponse = TestUtilities.createFailureResponse("Product creation failed");
        when(ruleDelegator.routeRequest(eq(PRODUCT), eq(CREATE_PRODUCT), any(ProductDto.class))).thenReturn(failureResponse);

        ResponseEntity<ResponseDto> response = productController.createProduct(productDto);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }
}
