package com.interview.lender.controller;

import com.interview.lender.dto.FeeDto;
import com.interview.lender.dto.GlobalFiltersDto;
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
@RequestMapping("/fees")
@RequiredArgsConstructor
public class FeeController {

    private final RuleDelegator ruleDelegator;



    @PostMapping
    public ResponseEntity<ResponseDto> createProductFee(@Valid @RequestBody FeeDto requestDto) {
        ResponseDto response = ruleDelegator.routeRequest(PRODUCT, CREATE_FEE, requestDto);

        return new ResponseEntity<>(response, response.getHttpStatus());
    }



    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto> getFee(@PathVariable UUID id, @Valid GlobalFiltersDto filtersDto) {
        filtersDto.setId(id);
        ResponseDto response = ruleDelegator.routeRequest(PRODUCT, GET_FEE, filtersDto);

        return new ResponseEntity<>(response, response.getHttpStatus());
    }



    @GetMapping
    public ResponseEntity<ResponseDto> getAllFees(@Valid GlobalFiltersDto filtersDto) {
        ResponseDto response = ruleDelegator.routeRequest(PRODUCT, GET_ALL_FEES, filtersDto);

        return new ResponseEntity<>(response, response.getHttpStatus());
    }



    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto> updateFee(@PathVariable UUID id, @Valid @RequestBody FeeDto requestDto) {
        requestDto.setId(id);
        ResponseDto response = ruleDelegator.routeRequest(PRODUCT, UPDATE_FEE, requestDto);

        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}
