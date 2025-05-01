package com.interview.lender.controller;

import com.interview.lender.dto.GlobalFiltersDto;
import com.interview.lender.dto.ProductDto;
import com.interview.lender.dto.ResponseDto;
import com.interview.lender.rule.RuleDelegator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.interview.lender.enums.BusinessRules.PRODUCT;
import static com.interview.lender.enums.BusinessRulesOperations.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final RuleDelegator ruleDelegator;



    @PostMapping
    public ResponseEntity<ResponseDto> createProduct(@Valid @RequestBody ProductDto requestDto) {
        ResponseDto response = ruleDelegator.routeRequest(PRODUCT, CREATE_PRODUCT, requestDto);

        return new ResponseEntity<>(response, response.getHttpStatus());
    }



    @PostMapping("/{productId}/fees/{feeId}")
    public ResponseEntity<ResponseDto> createProductFee(@PathVariable UUID productId, @PathVariable UUID feeId) {
        var globalFiltersDto = GlobalFiltersDto.builder()
                .productId(productId)
                .feeId(feeId)
                .build();
        ResponseDto response = ruleDelegator.routeRequest(PRODUCT, CREATE_PRODUCT_FEE, globalFiltersDto);

        return new ResponseEntity<>(response, response.getHttpStatus());
    }



    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto> getProduct(@PathVariable UUID id, @Valid GlobalFiltersDto filtersDto) {
        filtersDto.setId(id);
        ResponseDto response = ruleDelegator.routeRequest(PRODUCT, GET_PRODUCT, filtersDto);

        return new ResponseEntity<>(response, response.getHttpStatus());
    }



    @GetMapping
    public ResponseEntity<ResponseDto> getAllProducts(@Valid GlobalFiltersDto filtersDto) {
        ResponseDto response = ruleDelegator.routeRequest(PRODUCT, GET_ALL_PRODUCTS, filtersDto);

        return new ResponseEntity<>(response, response.getHttpStatus());
    }



    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto> updateProduct(@PathVariable UUID id, @Valid @RequestBody ProductDto requestDto) {
        requestDto.setId(id);
        ResponseDto response = ruleDelegator.routeRequest(PRODUCT, UPDATE_PRODUCT, requestDto);

        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}
