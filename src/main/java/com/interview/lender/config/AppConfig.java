package com.interview.lender.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class AppConfig {
    @Value("${cbs.kyc.url}")
    private String cbsKycUrl;

    @Value("${cbs.transaction.url}")
    private String cbsTransactionUrl;

    @Value("${cbs.username}")
    private String cbsUsername;

    @Value("${cbs.password}")
    private String cbsPassword;

    // Scoring Engine Configuration
    @Value("${scoring.base.url}")
    private String scoringBaseUrl;

    @Value("${scoring.initiate.path}")
    private String scoringInitiatePath;

    @Value("${scoring.query.path}")
    private String scoringQueryPath;

    @Value("${scoring.client.create.path}")
    private String scoringClientCreatePath;

    @Value("${scoring.retry.max.attempts}")
    private int scoringRetryMaxAttempts;

    @Value("${scoring.retry.delay.seconds}")
    private int scoringRetryDelaySeconds;

    // Transaction Data API Configuration
    @Value("${transaction.url}")
    private String transactionsUrl;

    @Value("${transaction.api.service.name}")
    private String transactionApiServiceName;

    @Value("${transaction.api.username}")
    private String transactionApiUsername;

    @Value("${transaction.api.password}")
    private String transactionApiPassword;
}
