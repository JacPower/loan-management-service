package com.interview.lender.dto;
import com.interview.lender.enums.NotificationChannel;
import com.interview.lender.enums.NotificationStatus;
import com.interview.lender.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private UUID id;
    private UUID customerId;
    private UUID loanId;
    private NotificationType type;
    private String content;
    private NotificationChannel channel;
    private NotificationStatus status;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
}
