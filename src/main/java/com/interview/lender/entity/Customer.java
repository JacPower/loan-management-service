package com.interview.lender.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String customerNumber;

    private String firstName;
    private String lastName;
    private String middleName;
    private String phoneNumber;
    private String email;
    private String idNumber;
    private String idNumberType;
    private Double monthlyIncome;
    private String gender;
    private Boolean isActive;
    private LocalDateTime registrationDate;
    private String scoringToken;
}
