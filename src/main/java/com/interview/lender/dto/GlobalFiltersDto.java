package com.interview.lender.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlobalFiltersDto {
    @PastOrPresent(message = "Start date must be a past or present date")
    @Builder.Default
    private LocalDate startDate = LocalDate.now().minusDays(30);

    @PastOrPresent(message = "End date must be a past or present date")
    @Builder.Default
    private LocalDate endDate = LocalDate.now();

    @Min(value = 0, message = "Page number must be 0 or greater")
    @Builder.Default
    private int page = 0;

    @Min(value = 1, message = "Size must be at least 1")
    @Max(value = 100, message = "Size cannot exceed 100")
    @Builder.Default
    private int size = 10;

    private UUID id;
    private UUID productId;
    private UUID feeId;
}
