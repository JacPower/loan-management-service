package com.interview.lender.schedular;

import com.interview.lender.config.AppConfig;
import com.interview.lender.scheduler.JobScheduler;
import com.interview.lender.services.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobSchedulerTest {

    @Mock
    private AppConfig appConfig;
    @Mock
    private LoanService loanService;

    @InjectMocks
    private JobScheduler jobScheduler;



    @BeforeEach
    void setUp() {
        // No specific setup needed for this simple scheduler
    }



    @Test
    void processLoanScoring_shouldCallLoanServiceProcessLoanScoring() {
        doNothing().when(loanService).processLoanScoring();

        jobScheduler.processLoanScoring();

        verify(loanService, times(1)).processLoanScoring();
    }



    @Test
    void processLoanScoring_shouldBeCalledMultipleTimes() {
        doNothing().when(loanService).processLoanScoring();

        jobScheduler.processLoanScoring();
        jobScheduler.processLoanScoring();
        jobScheduler.processLoanScoring();

        verify(loanService, times(3)).processLoanScoring();
    }



    private void assertDoesNotThrow(Runnable executable) {
        try {
            executable.run();
        } catch (Exception e) {
            throw new AssertionError("Expected no exception to be thrown, but got: " + e.getMessage(), e);
        }
    }
}
