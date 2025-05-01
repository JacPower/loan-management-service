package com.interview.lender.controller;

import com.interview.lender.dto.GlobalFiltersDto;
import com.interview.lender.dto.LoanRequestDto;
import com.interview.lender.dto.PaymentDto;
import com.interview.lender.dto.ResponseDto;
import com.interview.lender.rule.RuleDelegator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.interview.lender.enums.BusinessRules.LOAN;
import static com.interview.lender.enums.BusinessRulesOperations.*;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
public class LoanController {

    private final RuleDelegator ruleDelegator;



    @PostMapping
    public ResponseEntity<ResponseDto> createLoan(@Valid @RequestBody LoanRequestDto requestDto) {
        ResponseDto response = ruleDelegator.routeRequest(LOAN, CREATE_LOAN, requestDto);

        return new ResponseEntity<>(response, response.getHttpStatus());
    }



    @PostMapping("/pay")
    public ResponseEntity<ResponseDto> payLoan(@Valid @RequestBody PaymentDto requestDto) {
        ResponseDto response = ruleDelegator.routeRequest(LOAN, PAY_LOAN, requestDto);

        return new ResponseEntity<>(response, response.getHttpStatus());
    }



    @GetMapping
    public ResponseEntity<ResponseDto> getAllLoans(@Valid GlobalFiltersDto filtersDto) {
        ResponseDto response = ruleDelegator.routeRequest(LOAN, GET_ALL_LOANS, filtersDto);

        return new ResponseEntity<>(response, response.getHttpStatus());
    }



    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto> getLoan(@PathVariable UUID id, @Valid GlobalFiltersDto filtersDto) {
        filtersDto.setId(id);
        ResponseDto response = ruleDelegator.routeRequest(LOAN, GET_LOAN, filtersDto);

        return new ResponseEntity<>(response, response.getHttpStatus());
    }



    @GetMapping("/{id}/payments")
    public ResponseEntity<ResponseDto> getLoanPayments(@PathVariable UUID id, @Valid GlobalFiltersDto filtersDto) {
        filtersDto.setId(id);
        ResponseDto response = ruleDelegator.routeRequest(LOAN, GET_LOAN_PAYMENTS, filtersDto);

        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}
