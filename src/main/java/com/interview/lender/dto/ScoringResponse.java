package com.interview.lender.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoringResponse {
    private Long id;
    private String customerNumber;
    private Integer score;
    private BigDecimal limitAmount;
    private String exclusion;
    private String exclusionReason;
}
