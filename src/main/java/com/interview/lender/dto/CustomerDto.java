package com.interview.lender.dto;

import com.interview.lender.enums.BillingCycle;
import com.interview.lender.enums.Gender;
import com.interview.lender.enums.NotificationChannel;
import com.interview.lender.enums.CustomerStatus;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Builder
@Data
public class CustomerDto {
    UUID id;
    @NotNull(message = "Preferred billing day cannot be null")
    Integer preferredBillingDay;
    @NotBlank(message = "First name cannot be null")
    @NotBlank(message = "First name cannot be blank")
    private String firstName;
    @NotNull(message = "Last name cannot be null")
    @NotBlank(message = "Last name cannot be blank")
    private String middleName;
    @NotNull(message = "Last name cannot be null")
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;
    @NotNull(message = "Gender cannot be null")
    private Gender gender;
    @NotNull(message = "Email cannot be null")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    private String email;
    @NotNull(message = "phone number cannot be null")
    @NotBlank(message = "phone")
    @Pattern(regexp = "^\\d{12}$", message = "Phone number must be 12 digits")
    private String phone;
    @NotNull(message = "phone number cannot be null")
    @NotBlank(message = "phone")
    @Size(min = 6, max = 20, message = "Id number should contain 6-20 characters")
    // this includes alien number, passport number and national id number
    private String idNumber;
    @NotNull(message = "Date of birth cannot be null")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dob;
    @NotNull(message = "Loan limit cannot be null")
    @Positive(message = "Loan limit must be positive")
    private Double loanLimit;
    @NotNull(message = "Description cannot be null")
    @NotBlank(message = "Description cannot be blank")
    private String description;
    @NotNull(message = "Billing cycle cannot be null")
    private BillingCycle billingCycle;

    private Set<NotificationChannel> preferredChannels;

    @Builder.Default
    private CustomerStatus status = CustomerStatus.ACTIVE;
}
