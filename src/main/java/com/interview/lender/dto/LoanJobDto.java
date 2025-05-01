package com.interview.lender.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class LoanJobDto {
    private LocalDate executionDate;
    private Integer reminderDays;
    private Integer overdueThreshold;
    private Integer defaultThresholdDays;
    private Integer writeOffThresholdDays;
    private String reportType;
}
