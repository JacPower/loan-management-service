package com.interview.lender.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_fees")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductFee extends BaseEntity {
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fee_id", nullable = false)
    private Fee fee;
}
