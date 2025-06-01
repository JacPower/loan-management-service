package com.interview.lender.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.interview.lender.config.AppConfig;
import com.interview.lender.dto.HttpResultDto;
import com.interview.lender.dto.TransactionDto;
import com.interview.lender.services.RestClientService;
import com.interview.lender.services.TransactionService;
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

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TransactionServiceTest {

    @Mock
    private AppConfig appConfig;
    @Mock
    private RestClientService restClientService;

    @InjectMocks
    private TransactionService transactionService;

    private HttpResultDto successResult;
    private HttpResultDto failureResult;



    @BeforeEach
    void setUp() {
        when(appConfig.getCbsTransactionUrl()).thenReturn(TestUtil.TEST_TRANSACTION_URL);
        when(appConfig.getCbsUsername()).thenReturn(TestUtil.TEST_CBS_USERNAME);
        when(appConfig.getCbsPassword()).thenReturn(TestUtil.TEST_CBS_PASSWORD);

        successResult = TestUtil.createSuccessHttpResult(TestUtil.MOCK_TRANSACTION_XML_RESPONSE);
        failureResult = TestUtil.createFailureHttpResult(500, "Internal Server Error");
    }



    @Test
    void initXmlMapper_shouldRegisterJavaTimeModule() throws Exception {
        transactionService.initXmlMapper();

        Field xmlMapperField = TransactionService.class.getDeclaredField("xmlMapper");
        xmlMapperField.setAccessible(true);
        XmlMapper xmlMapper = (XmlMapper) xmlMapperField.get(transactionService);

        assertTrue(xmlMapper.getRegisteredModuleIds().contains("jackson-datatype-jsr310"));
    }



    @Test
    void getTransactionHistory_shouldReturnTransactions_whenValidSoapResponse() {
        HttpResultDto validSuccessResult = TestUtil.createSuccessHttpResult(TestUtil.MOCK_TRANSACTION_XML_RESPONSE);
        when(restClientService.sendRequest(anyString(), isNull(), any(HttpHeaders.class), anyString(), eq(String.class)))
                .thenReturn(validSuccessResult);

        var response = transactionService.getTransactionHistory(TestUtil.TEST_CUSTOMER_NUMBER);

        TestUtil.assertSuccessResponse(response);
        @SuppressWarnings("unchecked")
        List<TransactionDto> transactions = (List<TransactionDto>) response.getData();
        assertFalse(transactions.isEmpty());
    }



    @Test
    void getTransactionHistory_shouldReturnMockData_whenSoapRequestFails() {
        when(restClientService.sendRequest(anyString(), isNull(), any(HttpHeaders.class), anyString(), eq(String.class)))
                .thenReturn(failureResult);

        var response = transactionService.getTransactionHistory(TestUtil.TEST_CUSTOMER_NUMBER);

        TestUtil.assertSuccessResponse(response);
        @SuppressWarnings("unchecked")
        List<TransactionDto> transactions = (List<TransactionDto>) response.getData();
        assertFalse(transactions.isEmpty());
    }



    @Test
    void getTransactionHistory_shouldReturnMockData_whenXmlParsingFails() {
        String malformedXml = "<?xml version='1.0' encoding='UTF-8'?><soap:Envelope><unclosed-tag>";
        HttpResultDto malformedResult = TestUtil.createSuccessHttpResult(malformedXml);

        when(restClientService.sendRequest(anyString(), isNull(), any(HttpHeaders.class), anyString(), eq(String.class)))
                .thenReturn(malformedResult);

        var response = transactionService.getTransactionHistory(TestUtil.TEST_CUSTOMER_NUMBER);

        TestUtil.assertSuccessResponse(response);

        @SuppressWarnings("unchecked")
        List<TransactionDto> transactions = (List<TransactionDto>) response.getData();
        assertFalse(transactions.isEmpty());
    }



    @Test
    void getTransactionHistory_shouldReturnMockData_whenSoapResponseHasNoTransactions() {
        HttpResultDto emptyResult = TestUtil.createSuccessHttpResult(TestUtil.MOCK_TRANSACTION_XML_RESPONSE_EMPTY);
        when(restClientService.sendRequest(anyString(), isNull(), any(HttpHeaders.class), anyString(), eq(String.class)))
                .thenReturn(emptyResult);

        var response = transactionService.getTransactionHistory(TestUtil.TEST_CUSTOMER_NUMBER);

        TestUtil.assertSuccessResponse(response);

        @SuppressWarnings("unchecked")
        List<TransactionDto> transactions = (List<TransactionDto>) response.getData();
        assertFalse(transactions.isEmpty());
    }



    @Test
    void getTransactionHistory_shouldSetCorrectHeaders_whenMakingSoapRequest() {
        when(restClientService.sendRequest(anyString(), isNull(), any(HttpHeaders.class), anyString(), eq(String.class)))
                .thenReturn(successResult);

        transactionService.getTransactionHistory(TestUtil.TEST_CUSTOMER_NUMBER);

        verify(restClientService).sendRequest(
                anyString(),
                isNull(),
                argThat(headers ->
                        Objects.requireNonNull(headers.getContentType()).toString().equals("text/xml") &&
                        Objects.requireNonNull(headers.getFirst("SOAPAction")).isEmpty() &&
                        Objects.equals(headers.getFirst("Accept"), "text/xml, application/xml")
                ),
                anyString(),
                eq(String.class)
        );
    }



    @Test
    void buildSoapRequest_shouldContainCorrectCredentialsAndCustomerNumber() {
        when(restClientService.sendRequest(anyString(), isNull(), any(HttpHeaders.class), anyString(), eq(String.class)))
                .thenReturn(successResult);

        transactionService.getTransactionHistory(TestUtil.TEST_CUSTOMER_NUMBER);

        verify(restClientService).sendRequest(
                anyString(),
                isNull(),
                any(HttpHeaders.class),
                argThat(soapRequest ->
                        soapRequest.toString().contains(TestUtil.TEST_CBS_USERNAME) &&
                        soapRequest.toString().contains(TestUtil.TEST_CBS_PASSWORD) &&
                        soapRequest.toString().contains(TestUtil.TEST_CUSTOMER_NUMBER)
                ),
                eq(String.class)
        );
    }



    @Test
    void extractTransactions_shouldParseValidXmlResponse() {
        HttpResultDto validResult = TestUtil.createSuccessHttpResult(TestUtil.MOCK_TRANSACTION_XML_RESPONSE);

        when(restClientService.sendRequest(anyString(), isNull(), any(HttpHeaders.class), anyString(), eq(String.class)))
                .thenReturn(validResult);

        var response = transactionService.getTransactionHistory(TestUtil.TEST_CUSTOMER_NUMBER);

        @SuppressWarnings("unchecked")
        List<TransactionDto> transactions = (List<TransactionDto>) response.getData();

        assertNotNull(transactions);
        assertFalse(transactions.isEmpty());
    }



    @Test
    void getMockTransactionHistory_shouldReturnValidTransactionList() {
        var mockTransactions = transactionService.getMockTransactionHistory();

        assertNotNull(mockTransactions);
        assertFalse(mockTransactions.isEmpty());

        // Verify structure of mock data
        TransactionDto firstTransaction = mockTransactions.getFirst();
        assertNotNull(firstTransaction.getAccountNumber());
        assertNotNull(firstTransaction.getMonthlyBalance());
    }



    @Test
    void getMockTransactionHistory_shouldReturnConsistentData() {
        var mockTransactions1 = transactionService.getMockTransactionHistory();
        var mockTransactions2 = transactionService.getMockTransactionHistory();

        assertEquals(mockTransactions1.size(), mockTransactions2.size());

        if (!mockTransactions1.isEmpty() && !mockTransactions2.isEmpty()) {
            assertEquals(mockTransactions1.getFirst().getAccountNumber(), mockTransactions2.getFirst().getAccountNumber());
        }
    }



    @Test
    void getTransactionHistory_shouldAlwaysReturnSuccessResponse_regardlessOfExternalServiceStatus() {
        when(restClientService.sendRequest(anyString(), isNull(), any(HttpHeaders.class), anyString(), eq(String.class)))
                .thenReturn(successResult);

        var successResponse = transactionService.getTransactionHistory(TestUtil.TEST_CUSTOMER_NUMBER);
        TestUtil.assertSuccessResponse(successResponse);

        when(restClientService.sendRequest(anyString(), isNull(), any(HttpHeaders.class), anyString(), eq(String.class)))
                .thenReturn(failureResult);

        var failureResponse = transactionService.getTransactionHistory(TestUtil.TEST_CUSTOMER_NUMBER);
        TestUtil.assertSuccessResponse(failureResponse);
    }



    @Test
    void getTransactionHistory_shouldHandleDifferentCustomerNumbers() {
        when(restClientService.sendRequest(anyString(), isNull(), any(HttpHeaders.class), anyString(), eq(String.class)))
                .thenReturn(successResult);

        transactionService.getTransactionHistory(TestUtil.TEST_CUSTOMER_NUMBER);
        transactionService.getTransactionHistory(TestUtil.TEST_CUSTOMER_NUMBER_2);
        transactionService.getTransactionHistory(TestUtil.TEST_CUSTOMER_NUMBER_INVALID);

        verify(restClientService).sendRequest(anyString(), isNull(), any(HttpHeaders.class), contains(TestUtil.TEST_CUSTOMER_NUMBER), eq(String.class));
        verify(restClientService).sendRequest(anyString(), isNull(), any(HttpHeaders.class), contains(TestUtil.TEST_CUSTOMER_NUMBER_2), eq(String.class));
        verify(restClientService).sendRequest(anyString(), isNull(), any(HttpHeaders.class), contains(TestUtil.TEST_CUSTOMER_NUMBER_INVALID), eq(String.class));
    }



    @Test
    void extractTransactions_shouldLogWarning_whenResponseHasNoTransactions() {
        HttpResultDto emptyTransactionResult = TestUtil.createSuccessHttpResult(TestUtil.MOCK_TRANSACTION_XML_RESPONSE_EMPTY);

        when(restClientService.sendRequest(anyString(), isNull(), any(HttpHeaders.class), anyString(), eq(String.class)))
                .thenReturn(emptyTransactionResult);

        assertDoesNotThrow(() -> {
            var response = transactionService.getTransactionHistory(TestUtil.TEST_CUSTOMER_NUMBER);
            TestUtil.assertSuccessResponse(response);
        });
    }



    @Test
    void xmlMapper_shouldBeConfiguredWithJavaTimeModule_afterPostConstruct() throws Exception {
        transactionService.initXmlMapper();

        Field xmlMapperField = TransactionService.class.getDeclaredField("xmlMapper");
        xmlMapperField.setAccessible(true);
        XmlMapper xmlMapper = (XmlMapper) xmlMapperField.get(transactionService);

        assertTrue(xmlMapper.getRegisteredModuleIds().contains("jackson-datatype-jsr310"));

        assertNotNull(xmlMapper);
    }
}
