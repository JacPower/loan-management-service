package com.interview.lender.services;

import com.interview.lender.dto.PaymentDto;
import com.interview.lender.dto.ResponseDto;
import com.interview.lender.entity.Installment;
import com.interview.lender.entity.Loan;
import com.interview.lender.entity.Payment;
import com.interview.lender.enums.LoanEventType;
import com.interview.lender.enums.LoanStatus;
import com.interview.lender.repository.InstallmentRepository;
import com.interview.lender.repository.LoanRepository;
import com.interview.lender.repository.PaymentRepository;
import com.interview.lender.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.interview.lender.enums.InstallmentStatus.*;
import static com.interview.lender.enums.LoanStructureType.INSTALLMENT;
import static com.interview.lender.enums.PaymentStatus.COMPLETED;
import static org.springframework.http.HttpStatus.OK;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepaymentHandler {

    private final LoanRepository loanRepository;
    private final PaymentRepository paymentRepository;
    private final InstallmentRepository installmentRepository;
    private final NotificationHandler notificationHandler;
    private final LoanUtils loanUtils;



    @Transactional
    public ResponseDto processPayment(PaymentDto requestDto) {
        log.info("<=== Start processing loan payment | Payment Code: {} ===>", requestDto.getPaymentCode());

        var loan = loanUtils.getLoan(requestDto.getLoanCode(), LoanStatus.OPEN);
        var payment = savePaymentLoan(loan, requestDto);

        double newBalance = loan.getCurrentBalance() - payment.getAmount();
        loan.setAmountPaid(payment.getAmount());
        loan.setCurrentBalance(newBalance);

        if (loan.getStructureType().name().equals(INSTALLMENT.name())) {
            processInstallmentPayment(loan, payment.getAmount());
        } else {
            log.info("Non-installment loan {} | Principal: {}, Paid: {}, Balance: {}", loan.getId(), loan.getPrincipalAmount(), getTotalPaid(loan), newBalance);
        }

        updateLoanStatus(loan, newBalance);
        loanRepository.save(loan);
        log.info("Loan status and balance updated | Loan Code: {}", requestDto.getLoanCode());

        boolean notificationSent = notificationHandler.sendLoanEventNotification(loan, LoanEventType.PAYMENT_RECEIVED);
        if (!notificationSent) log.error("Failed to send payment notification for loan {}", loan.getId());

        log.info("<=== End processing loan payment | Payment Code: {} ===>", requestDto.getPaymentCode());

        return Util.buildResponse("Loan payment has been processed", OK, payment);
    }



    private Payment savePaymentLoan(Loan loan, PaymentDto requestDto) {
        Payment payment = Payment.builder()
                .loan(loan)
                .amount(requestDto.getAmount())
                .paymentDate(requestDto.getPaymentDate())
                .paymentMethod(requestDto.getPaymentMethod())
                .paymentCode(requestDto.getPaymentCode())
                .paymentStatus(COMPLETED)
                .build();

        return paymentRepository.save(payment);
    }



    private void updateLoanStatus(Loan loan, double balance) {
        if (balance <= 0) {
            loan.setLoanStatus(LoanStatus.CLOSED);
            log.info("Loan {} is fully paid", loan.getId());
        } else if ((loan.getLoanStatus() == LoanStatus.OVERDUE || loan.getLoanStatus() == LoanStatus.DEFAULTED) && isLoanCurrent(loan)) {
            loan.setLoanStatus(LoanStatus.OPEN);
            log.info("Loan {} status updated to OPEN", loan.getId());
        }
    }



    private boolean isLoanCurrent(Loan loan) {
        if (!INSTALLMENT.name().equals(loan.getStructureType().name())) return true;

        long overdueCount = installmentRepository.countByLoanIdAndInstallmentStatusAndDueDateBefore(loan.getId(), UNPAID, LocalDate.now());
        return overdueCount == 0;
    }



    private double getTotalPaid(Loan loan) {
        return paymentRepository.findByLoanIdAndPaymentStatus(loan.getId(), COMPLETED).stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }



    private void processInstallmentPayment(Loan loan, double paymentAmount) {
        log.info("Start installment payment processing | Loan Code: {}", loan.getLoanCode());
        List<Installment> partiallyPaidInstalments = installmentRepository.findByLoanIdAndInstallmentStatusOrderByDueDate(loan.getId(), PARTIALLY_PAID);

        double unappliedAmount = paymentAmount;
        if (!partiallyPaidInstalments.isEmpty()) {
            unappliedAmount = applyToInstallments(partiallyPaidInstalments, paymentAmount, true);
        }


        if (unappliedAmount > 0) {
            List<Installment> unpaidInstalments = installmentRepository.findByLoanIdAndInstallmentStatusOrderByDueDate(loan.getId(), UNPAID);
            unappliedAmount = applyToInstallments(unpaidInstalments, unappliedAmount, false);
        }

        if (unappliedAmount > 0) log.info("Excess payment of {} for loan {}", unappliedAmount, loan.getId());

        log.info("Completed installment payment processing | Loan Code: {}", loan.getLoanCode());
    }



    private double applyToInstallments(List<Installment> installments, double amount, boolean isPartial) {
        double remaining = amount;

        for (Installment installment : installments) {
            if (remaining <= 0) break;

            double due = installment.getAmount() - installment.getAmountPaid();
            if (remaining >= due) {
                installment.setAmountPaid(installment.getAmount());
                installment.setInstallmentStatus(PAID);
                remaining -= due;
            } else {
                installment.setAmountPaid(installment.getAmountPaid() + remaining);
                if (!isPartial) installment.setInstallmentStatus(PARTIALLY_PAID);
                remaining = 0;
            }

            installmentRepository.save(installment);
        }

        return remaining;
    }
}
