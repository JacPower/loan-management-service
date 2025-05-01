package com.interview.lender.dto;

import com.interview.lender.enums.ApplicationTiming;
import com.interview.lender.enums.CalculationType;
import com.interview.lender.enums.FeeType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeDto {
    private UUID id;

    @NotBlank(message = "Fee name cannot be empty")
    @Size(max = 50, message = "Fee name cannot exceed 30 characters")
    private String feeName;

    @NotNull(message = "Fee type cannot be null")
    private FeeType feeType;

    @NotNull(message = "Calculation type cannot be null")
    private CalculationType calculationType;

    @NotNull(message = "Fee value cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Fee value cannot be negative")
    private Double feeValue;

    @NotNull(message = "Description cannot be null")
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @NotNull(message = "Active cannot be null")
    private Boolean isActive;

    @NotNull(message = "Application timing cannot be null")
    private ApplicationTiming applicationTiming;
}
