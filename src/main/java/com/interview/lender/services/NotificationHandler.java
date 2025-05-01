package com.interview.lender.services;

import com.interview.lender.config.NotificationConfig;
import com.interview.lender.entity.*;
import com.interview.lender.enums.*;
import com.interview.lender.repository.CustomerNotificationChannelRepository;
import com.interview.lender.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationHandler {
    private final NotificationRepository notificationRepository;
    private final CustomerNotificationChannelRepository channelRepository;
    private final NotificationConfig notificationConfig;



    public boolean notifyLoanStatusChange(Loan loan, LoanStatus oldStatus, LoanStatus newStatus) {
        LoanEventType eventType = mapStatusChangeToEventType(oldStatus, newStatus);

        if (eventType != null) return sendLoanEventNotification(loan, eventType);

        return false;
    }



    public boolean sendLoanEventNotification(Loan loan, LoanEventType event) {
        NotificationType notificationType = mapEventToNotificationType(event);
        if (notificationType == null) {
            log.error("No notification type mapping for event: {}", event);
            return false;
        }

        if (!notificationConfig.isNotificationEnabled(notificationType)) {
            log.error("Notification rules determined not to send {} for loan {}", notificationType, loan.getId());
            return false;
        }
        String content = generateContent(loan, notificationType);

        return sendNotificationToCustomer(loan.getCustomer(), loan, notificationType, content);
    }



    public boolean sendUpcomingPaymentReminder(Loan loan) {
        if (!notificationConfig.isNotificationEnabled(NotificationType.PAYMENT_REMINDER)) {
            return false;
        }

        String content = generateContent(loan, NotificationType.PAYMENT_REMINDER);
        return sendNotificationToCustomer(loan.getCustomer(), loan, NotificationType.PAYMENT_REMINDER, content);
    }



    public boolean sendInstallmentReminder(Installment installment) {
        Loan loan = installment.getLoan();

        if (!notificationConfig.isNotificationEnabled(NotificationType.INSTALLMENT_REMINDER)) {
            return false;
        }

        Map<String, Object> variables = Map.of(
                "installmentAmount", installment.getAmount(),
                "installmentDueDate", formatDate(installment.getDueDate())
        );

        String content = generateContent(loan, NotificationType.INSTALLMENT_REMINDER, variables);
        return sendNotificationToCustomer(loan.getCustomer(), loan, NotificationType.INSTALLMENT_REMINDER, content);
    }



    private boolean sendNotificationToCustomer(Customer customer, Loan loan, NotificationType type, String content) {
        if (content == null) {
            log.error("No content generated for notification type {}, loan {}", type, loan.getId());
            return false;
        }

        List<CustomerNotificationChannel> channels = channelRepository.findByCustomerId(customer.getId());

        if (channels.isEmpty()) {
            NotificationChannel defaultChannel = notificationConfig.getDefaultNotificationChannel();
            log.info("No channels for customer {}, using default: {}", customer.getId(), defaultChannel);
            createNotification(customer, loan, type, content, defaultChannel);
            return true;
        }

        boolean anySent = false;
        for (CustomerNotificationChannel channel : channels) {
            log.info("Sending {} to customer {} via {}", type, customer.getId(), channel.getChannel());
            createNotification(customer, loan, type, content, channel.getChannel());
            anySent = true;
        }

        return anySent;
    }



    private NotificationType mapEventToNotificationType(LoanEventType event) {
        return switch (event) {
            case LOAN_CREATED -> NotificationType.LOAN_CREATION;
            case PAYMENT_DUE -> NotificationType.PAYMENT_REMINDER;
            case PAYMENT_RECEIVED -> NotificationType.PAYMENT_RECEIVED;
            case LOAN_OVERDUE -> NotificationType.LOAN_OVERDUE;
            case LOAN_DEFAULTED -> NotificationType.LOAN_DEFAULT;
            case LOAN_WRITTEN_OFF -> NotificationType.LOAN_WRITTEN_OFF;
            case LOAN_CLOSED -> NotificationType.LOAN_CLOSED;
            case FEE_APPLIED -> NotificationType.FEE_APPLIED;
        };
    }



    private LoanEventType mapStatusChangeToEventType(LoanStatus oldStatus, LoanStatus newStatus) {
        if (oldStatus == LoanStatus.OPEN && newStatus == LoanStatus.OVERDUE) {
            return LoanEventType.LOAN_OVERDUE;
        } else if (oldStatus == LoanStatus.OVERDUE && newStatus == LoanStatus.DEFAULTED) {
            return LoanEventType.LOAN_DEFAULTED;
        } else if (oldStatus == LoanStatus.DEFAULTED && newStatus == LoanStatus.WRITTEN_OFF) {
            return LoanEventType.LOAN_WRITTEN_OFF;
        } else if (newStatus == LoanStatus.CLOSED) {
            return LoanEventType.LOAN_CLOSED;
        }
        return null;
    }



    private String generateContent(Loan loan, NotificationType type) {
        return generateContent(loan, type, Map.of());
    }



    private String generateContent(Loan loan, NotificationType type, Map<String, Object> customVars) {
        String template = notificationConfig.getNotificationTemplate(type);
        if (template.isEmpty()) {
            log.warn("No template found for notification type: {}", type);
            return null;
        }

        Customer customer = loan.getCustomer();
        Product product = loan.getProduct();
        var dueDate = loan.getInstallments().isEmpty() ? loan.getDueDate() : formatDate(loan.getInstallments().getFirst().getDueDate());
        Map<String, Object> variables = new HashMap<>(Map.of(
                "customerName", customer.getFirstName() + " " + customer.getLastName(),
                "firstName", customer.getFirstName(),
                "loanId", loan.getLoanCode(),
                "loanAmount", loan.getCurrentBalance(),
                "paymentAmount", loan.getAmountPaid(),
                "currentBalance", loan.getCurrentBalance(),
                "lateFeeAmount", loan.getLateFeeApplied(),
                "dueDate", dueDate,
                "productName", product.getProductName()
        ));

        if (customVars != null) {
            variables.putAll(customVars);
        }

        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            template = template.replace(placeholder, value);
        }

        return template;
    }



    private void createNotification(Customer customer, Loan loan, NotificationType type, String content, NotificationChannel channel) {
        Notification notification = Notification.builder()
                .customer(customer)
                .loan(loan)
                .notificationType(type)
                .content(content)
                .channel(channel)
                .status(NotificationStatus.PENDING)
                .build();

        notificationRepository.save(notification);
    }



    private String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
    }
}