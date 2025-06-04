package com.interview.lender.services;

import com.interview.lender.config.AppConfig;
import com.interview.lender.dto.ClientRegistrationRequest;
import com.interview.lender.dto.ClientRegistrationResponse;
import com.interview.lender.dto.HttpResultDto;
import com.interview.lender.dto.ScoringResponse;
import com.interview.lender.exception.ExternalServiceException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScoringService {

    private final AppConfig appConfig;
    private final RestClientService restClientService;
    private String clientToken = "";



    @PostConstruct
    public void init() {
        registerClient();
    }



    public String initiateScoring(String customerNumber) {
        log.info("Initiating scoring for customer: {}", customerNumber);

        String url = appConfig.getScoringBaseUrl() + appConfig.getScoringInitiatePath() + "/" + customerNumber;
        HttpHeaders headers = new HttpHeaders();
        headers.set("client-token", getClientToken());
        HttpResultDto result = restClientService.sendRequest(url, HttpMethod.GET, headers, null, String.class);

        if (!result.isSuccess()) {
            log.error("Failed to initiate scoring: {}", result.getError());
            throw new ExternalServiceException("Failed to initiate scoring: " + result.getError());
        }

        String token = (String) result.getMessage();
        log.info("Scoring initiated successfully for customer: {}, token: {}", customerNumber, token);

        return token;
    }



    public Optional<ScoringResponse> getScore(String token) {
        log.info("Getting score for token: {}", token);

        String url = appConfig.getScoringBaseUrl() + appConfig.getScoringQueryPath() + "/" + token;
        HttpHeaders headers = new HttpHeaders();
        headers.set("client-token", getClientToken());
        HttpResultDto result = restClientService.sendRequest(url, HttpMethod.GET, headers, null, ScoringResponse.class);

        if (!result.isSuccess()) {
            int statusCode = result.getStatusCode();
            if (statusCode == HttpStatus.NOT_FOUND.value() || statusCode == HttpStatus.NO_CONTENT.value()) {
                log.warn("Score not ready yet for token: {}", token);
                return Optional.empty();
            }

            log.error("Failed to get score: {}", result.getError());
            throw new ExternalServiceException("Failed to get score: " + result.getError());
        }
        ScoringResponse scoringResponse = (ScoringResponse) result.getMessage();

        return Optional.of(scoringResponse);
    }



    public void registerClient() {
        log.info("Registering LMS client with scoring service...");

        ClientRegistrationRequest request = ClientRegistrationRequest.builder()
                .url(appConfig.getTransactionsUrl())
                .name(appConfig.getTransactionApiServiceName())
                .username(appConfig.getTransactionApiServiceName())
                .password(appConfig.getTransactionApiPassword())
                .build();

        String url = appConfig.getScoringBaseUrl() + appConfig.getScoringClientCreatePath();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpResultDto result = restClientService.sendRequest(url, HttpMethod.POST, headers, request, ClientRegistrationResponse.class);

        if (!result.isSuccess()) {
            log.error("Failed to register client: {}", result.getError());
            throw new ExternalServiceException("Failed to register client: " + result.getError());
        }

        ClientRegistrationResponse response = (ClientRegistrationResponse) result.getMessage();
        clientToken = response.getToken();
        log.info("Client registered successfully | Name: {}, token: {}", response.getName(), response.getToken());
    }



    private String getClientToken() {
        if (clientToken.isBlank()) {
            registerClient();
        }
        return clientToken;
    }
}