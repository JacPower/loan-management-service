package com.interview.lender.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.interview.lender.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class PaymentDto {
    @NotNull(message = "Loan code cannot be null")
    @NotBlank(message = "Loan code cannot be blank")
    private String loanCode;

    @NotNull(message = "Payment amount cannot be null")
    @Positive(message = "Payment amount must be positive")
    private Double amount;

    @NotNull(message = "Payment cannot be null")
    private PaymentMethod paymentMethod;

    @NotNull(message = "Payment code cannot be null")
    @NotBlank(message = "Payment code cannot be blank")
    private String paymentCode;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @Builder.Default
    private LocalDateTime paymentDate = LocalDateTime.now();
}
