package com.interview.lender.controller;

import com.interview.lender.dto.*;
import com.interview.lender.services.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Loan Management", description = "APIs for loan management operations")
@SecurityRequirement(name = "basicAuth")
public class LoanController {

    private final LoanService loanService;



    @PostMapping("/subscribe")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Subscribe customer to lending service", description = "Register a customer for loan services by validating their details through KYC")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer subscribed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Customer not authenticated"),
            @ApiResponse(responseCode = "404", description = "Customer not found in KYC system"),
            @ApiResponse(responseCode = "409", description = "Customer subscription already exist"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseDto> subscribeCustomer(@Valid @RequestBody SubscriptionRequest request) {
        var response = loanService.subscribeCustomer(request);

        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }



    @PostMapping("/request")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Request a loan", description = "Submit a loan application for a subscribed customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Loan request submitted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or customer has ongoing loan"),
            @ApiResponse(responseCode = "404", description = "Customer not subscribed"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseDto> requestLoan(@Valid @RequestBody LoanRequest request) {
        var response = loanService.requestLoan(request);

        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }



    @GetMapping("/status/{customerNumber}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get loan status", description = "Retrieve the current status of the most recent loan for a customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan status retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No loan found for customer"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseDto> getLoanStatus(@Parameter(description = "Customer number", required = true) @PathVariable String customerNumber) {
        var response = loanService.getLoanStatus(customerNumber);

        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }
}