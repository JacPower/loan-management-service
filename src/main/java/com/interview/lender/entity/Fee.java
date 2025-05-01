package com.interview.lender.entity;

import com.interview.lender.enums.ApplicationTiming;
import com.interview.lender.enums.CalculationType;
import com.interview.lender.enums.FeeType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fees")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Fee extends BaseEntity {
    @Column(nullable = false, length = 30)
    private String feeName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeeType feeType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CalculationType calculationType;

    @Column(nullable = false)
    private Double feeValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationTiming applicationTiming;

    @Column(nullable = false)
    private String description;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;
}
