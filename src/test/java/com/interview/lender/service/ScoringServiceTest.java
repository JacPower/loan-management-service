package com.interview.lender.service;

import com.interview.lender.config.AppConfig;
import com.interview.lender.dto.ClientRegistrationRequest;
import com.interview.lender.dto.ClientRegistrationResponse;
import com.interview.lender.dto.HttpResultDto;
import com.interview.lender.dto.ScoringResponse;
import com.interview.lender.exception.ExternalServiceException;
import com.interview.lender.services.RestClientService;
import com.interview.lender.services.ScoringService;
import com.interview.lender.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ScoringServiceTest {

    @Mock
    private AppConfig appConfig;
    @Mock
    private RestClientService restClientService;

    @InjectMocks
    private ScoringService scoringService;

    private HttpResultDto successResult;
    private HttpResultDto failureResult;
    private ScoringResponse scoringResponse;
    private HttpResultDto registrationSuccessResult;



    @BeforeEach
    void setUp() {
        when(appConfig.getScoringBaseUrl()).thenReturn("https://scoringdevtest.credable.ionull");
        when(appConfig.getTransactionsUrl()).thenReturn("http://localhost:8080/api/v1/transaction-data");
        when(appConfig.getTransactionApiServiceName()).thenReturn("LMS Transaction Service");
        when(appConfig.getTransactionApiPassword()).thenReturn("lms_pass_123");

        when(appConfig.getScoringClientCreatePath()).thenReturn("/createClient");

        successResult = TestUtil.createSuccessHttpResult(TestUtil.TEST_SCORING_TOKEN);
        failureResult = TestUtil.createFailureHttpResult(500, "Internal Server Error");
        scoringResponse = TestUtil.createScoringResponse();

        ClientRegistrationResponse clientRegistrationResponse = TestUtil.createClientRegistrationResponse();
        registrationSuccessResult = TestUtil.createSuccessHttpResult(clientRegistrationResponse);
    }



    private void stubClientRegistration() {
        when(restClientService.sendRequest(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpHeaders.class),
                any(ClientRegistrationRequest.class),
                eq(ClientRegistrationResponse.class)))
                .thenReturn(registrationSuccessResult);
    }



    @Test
    void initXmlMapper_shouldCallRegisterClient_whenPostConstructExecutes() {
        var clientResponse = TestUtil.createClientRegistrationResponse();
        HttpResultDto result = TestUtil.createSuccessHttpResult(clientResponse);

        when(restClientService.sendRequest(anyString(), eq(HttpMethod.POST), any(HttpHeaders.class),
                any(ClientRegistrationRequest.class), eq(com.interview.lender.dto.ClientRegistrationResponse.class)))
                .thenReturn(result);

        scoringService.initXmlMapper();

        verify(restClientService).sendRequest(
                contains("createClient"),
                eq(HttpMethod.POST),
                any(HttpHeaders.class),
                any(ClientRegistrationRequest.class),
                eq(com.interview.lender.dto.ClientRegistrationResponse.class)
        );
    }



    @Test
    void initiateScoring_shouldReturnToken_whenRequestIsSuccessful() {
        stubClientRegistration();

        when(restClientService.sendRequest(anyString(), eq(HttpMethod.GET), any(HttpHeaders.class), isNull(), eq(String.class)))
                .thenReturn(successResult);

        var token = scoringService.initiateScoring(TestUtil.TEST_CUSTOMER_NUMBER);

        assertEquals(TestUtil.TEST_SCORING_TOKEN, token);

        verify(restClientService).sendRequest(
                endsWith("/" + TestUtil.TEST_CUSTOMER_NUMBER),
                eq(HttpMethod.GET),
                any(HttpHeaders.class),
                isNull(),
                eq(String.class)
        );
    }



    @Test
    void initiateScoring_shouldThrowException_whenRequestFails() {
        stubClientRegistration();

        when(restClientService.sendRequest(anyString(), eq(HttpMethod.GET), any(HttpHeaders.class), isNull(), eq(String.class)))
                .thenReturn(failureResult);

        var exception = assertThrows(ExternalServiceException.class,
                () -> scoringService.initiateScoring(TestUtil.TEST_CUSTOMER_NUMBER));

        assertEquals("Failed to initiate scoring: Internal Server Error", exception.getMessage());
    }



    @Test
    void getScore_shouldReturnScoringResponse_whenScoreIsReady() {
        stubClientRegistration();

        HttpResultDto scoringResult = TestUtil.createSuccessHttpResult(scoringResponse);
        when(restClientService.sendRequest(anyString(), eq(HttpMethod.GET), any(HttpHeaders.class), isNull(), eq(ScoringResponse.class)))
                .thenReturn(scoringResult);

        var result = scoringService.getScore(TestUtil.TEST_SCORING_TOKEN);

        assertTrue(result.isPresent());
        assertEquals(TestUtil.TEST_CREDIT_SCORE, result.get().getScore());
        assertEquals(TestUtil.TEST_CREDIT_LIMIT, result.get().getLimitAmount());
        assertEquals("No Exclusion", result.get().getExclusion());
    }



    @Test
    void getScore_shouldReturnEmpty_whenScoreNotReady() {
        stubClientRegistration();

        HttpResultDto notFoundResult = TestUtil.createFailureHttpResult(404, "Not Found");
        when(restClientService.sendRequest(anyString(), eq(HttpMethod.GET), any(HttpHeaders.class), isNull(), eq(ScoringResponse.class)))
                .thenReturn(notFoundResult);

        var result = scoringService.getScore(TestUtil.TEST_SCORING_TOKEN);

        assertFalse(result.isPresent());
    }



    @Test
    void getScore_shouldReturnEmpty_whenNoContent() {
        stubClientRegistration();

        HttpResultDto noContentResult = TestUtil.createFailureHttpResult(HttpStatus.NO_CONTENT.value(), "No Content");
        when(restClientService.sendRequest(anyString(), eq(HttpMethod.GET), any(HttpHeaders.class), isNull(), eq(ScoringResponse.class)))
                .thenReturn(noContentResult);

        var result = scoringService.getScore(TestUtil.TEST_SCORING_TOKEN);

        assertFalse(result.isPresent());
    }



    @Test
    void getScore_shouldThrowException_whenServerError() {
        stubClientRegistration();

        when(restClientService.sendRequest(anyString(), eq(HttpMethod.GET), any(HttpHeaders.class), isNull(), eq(ScoringResponse.class)))
                .thenReturn(failureResult);

        var exception = assertThrows(ExternalServiceException.class, () -> scoringService.getScore(TestUtil.TEST_SCORING_TOKEN));

        assertEquals("Failed to get score: Internal Server Error", exception.getMessage());
    }



    @Test
    void registerClient_shouldSetClientToken_whenRegistrationIsSuccessful() {
        when(restClientService.sendRequest(anyString(), eq(HttpMethod.POST), any(HttpHeaders.class), any(ClientRegistrationRequest.class), eq(ClientRegistrationResponse.class)))
                .thenReturn(registrationSuccessResult);

        scoringService.registerClient();

        verify(restClientService).sendRequest(
                contains("createClient"),
                eq(HttpMethod.POST),
                any(HttpHeaders.class),
                any(ClientRegistrationRequest.class),
                eq(ClientRegistrationResponse.class)
        );
    }



    @Test
    void registerClient_shouldThrowException_whenRegistrationFails() {
        when(restClientService.sendRequest(anyString(), eq(HttpMethod.POST), any(HttpHeaders.class), any(ClientRegistrationRequest.class), eq(ClientRegistrationResponse.class)))
                .thenReturn(failureResult);

        var exception = assertThrows(ExternalServiceException.class, () -> scoringService.registerClient());

        assertEquals("Failed to register client: Internal Server Error", exception.getMessage());
    }
}