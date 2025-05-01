package com.interview.lender.services;

import com.interview.lender.dto.LoanJobDto;
import com.interview.lender.dto.PaymentDto;
import com.interview.lender.dto.ResponseDto;
import com.interview.lender.entity.*;
import com.interview.lender.enums.InstallmentStatus;
import com.interview.lender.enums.LoanEventType;
import com.interview.lender.enums.LoanStatus;
import com.interview.lender.repository.LoanFeeRepository;
import com.interview.lender.repository.LoanRepository;
import com.interview.lender.repository.ProductFeeRepository;
import com.interview.lender.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.interview.lender.enums.FeeType.LATE_PAYMENT;
import static org.springframework.http.HttpStatus.OK;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoanEventsProcessor {
    private final LoanRepository loanRepository;
    private final NotificationHandler notificationHandler;
    private final RepaymentHandler repaymentHandler;
    private final LoanFeeRepository loanFeeRepository;
    private final ProductFeeRepository productFeeRepository;



    public ResponseDto processLoanPayment(PaymentDto requestDto) {
        return repaymentHandler.processPayment(requestDto);
    }



    @Transactional
    public ResponseDto applyLateFees(LoanJobDto requestDto) {
        log.info("<===Start late fee application job===>");

        LocalDate executionDate = requestDto.getExecutionDate();
        List<Loan> overdueLoans = loanRepository.findByLoanStatusAndDueDateBefore(LoanStatus.OVERDUE, executionDate);

        if (overdueLoans.isEmpty()) {
            log.info("No overdue loans found for late fee application");
            return null;
        }

        List<Loan> loansEligibleForLateFee = overdueLoans.stream()
                .filter(loan -> {
                    Product product = loan.getProduct();
                    long daysLate = ChronoUnit.DAYS.between(loan.getDueDate(), executionDate);
                    return daysLate >= product.getDaysAfterDueForLateFee();
                })
                .toList();

        if (loansEligibleForLateFee.isEmpty()) {
            log.info("No loans past the grace period for late fee application");
            return Util.buildResponse("No loans eligible for late fee application", OK, requestDto);
        }

        log.info("Found {} overdue loans past grace period out of {} total overdue loans", loansEligibleForLateFee.size(), overdueLoans.size());

        long updatedCount = loansEligibleForLateFee.stream()
                .filter(loan -> processLateFeeForLoan(loan, executionDate))
                .count();

        log.info("Applied fees to {}/{} eligible loan", updatedCount, loansEligibleForLateFee.size());
        log.info("<===End late fee application job===>");

        return Util.buildResponse("Completed applying late fee", OK, requestDto);
    }



    @Transactional
    public ResponseDto sendLoanDueReminders(LoanJobDto requestDto) {
        log.info("<===Start sending payment reminders job===>");

        LocalDate executionDate = requestDto.getExecutionDate();
        int reminderDays = requestDto.getReminderDays();
        LocalDate thresholdDate = executionDate.plusDays(reminderDays);

        List<Loan> upcomingLoans = loanRepository.findByLoanStatusAndDueDateBetween(LoanStatus.OPEN, executionDate, thresholdDate);

        if (upcomingLoans.isEmpty()) {
            String message = "No loans due in the next %d days".formatted(reminderDays);
            log.info(message);
            return Util.buildResponse(message, OK, requestDto);
        }

        log.info("Found {} upcoming loans due between {} and {}", upcomingLoans.size(), executionDate, thresholdDate);

        long remindersSent = upcomingLoans.stream()
                .filter(notificationHandler::sendUpcomingPaymentReminder)
                .count();

        log.info("Sent {}/{} payment reminders", remindersSent, upcomingLoans.size());
        log.info("<===End sending payment reminders job===>");

        return Util.buildResponse("Completed sending loan due reminders", OK, requestDto);
    }



    @Transactional
    public ResponseDto sendInstallmentDueReminders(LoanJobDto requestDto) {
        log.info("<=== Start loan installment due reminder job ===>");

        LocalDate executionDate = requestDto.getExecutionDate();
        LocalDate thresholdDate = executionDate.plusDays(requestDto.getReminderDays());

        List<Installment> upcomingInstallments = getUpcomingInstallments(executionDate, thresholdDate);

        if (upcomingInstallments.isEmpty()) {
            String message = "No loan installments due in the next %d days".formatted(requestDto.getReminderDays());
            log.info(message);
            return Util.buildResponse(message, OK, requestDto);
        }

        log.info("Found {} installments due between {} and {}", upcomingInstallments.size(), executionDate, thresholdDate);

        long remindersSent = upcomingInstallments.stream()
                .filter(notificationHandler::sendInstallmentReminder)
                .count();

        log.info("Sent {}/{} installment reminders", remindersSent, upcomingInstallments.size());
        log.info("<=== End loan installment due reminder job ===>");

        return Util.buildResponse("Completed sending installment due reminders", OK, requestDto);
    }



    @Transactional
    public ResponseDto markLoansAsOverdue(LoanJobDto requestDto) {
        log.info("<===Start marking loans as overdue job===>");
        int overdueThreshold = requestDto.getOverdueThreshold();
        LocalDate thresholdDate = requestDto.getExecutionDate().minusDays(overdueThreshold);

        List<Loan> pastDueLoans = loanRepository.findByLoanStatusAndDueDateBefore(LoanStatus.OPEN, thresholdDate);

        if (pastDueLoans.isEmpty()) {
            log.info("No loans past due by {}+ days to mark as overdue", overdueThreshold);
            return Util.buildResponse("No due loans found", OK, requestDto);
        }

        log.info("Found {} loans past due by {}+ days", pastDueLoans.size(), overdueThreshold);

        long updatedCount = pastDueLoans.stream()
                .filter(loan -> updateLoanStatus(loan, LoanStatus.OVERDUE, "System identified loan as overdue"))
                .count();

        log.info("Marked {} of {} loans as OVERDUE", updatedCount, pastDueLoans.size());
        log.info("<===End marking loans as overdue job===>");

        return Util.buildResponse("Completed marking loans as overdue", OK, requestDto);
    }



    public ResponseDto markLoansAsDefaulted(LoanJobDto requestDto) {
        log.info("<===Start updating overdue loan statuses to defaulted job===>");

        int thresholdDays = requestDto.getDefaultThresholdDays();
        LocalDate thresholdDate = requestDto.getExecutionDate().minusDays(thresholdDays);

        List<Loan> severelyOverdueLoans = loanRepository.findByLoanStatusAndDueDateBefore(LoanStatus.OVERDUE, thresholdDate);

        if (severelyOverdueLoans.isEmpty()) {
            log.info("No loans overdue by {}+ days to mark as defaulted", thresholdDays);
            return Util.buildResponse("No loans qualify for default status", OK, requestDto);
        }

        log.info("Found {} loans overdue by {}+ days", severelyOverdueLoans.size(), thresholdDays);

        long updatedCount = severelyOverdueLoans.stream()
                .filter(loan -> updateLoanStatus(loan, LoanStatus.DEFAULTED, "System identified loan as defaulted"))
                .count();

        log.info("Marked {} of {} loans as DEFAULTED", updatedCount, severelyOverdueLoans.size());
        log.info("<===End updating overdue loan statuses to defaulted job===>");

        return Util.buildResponse("Completed marking loans as defaulted", OK, requestDto);
    }



    @Transactional
    public ResponseDto markLoansAsWrittenOff(LoanJobDto requestDto) {
        log.info("<===Start identifying write-off loans job===>");

        int writeOffThresholdDays = requestDto.getWriteOffThresholdDays();
        LocalDate thresholdDate = requestDto.getExecutionDate().minusDays(writeOffThresholdDays);

        List<Loan> longDefaultedLoans = loanRepository.findByLoanStatusAndDueDateBefore(LoanStatus.DEFAULTED, thresholdDate);

        if (longDefaultedLoans.isEmpty()) {
            log.info("No loans defaulted for {}+ days to write off", writeOffThresholdDays);
            return Util.buildResponse("No loans that qualify for write-off", OK, requestDto);
        }

        log.info("Found {} loans defaulted for {}+ days", longDefaultedLoans.size(), writeOffThresholdDays);

        long updatedCount = longDefaultedLoans.stream()
                .filter(loan -> updateLoanStatus(loan, LoanStatus.WRITTEN_OFF, "System identified loan as written off"))
                .count();

        log.info("Marked {} of {} loans as WRITTEN_OFF", updatedCount, longDefaultedLoans.size());
        log.info("<===End identifying write-off candidates job===>");

        return Util.buildResponse("Completed marking loans as written-off", OK, requestDto);
    }



    private boolean updateLoanStatus(Loan loan, LoanStatus newStatus, String description) {
        LoanStatus oldStatus = loan.getLoanStatus();
        loan.setDescription(description);
        loan.setLoanStatus(newStatus);
        loanRepository.save(loan);

        boolean notified = notificationHandler.notifyLoanStatusChange(loan, oldStatus, newStatus);
        if (!notified) {
            log.warn("Failed to send notification for loan {} status change to {}", loan.getId(), newStatus);
        }

        return true;
    }



    private boolean processLateFeeForLoan(Loan loan, LocalDate executionDate) {
        LocalDate dueDate = loan.getDueDate();
        if (!executionDate.isAfter(dueDate)) {
            return false;
        }

        Product product = loan.getProduct();
        long daysLate = ChronoUnit.DAYS.between(dueDate, executionDate);

        if (daysLate < product.getDaysAfterDueForLateFee()) {
            log.info("Loan {} is {} days late (grace period: {} days) - fee not applied", loan.getId(), daysLate, product.getDaysAfterDueForLateFee());
            return false;
        }

        List<ProductFee> productFees = productFeeRepository.findByProductId(product.getId());

        if (productFees.isEmpty()) {
            log.info("No fees configured for product {} of loan {}", product.getId(), loan.getId());
            return false;
        }

        List<Fee> feesToApply = productFees.stream()
                .map(ProductFee::getFee)
                .filter(this::isActiveLatePaymentFee)
                .toList();
        feesToApply.forEach(fee -> applyFeeToLoan(loan, fee, executionDate));

        return !feesToApply.isEmpty();
    }



    private void applyFeeToLoan(Loan loan, Fee fee, LocalDate executionDate) {
        double feeAmount = calculateFeeAmount(loan, fee);

        LoanFee loanFee = LoanFee.builder()
                .loan(loan)
                .fee(fee)
                .amount(feeAmount)
                .appliedAt(executionDate.atStartOfDay())
                .build();

        loanFeeRepository.save(loanFee);

        loan.setLateFeeApplied(feeAmount);
        loan.setCurrentBalance(loan.getCurrentBalance() + feeAmount);
        loanRepository.save(loan);

        notificationHandler.sendLoanEventNotification(loan, LoanEventType.FEE_APPLIED);
    }



    private List<Installment> getUpcomingInstallments(LocalDate startDate, LocalDate endDate) {
        return loanRepository.findByLoanStatus(LoanStatus.OPEN).stream()
                .flatMap(loan -> loan.getInstallments().stream()
                        .filter(installment ->
                                installment.getInstallmentStatus() == InstallmentStatus.UNPAID &&
                                !installment.getDueDate().isBefore(startDate) &&
                                !installment.getDueDate().isAfter(endDate)))
                .collect(Collectors.toList());
    }



    private boolean isActiveLatePaymentFee(Fee fee) {
        return Boolean.TRUE.equals(fee.getIsActive()) && LATE_PAYMENT.name().equals(fee.getFeeType().name());
    }



    private double calculateFeeAmount(Loan loan, Fee fee) {
        return switch (fee.getCalculationType()) {
            case FIXED -> fee.getFeeValue();
            case PERCENTAGE -> loan.getPrincipalAmount() * fee.getFeeValue();
        };
    }
}