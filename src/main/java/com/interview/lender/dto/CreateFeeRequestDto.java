package com.interview.lender.dto;

import com.interview.lender.enums.ApplicationTiming;
import com.interview.lender.enums.CalculationType;
import com.interview.lender.enums.FeeType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class CreateFeeRequestDto {
    @NotBlank(message = "Fee name is required")
    private String name;

    @NotNull(message = "Fee type is required")
    private FeeType type;

    @NotNull(message = "Calculation type is required")
    private CalculationType calculationType;

    @NotNull(message = "Fee value is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Fee value must be positive")
    private double value;

    @NotNull(message = "Application timing is required")
    private ApplicationTiming applicationTiming;
}
