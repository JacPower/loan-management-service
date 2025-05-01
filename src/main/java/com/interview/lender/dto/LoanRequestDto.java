package com.interview.lender.dto;

import com.interview.lender.enums.LoanStructureType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequestDto {

    @NotNull(message = "Customer ID cannot be null")
    private UUID customerId;

    @NotNull(message = "Product ID cannot be null")
    private UUID productId;

    @NotNull(message = "Principal amount is required")
    @DecimalMin(value = "100.00", message = "Principal amount must be greater than 100")
    private Double principalAmount;

    @NotNull(message = "Loan structure type is required")
    private LoanStructureType structureType;

    @NotNull(message = "Description cannot be null")
    @NotBlank(message = "Description cannot be empty")
    private String description;

    private LocalDate disbursementDate = LocalDate.now();
}
