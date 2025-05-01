package com.interview.lender.controller;

import com.interview.lender.dto.CustomerDto;
import com.interview.lender.dto.GlobalFiltersDto;
import com.interview.lender.dto.ResponseDto;
import com.interview.lender.rule.RuleDelegator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.interview.lender.enums.BusinessRules.CUSTOMER;
import static com.interview.lender.enums.BusinessRulesOperations.*;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final RuleDelegator ruleDelegator;



    @PostMapping
    public ResponseEntity<ResponseDto> createCustomer(@Valid @RequestBody CustomerDto requestDto) {
        ResponseDto response = ruleDelegator.routeRequest(CUSTOMER, CREATE_CUSTOMER, requestDto);

        return new ResponseEntity<>(response, response.getHttpStatus());
    }



    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto> getCustomer(@PathVariable UUID id, @Valid GlobalFiltersDto filtersDto) {
        filtersDto.setId(id);
        ResponseDto response = ruleDelegator.routeRequest(CUSTOMER, GET_CUSTOMER, filtersDto);

        return new ResponseEntity<>(response, response.getHttpStatus());
    }



    @GetMapping
    public ResponseEntity<ResponseDto> getAllCustomers(@Valid GlobalFiltersDto filtersDto) {
        ResponseDto response = ruleDelegator.routeRequest(CUSTOMER, GET_ALL_CUSTOMERS, filtersDto);

        return new ResponseEntity<>(response, response.getHttpStatus());
    }



    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto> updateCustomer(@PathVariable UUID id, @Valid @RequestBody CustomerDto requestDto) {
        requestDto.setId(id);
        ResponseDto response = ruleDelegator.routeRequest(CUSTOMER, UPDATE_CUSTOMER, requestDto);

        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}
