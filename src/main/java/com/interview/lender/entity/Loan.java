package com.interview.lender.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.interview.lender.enums.LoanStatus;
import com.interview.lender.enums.LoanStructureType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "loans")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Loan extends BaseEntity {
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private Double principalAmount;

    @Column(nullable = false)
    private LocalDate disbursementDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus loanStatus;

    @Column(nullable = false)
    private String loanCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStructureType structureType;

    @Column(nullable = false)
    private Double currentBalance;

    @Builder.Default
    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LoanFee> appliedFees = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Installment> installments = new ArrayList<>();

    @Column()
    private String description;

    @Transient
    private double amountPaid = 0.00;

    @Transient
    private double lateFeeApplied = 0.00;
}
