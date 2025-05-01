package com.interview.lender.entity;

import com.interview.lender.enums.NotificationChannel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customer_notification_channels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerNotificationChannel extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    private NotificationChannel channel;

    @Column(nullable = false)
    private Boolean isActive;
}
