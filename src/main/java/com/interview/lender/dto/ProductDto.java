package com.interview.lender.dto;

import com.interview.lender.enums.ProductStatus;
import com.interview.lender.enums.TenureType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private UUID id;

    @NotBlank(message = "Product name cannot be empty")
    @NotNull(message = "Product name cannot be null")
    @Size(max = 50, message = "Product name cannot exceed 50 characters")
    private String productName;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @NotNull(message = "Tenure type cannot be null")
    private TenureType tenureType;

    @NotNull(message = "Tenure value cannot be null")
    @Positive(message = "Tenure value must be positive")
    private Integer tenureValue;

    @NotNull(message = "Days after due for late fee cannot be null")
    @PositiveOrZero(message = "Days after due for late fee cannot be negative")
    private Integer daysAfterDueForLateFee;

    @NotNull(message = "Product status cannot be null")
    private ProductStatus productStatus;

    @NotNull(message = "Fixed term flag cannot be null")
    private Boolean isFixedTerm;

    @NotNull(message = "Notification enabled flag cannot be null")
    private Boolean isNotificationEnabled;
}
