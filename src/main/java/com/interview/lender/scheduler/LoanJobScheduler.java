package com.interview.lender.scheduler;

import com.interview.lender.dto.LoanJobDto;
import com.interview.lender.rule.RuleDelegator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static com.interview.lender.enums.BusinessRules.LOAN;
import static com.interview.lender.enums.BusinessRulesOperations.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoanJobScheduler {
    private final RuleDelegator ruleDelegator;



    /**
     * Daily job to mark overdue loans.
     * Runs at 12:00 AM every day.
     */
    @Scheduled(cron = "0 00 00 * * ?")
    public void markOverdueLoans() {
        log.info("Scheduling job: Mark overdue loans");

        LoanJobDto payload = basePayloadBuilder()
                .overdueThreshold(1)
                .build();
        ruleDelegator.routeRequest(LOAN, MARK_LOANS_OVERDUE, payload);
    }



    /**
     * Daily job to apply late fees to overdue loans.
     * Runs at 00:30 AM every day.
     */
    @Scheduled(cron = "0 30 00 * * ?")
    public void applyLateFeesToOverdueLoans() {
        log.info("Scheduling job: Apply late fees to overdue loans");

        LoanJobDto payload = basePayloadBuilder().build();
        ruleDelegator.routeRequest(LOAN, APPLY_LATE_FEES, payload);
    }



    /**
     * Daily job to send loan payment reminders.
     * Runs at 01:00 AM every day.
     */
    @Scheduled(cron = "0 0 01 * * ?")
    public void sendLoanDueReminders() {
        log.info("Scheduling job: Send loan due reminders for upcoming loans");

        LoanJobDto payload = basePayloadBuilder()
                .reminderDays(3)
                .build();
        ruleDelegator.routeRequest(LOAN, SEND_PAYMENT_DUE_REMINDER, payload);
    }



    /**
     * Daily job to send loan payment reminders.
     * Runs at 01:30 AM every day.
     */
    @Scheduled(cron = "0 30 01 * * ?")
    public void sendInstallmentDueReminders() {
        log.info("Scheduling job: Send installment reminders");

        LoanJobDto payload = basePayloadBuilder()
                .reminderDays(3)
                .build();
        ruleDelegator.routeRequest(LOAN, SEND_PAYMENT_DUE_REMINDER, payload);
    }



    /**
     * Daily job to update severely overdue loans to default.
     * Runs at 02:00 PM every day.
     */
    @Scheduled(cron = "0 0 02 * * ?")
    public void updateDefaultedLoanStatuses() {
        log.info("Scheduling job: Update statuses of severely overdue loans to defaulted");

        LoanJobDto payload = basePayloadBuilder()
                .defaultThresholdDays(30)
                .build();
        ruleDelegator.routeRequest(LOAN, MARK_LOANS_DEFAULTED, payload);
    }



    /**
     * Monthly job to identify loans for write-off.
     * Runs at 02:30 AM on the 1st day of each month.
     */
    @Scheduled(cron = "0 30 0 2 * ?")
    public void updateWrittenOffLoanStatuses() {
        log.info("Scheduling job: Identify write-off loans");

        LoanJobDto payload = basePayloadBuilder()
                .writeOffThresholdDays(90)
                .build();
        ruleDelegator.routeRequest(LOAN, MARK_LOANS_WRITE_OFF, payload);
    }



    private LoanJobDto.LoanJobDtoBuilder basePayloadBuilder() {
        return LoanJobDto.builder().executionDate(LocalDate.now());
    }
}