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
public class LoanStatusResponse {
    private Long loanId;
    private String customerNumber;
    private BigDecimal requestedAmount;
    private BigDecimal approvedAmount;
    private String status;
    private Integer creditScore;
    private BigDecimal creditLimit;
    private String exclusion;
    private String exclusionReason;
    private LocalDateTime applicationDate;
    private LocalDateTime approvalDate;
}
