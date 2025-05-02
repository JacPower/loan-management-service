package com.interview.lender.rule;

import com.interview.lender.dto.*;
import com.interview.lender.enums.BusinessRulesOperations;
import com.interview.lender.exception.BusinessException;
import com.interview.lender.services.LoanApplicationProcessor;
import com.interview.lender.services.LoanEventsProcessor;
import com.interview.lender.util.TestUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.interview.lender.enums.BusinessRules.LOAN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanRuleTest {

    @Mock
    private LoanApplicationProcessor loanApplicationProcessor;
    @Mock
    private LoanEventsProcessor loanEventsProcessor;

    @InjectMocks
    private LoanRule loanRule;

    private LoanRequestDto loanRequestDto;
    private PaymentDto paymentDto;
    private LoanJobDto loanJobDto;
    private GlobalFiltersDto filtersDto;
    private ResponseDto responseDto;



    @BeforeEach
    void setUp() {
        loanRequestDto = TestUtilities.createLoanRequestDto();
        paymentDto = TestUtilities.createPaymentDto();
        loanJobDto = TestUtilities.createLoanJobDto();
        filtersDto = GlobalFiltersDto.builder().build();
        responseDto = TestUtilities.createSuccessResponse("Success", null);
    }



    @Test
    void canExecute_shouldReturnTrue_whenValidBusinessRule() {
        boolean result = loanRule.canExecute(LOAN);

        assertTrue(result);
    }



    @Test
    void canExecute_shouldReturnFalse_whenInvalidBusinessRule() {
        boolean result = loanRule.canExecute("INVALID_RULE");

        assertFalse(result);
    }



    @Test
    void execute_shouldCreateLoan_whenCreateLoanOperation() {
        when(loanApplicationProcessor.createLoan(any(LoanRequestDto.class))).thenReturn(responseDto);

        ResponseDto result = loanRule.execute(BusinessRulesOperations.CREATE_LOAN, loanRequestDto);

        assertEquals(responseDto, result);
        verify(loanApplicationProcessor).createLoan(loanRequestDto);
    }



    @Test
    void execute_shouldGetLoan_whenGetLoanOperation() {
        when(loanApplicationProcessor.getLoan(any(GlobalFiltersDto.class))).thenReturn(responseDto);

        ResponseDto result = loanRule.execute(BusinessRulesOperations.GET_LOAN, filtersDto);

        assertEquals(responseDto, result);
    }



    @Test
    void execute_shouldGetAllLoans_whenGetAllLoansOperation() {
        when(loanApplicationProcessor.getAllLoans(any(GlobalFiltersDto.class))).thenReturn(responseDto);

        ResponseDto result = loanRule.execute(BusinessRulesOperations.GET_ALL_LOANS, filtersDto);

        assertEquals(responseDto, result);
        verify(loanApplicationProcessor).getAllLoans(filtersDto);
    }



    @Test
    void execute_shouldPayLoan_whenPayLoanOperation() {
        when(loanEventsProcessor.processLoanPayment(any(PaymentDto.class))).thenReturn(responseDto);

        ResponseDto result = loanRule.execute(BusinessRulesOperations.PAY_LOAN, paymentDto);

        assertEquals(responseDto, result);
    }



    @Test
    void execute_shouldGetLoanPayments_whenGetLoanPaymentsOperation() {
        when(loanApplicationProcessor.getLoanPayments(any(GlobalFiltersDto.class))).thenReturn(responseDto);

        ResponseDto result = loanRule.execute(BusinessRulesOperations.GET_LOAN_PAYMENTS, filtersDto);

        assertEquals(responseDto, result);
    }



    @Test
    void execute_shouldMarkLoansOverdue_whenMarkLoansOverdueOperation() {
        when(loanEventsProcessor.markLoansAsOverdue(any(LoanJobDto.class))).thenReturn(responseDto);

        ResponseDto result = loanRule.execute(BusinessRulesOperations.MARK_LOANS_OVERDUE, loanJobDto);

        assertEquals(responseDto, result);
    }



    @Test
    void execute_shouldMarkLoansDefaulted_whenMarkLoansDefaultedOperation() {
        when(loanEventsProcessor.markLoansAsDefaulted(any(LoanJobDto.class))).thenReturn(responseDto);

        ResponseDto result = loanRule.execute(BusinessRulesOperations.MARK_LOANS_DEFAULTED, loanJobDto);

        assertEquals(responseDto, result);
    }



    @Test
    void execute_shouldMarkLoansWriteOff_whenMarkLoansWriteOffOperation() {
        when(loanEventsProcessor.markLoansAsWrittenOff(any(LoanJobDto.class))).thenReturn(responseDto);

        ResponseDto result = loanRule.execute(BusinessRulesOperations.MARK_LOANS_WRITE_OFF, loanJobDto);

        assertEquals(responseDto, result);
        verify(loanEventsProcessor).markLoansAsWrittenOff(loanJobDto);
    }



    @Test
    void execute_shouldApplyLateFees_whenApplyLateFeesOperation() {
        when(loanEventsProcessor.applyLateFees(any(LoanJobDto.class))).thenReturn(responseDto);

        ResponseDto result = loanRule.execute(BusinessRulesOperations.APPLY_LATE_FEES, loanJobDto);

        assertEquals(responseDto, result);
    }



    @Test
    void execute_shouldSendPaymentDueReminder_whenSendPaymentDueReminderOperation() {
        when(loanEventsProcessor.sendLoanDueReminders(any(LoanJobDto.class))).thenReturn(responseDto);

        ResponseDto result = loanRule.execute(BusinessRulesOperations.SEND_PAYMENT_DUE_REMINDER, loanJobDto);

        assertEquals(responseDto, result);
    }



    @Test
    void execute_shouldSendInstallmentDueReminder_whenSendInstallmentDueReminderOperation() {
        when(loanEventsProcessor.sendInstallmentDueReminders(any(LoanJobDto.class))).thenReturn(responseDto);

        ResponseDto result = loanRule.execute(BusinessRulesOperations.SEND_INSTALLMENT_DUE_REMINDER, loanJobDto);

        assertEquals(responseDto, result);
    }



    @Test
    void execute_shouldThrowException_whenUnknownOperation() {
        assertThrows(BusinessException.class, () -> {
            loanRule.execute("UNKNOWN_OPERATION", loanRequestDto);
        });
    }
}
