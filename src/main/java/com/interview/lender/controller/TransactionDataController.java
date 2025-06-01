package com.interview.lender.controller;

import com.interview.lender.dto.ResponseDto;
import com.interview.lender.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transaction-data")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transaction Data", description = "APIs for transaction data access (for scoring service)")
public class TransactionDataController {

    private final TransactionService transactionService;



    @GetMapping("/{customerNumber}")
    @Operation(summary = "Get customer transaction data", description = "Retrieve transaction history for a customer (used by scoring service)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction data retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseDto> getTransactionData(@Parameter(description = "Customer number", required = true) @PathVariable String customerNumber) {
        var response = transactionService.getTransactionHistory(customerNumber);

        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }
}
