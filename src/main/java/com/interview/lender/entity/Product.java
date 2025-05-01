package com.interview.lender.entity;

import com.interview.lender.enums.ProductStatus;
import com.interview.lender.enums.TenureType;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {
    @Column( nullable = false, length = 50)
    private String productName;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column( nullable = false, length = 20)
    private TenureType tenureType;

    @Column(nullable = false)
    private Integer tenureValue;

    @Column(nullable = false)
    private Integer daysAfterDueForLateFee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus productStatus;

    @Column(nullable = false)
    private Boolean isFixedTerm;

    @Column(nullable = false)
    private Boolean isNotificationEnabled;

    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductFee> productFees = new HashSet<>();
}
