package com.interview.lender.rule;

import com.interview.lender.dto.*;
import com.interview.lender.enums.BusinessRulesOperations;
import com.interview.lender.exception.BusinessException;
import com.interview.lender.services.LoanApplicationProcessor;
import com.interview.lender.services.LoanEventsProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.interview.lender.enums.BusinessRules.LOAN;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanRule implements IRule {
    private final LoanApplicationProcessor loanApplicationProcessor;
    private final LoanEventsProcessor loanEventsProcessor;



    @Override
    public boolean canExecute(Object businessRule) {
        return businessRule.equals(LOAN);
    }



    @Override
    public ResponseDto execute(Object operation, Object requestDto) {
        return switch (BusinessRulesOperations.ofValue(operation)) {
            case CREATE_LOAN -> loanApplicationProcessor.createLoan((LoanRequestDto) requestDto);
            case GET_LOAN -> loanApplicationProcessor.getLoan((GlobalFiltersDto) requestDto);
            case GET_ALL_LOANS -> loanApplicationProcessor.getAllLoans((GlobalFiltersDto) requestDto);
            case PAY_LOAN -> loanEventsProcessor.processLoanPayment((PaymentDto) requestDto);
            case GET_LOAN_PAYMENTS -> loanApplicationProcessor.getLoanPayments((GlobalFiltersDto) requestDto);
            case MARK_LOANS_OVERDUE -> loanEventsProcessor.markLoansAsOverdue((LoanJobDto) requestDto);
            case MARK_LOANS_DEFAULTED -> loanEventsProcessor.markLoansAsDefaulted((LoanJobDto) requestDto);
            case MARK_LOANS_WRITE_OFF -> loanEventsProcessor.markLoansAsWrittenOff((LoanJobDto) requestDto);
            case APPLY_LATE_FEES -> loanEventsProcessor.applyLateFees((LoanJobDto) requestDto);
            case SEND_PAYMENT_DUE_REMINDER -> loanEventsProcessor.sendLoanDueReminders((LoanJobDto) requestDto);
            case SEND_INSTALLMENT_DUE_REMINDER -> loanEventsProcessor.sendInstallmentDueReminders((LoanJobDto) requestDto);
            default -> {
                String message = "Loan operation not supported: " + operation;
                log.error(message);
                throw BusinessException.unknownOperation(message);
            }
        };
    }
}
