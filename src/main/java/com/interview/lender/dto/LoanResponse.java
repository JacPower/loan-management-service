package com.interview.lender.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanResponse {
    private Long loanId;
    private String customerNumber;
    private BigDecimal requestedAmount;
    private String status;
    private LocalDateTime applicationDate;
}
