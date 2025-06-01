package com.interview.lender.scheduler;

import com.interview.lender.config.AppConfig;
import com.interview.lender.services.LoanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JobScheduler {
    private final AppConfig appConfig;
    private final LoanService loanService;



    @Scheduled(fixedDelay = 60000)
    public void processLoanScoring() {
        loanService.processLoanScoring();
    }
}