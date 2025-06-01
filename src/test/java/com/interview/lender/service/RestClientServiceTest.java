package com.interview.lender.service;

import com.interview.lender.services.RestClientService;
import com.interview.lender.util.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestClientServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RestClientService restClientService;

    private HttpHeaders headers;
    private String requestPayload;
    private String responseBody;
    private ResponseEntity<String> successResponseEntity;



    @BeforeEach
    void setUp() {
        headers = TestUtil.createTestHeaders();
        requestPayload = "{\"test\": \"data\"}";
        responseBody = "{\"result\": \"success\"}";
        successResponseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
    }



    @Test
    void sendRequest_shouldReturnSuccessResult_whenPostRequestSucceeds() {
        when(restTemplate.postForEntity(eq(TestUtil.TEST_URL), any(HttpEntity.class), eq(String.class)))
                .thenReturn(successResponseEntity);

        var result = restClientService.sendRequest(TestUtil.TEST_URL, null, headers, requestPayload, String.class);

        TestUtil.assertHttpResultSuccess(result);
        assertEquals(responseBody, result.getMessage());
        verify(restTemplate).postForEntity(eq(TestUtil.TEST_URL), any(HttpEntity.class), eq(String.class));
    }



    @Test
    void sendRequest_shouldReturnSuccessResult_whenGetRequestSucceeds() {
        when(restTemplate.exchange(eq(TestUtil.TEST_URL), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(successResponseEntity);

        var result = restClientService.sendRequest(TestUtil.TEST_URL, HttpMethod.GET, headers, null, String.class);

        TestUtil.assertHttpResultSuccess(result);
        assertEquals(responseBody, result.getMessage());
        verify(restTemplate).exchange(eq(TestUtil.TEST_URL), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));
    }



    @Test
    void sendRequest_shouldReturnFailureResult_whenHttpClientErrorExceptionOccurs() {
        String errorBody = "Bad Request Error";
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", errorBody.getBytes(), null);

        when(restTemplate.postForEntity(eq(TestUtil.TEST_URL), any(HttpEntity.class), eq(String.class)))
                .thenThrow(exception);

        var result = restClientService.sendRequest(TestUtil.TEST_URL, null, headers, requestPayload, String.class);

        TestUtil.assertHttpResultFailure(result, 400);
        assertEquals(errorBody, result.getError());
    }



    @Test
    void sendRequest_shouldReturnFailureResult_whenRestClientExceptionOccurs() {
        String errorMessage = "Connection timeout";
        RestClientException exception = new RestClientException(errorMessage);

        when(restTemplate.postForEntity(eq(TestUtil.TEST_URL), any(HttpEntity.class), eq(String.class)))
                .thenThrow(exception);

        var result = restClientService.sendRequest(TestUtil.TEST_URL, null, headers, requestPayload, String.class);

        TestUtil.assertHttpResultFailure(result, 500);
        assertEquals(errorMessage, result.getError());
    }



    @Test
    void sendRequest_shouldCreateCorrectHttpEntity_withPayloadAndHeaders() {
        when(restTemplate.postForEntity(eq(TestUtil.TEST_URL), any(HttpEntity.class), eq(String.class)))
                .thenReturn(successResponseEntity);

        restClientService.sendRequest(TestUtil.TEST_URL, null, headers, requestPayload, String.class);

        verify(restTemplate).postForEntity(eq(TestUtil.TEST_URL), argThat(entity -> {
            HttpEntity<?> httpEntity = (HttpEntity<?>) entity;
            Assertions.assertNotNull(httpEntity.getBody());
            return httpEntity.getBody().equals(requestPayload) &&
                   Objects.equals(httpEntity.getHeaders().getContentType(), MediaType.APPLICATION_JSON) &&
                   Objects.equals(httpEntity.getHeaders().getFirst("Authorization"), TestUtil.TEST_AUTH_TOKEN);
        }), eq(String.class));
    }



    @Test
    void sendRequest_shouldUseExchangeMethod_whenHttpMethodIsProvided() {
        when(restTemplate.exchange(eq(TestUtil.TEST_URL), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(String.class)))
                .thenReturn(successResponseEntity);

        var result = restClientService.sendRequest(TestUtil.TEST_URL, HttpMethod.DELETE, headers, null, String.class);

        assertTrue(result.isSuccess());
        verify(restTemplate).exchange(eq(TestUtil.TEST_URL), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(String.class));
        verify(restTemplate, never()).postForEntity(anyString(), any(), any());
    }



    @Test
    void sendRequest_shouldUsePostForEntity_whenHttpMethodIsNull() {
        when(restTemplate.postForEntity(eq(TestUtil.TEST_URL), any(HttpEntity.class), eq(String.class)))
                .thenReturn(successResponseEntity);

        var result = restClientService.sendRequest(TestUtil.TEST_URL, null, headers, requestPayload, String.class);

        assertTrue(result.isSuccess());
        verify(restTemplate).postForEntity(eq(TestUtil.TEST_URL), any(HttpEntity.class), eq(String.class));
        verify(restTemplate, never()).exchange(anyString(), any(HttpMethod.class), any(), any(Class.class));
    }



    @Test
    void sendRequest_shouldHandleNullHeaders() {
        when(restTemplate.postForEntity(eq(TestUtil.TEST_URL), any(HttpEntity.class), eq(String.class)))
                .thenReturn(successResponseEntity);

        var result = restClientService.sendRequest(TestUtil.TEST_URL, null, null, requestPayload, String.class);

        assertTrue(result.isSuccess());
        verify(restTemplate).postForEntity(eq(TestUtil.TEST_URL), any(HttpEntity.class), eq(String.class));
    }
}
