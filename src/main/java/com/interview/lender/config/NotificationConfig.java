package com.interview.lender.config;

import com.interview.lender.enums.NotificationChannel;
import com.interview.lender.enums.NotificationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class NotificationConfig {
    // Default notification channel
    @Value("${notification.default.channel}")
    private String defaultChannelName;

    // Global notification enabled flags
    @Value("${notification.enabled.payment_reminder}")
    private boolean paymentReminderEnabled;

    @Value("${notification.enabled.installment_reminder}")
    private boolean installmentReminderEnabled;

    @Value("${notification.enabled.loan_overdue}")
    private boolean loanOverdueEnabled;

    @Value("${notification.enabled.loan_default}")
    private boolean loanDefaultEnabled;

    @Value("${notification.enabled.loan_written_off}")
    private boolean loanWrittenOffEnabled;

    @Value("${notification.enabled.loan_closed}")
    private boolean loanClosedEnabled;

    @Value("${notification.enabled.loan_creation}")
    private boolean loanCreationEnabled;

    @Value("${notification.enabled.payment_received}")
    private boolean paymentReceivedEnabled;

    @Value("${notification.enabled.late_fee}")
    private boolean lateFeeEnabled;

    // Notification templates
    @Value("${notification.template.payment_reminder}")
    private String paymentReminderTemplate;

    @Value("${notification.template.installment_reminder}")
    private String installmentReminderTemplate;

    @Value("${notification.template.loan_overdue}")
    private String loanOverdueTemplate;

    @Value("${notification.template.late_fee}")
    private String lateFeeTemplate;

    @Value("${notification.template.loan_default}")
    private String loanDefaultTemplate;

    @Value("${notification.template.loan_written_off}")
    private String loanWrittenOffTemplate;

    @Value("${notification.template.loan_closed}")
    private String loanClosedTemplate;

    @Value("${notification.template.loan_creation}")
    private String loanCreationTemplate;

    @Value("${notification.template.payment_received}")
    private String paymentReceivedTemplate;



    public boolean isNotificationEnabled(NotificationType type) {
        return switch (type) {
            case PAYMENT_REMINDER -> paymentReminderEnabled;
            case INSTALLMENT_REMINDER -> installmentReminderEnabled;
            case LOAN_OVERDUE -> loanOverdueEnabled;
            case LOAN_DEFAULT -> loanDefaultEnabled;
            case LOAN_WRITTEN_OFF -> loanWrittenOffEnabled;
            case LOAN_CLOSED -> loanClosedEnabled;
            case LOAN_CREATION -> loanCreationEnabled;
            case PAYMENT_RECEIVED -> paymentReceivedEnabled;
            case FEE_APPLIED -> lateFeeEnabled;
        };
    }



    public boolean isNotificationEnabledForProduct(NotificationType type) {
        // Without custom product settings, use global settings
        return isNotificationEnabled(type);
    }



    public boolean isNotificationEnabledForSegment(NotificationType type) {
        // Without custom segment settings, use global settings
        return isNotificationEnabled(type);
    }



    public boolean isChannelEnabledForNotificationType(NotificationType type, NotificationChannel channel) {
        // Without channel-specific settings, allow all channels
        return true;
    }



    public double getMinLoanAmountForNotification(NotificationType type) {
        // Without minimum amount settings, no minimum is enforced
        return 0.0;
    }



    public String getNotificationTemplate(NotificationType type) {
        return switch (type) {
            case PAYMENT_REMINDER -> paymentReminderTemplate;
            case INSTALLMENT_REMINDER -> installmentReminderTemplate;
            case LOAN_OVERDUE -> loanOverdueTemplate;
            case LOAN_DEFAULT -> loanDefaultTemplate;
            case LOAN_WRITTEN_OFF -> loanWrittenOffTemplate;
            case LOAN_CLOSED -> loanClosedTemplate;
            case LOAN_CREATION -> loanCreationTemplate;
            case PAYMENT_RECEIVED -> paymentReceivedTemplate;
            case FEE_APPLIED -> lateFeeTemplate;
        };
    }



    public NotificationChannel getDefaultNotificationChannel() {
        try {
            return NotificationChannel.valueOf(defaultChannelName.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid notification channel in config: {}", defaultChannelName);
            return NotificationChannel.SMS;
        }
    }
}
