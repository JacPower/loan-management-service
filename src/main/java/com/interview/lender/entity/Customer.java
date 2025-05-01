package com.interview.lender.entity;

import com.interview.lender.enums.BillingCycle;
import com.interview.lender.enums.Gender;
import com.interview.lender.enums.CustomerStatus;
import com.interview.lender.enums.NotificationChannel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer extends BaseEntity {
    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String middleName;

    @Column(nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false, updatable = false)
    private LocalDate dob;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false, unique = true)
    private String idNumber;

    @Column(nullable = false)
    private Double loanLimit;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillingCycle billingCycle = BillingCycle.INDIVIDUAL;

    @Column
    private Integer preferredBillingDay;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerStatus accountStatus;

    @Column(nullable = false, unique = true)
    private String description;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "customer_notification_channels", joinColumns = @JoinColumn(name = "customer_id"))
    @Column(name = "channel")
    @Enumerated(EnumType.STRING)
    private Set<NotificationChannel> preferredChannels = new HashSet<>();
}
