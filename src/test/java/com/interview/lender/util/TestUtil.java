package com.interview.lender.util;

import com.interview.lender.dto.*;
import com.interview.lender.entity.Customer;
import com.interview.lender.entity.Loan;
import com.interview.lender.enums.LoanStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public class TestUtil {

    public static final String TEST_CUSTOMER_NUMBER = "234774784";
    public static final String TEST_CUSTOMER_NUMBER_2 = "318411216";
    public static final String TEST_CUSTOMER_NUMBER_INVALID = "999999999";
    public static final String TEST_SCORING_TOKEN = "scoring-token-123";
    public static final String TEST_CLIENT_TOKEN = "client-token-123";
    public static final String TEST_URL = "https://api.example.com/test";
    public static final String TEST_KYC_SOAP_URL = "https://kycapidevtest.credable.io/service";
    public static final String TEST_TRANSACTION_URL = "https://trxapidevtest.credable.io/service";
    public static final String TEST_AUTH_TOKEN = "Bearer token123";
    public static final BigDecimal TEST_LOAN_AMOUNT = new BigDecimal("15000");
    public static final BigDecimal TEST_APPROVED_AMOUNT = new BigDecimal("12000");
    public static final BigDecimal TEST_CREDIT_LIMIT = new BigDecimal("50000");
    public static final Integer TEST_CREDIT_SCORE = 650;
    public static final String TEST_CBS_USERNAME = "admin";
    public static final String TEST_CBS_PASSWORD = "pwd123";

    public static final String MOCK_KYC_XML_RESPONSE = """
            <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
                <SOAP-ENV:Header/>
                <SOAP-ENV:Body>
                    <ns2:CustomerResponse xmlns:ns2="http://credable.io/cbs/customer">
                        <ns2:customer>
                            <ns2:createdAt>2025-04-12T08:43:51.694Z</ns2:createdAt>
                            <ns2:createdDate>2025-04-12T08:43:51.694Z</ns2:createdDate>
                            <ns2:customerNumber>234774784</ns2:customerNumber>
                            <ns2:dob>1970-06-25T00:00:00.000Z</ns2:dob>
                            <ns2:email>user1@example.com</ns2:email>
                            <ns2:firstName>John</ns2:firstName>
                            <ns2:gender>FEMALE</ns2:gender>
                            <ns2:id>1</ns2:id>
                            <ns2:idNumber>ID9368586</ns2:idNumber>
                            <ns2:idType>DRIVERS_LICENSE</ns2:idType>
                            <ns2:lastName>Doe</ns2:lastName>
                            <ns2:middleName>A.</ns2:middleName>
                            <ns2:mobile>255724197449</ns2:mobile>
                            <ns2:monthlyIncome>7216.72</ns2:monthlyIncome>
                            <ns2:status>ACTIVE</ns2:status>
                            <ns2:updatedAt>2025-04-12T08:43:51.694Z</ns2:updatedAt>
                        </ns2:customer>
                    </ns2:CustomerResponse>
                </SOAP-ENV:Body>
            </SOAP-ENV:Envelope>
            """;

    public static final String MOCK_KYC_XML_RESPONSE_WITHOUT_CUSTOMER_DETAILS = """
            <?xml version="1.0" encoding="UTF-8"?>
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                <soap:Body>
                    <ns:getCustomerResponse xmlns:ns="http://service.credable.io/">
                        <status>NOT_FOUND</status>
                        <message>Customer not found</message>
                    </ns:getCustomerResponse>
                </soap:Body>
            </soap:Envelope>
            """;

    public static final String MOCK_TRANSACTION_XML_RESPONSE = """
            <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
                <SOAP-ENV:Header/>
                <SOAP-ENV:Body>
                    <ns2:TransactionsResponse xmlns:ns2="http://credable.io/cbs/transaction"/>
                </SOAP-ENV:Body>
            </SOAP-ENV:Envelope>
            """;

    public static final String MOCK_TRANSACTION_XML_RESPONSE_EMPTY = """
            <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
                <SOAP-ENV:Header/>
                <SOAP-ENV:Body>
                    <ns2:TransactionsResponse xmlns:ns2="http://credable.io/cbs/transaction"/>
                </SOAP-ENV:Body>
            </SOAP-ENV:Envelope>
            """;

    public static final String MOCK_TRANSACTIONS_JSON_RESPONSE = """
            [
                {
                    "accountNumber": "332216783322167555621628",
                    "alternativechanneltrnscrAmount": 87988.2441,
                    "alternativechanneltrnscrNumber": 0,
                    "alternativechanneltrnsdebitAmount": 675.3423,
                    "alternativechanneltrnsdebitNumber": 902403930,
                    "atmTransactionsNumber": 4812921,
                    "atmtransactionsAmount": 561.96661249,
                    "bouncedChequesDebitNumber": 8,
                    "bouncedchequescreditNumber": 0,
                    "bouncedchequetransactionscrAmount": 748011.19,
                    "bouncedchequetransactionsdrAmount": 43345.569028578,
                    "chequeDebitTransactionsAmount": 4.6933076819151E8,
                    "chequeDebitTransactionsNumber": 44,
                    "createdAt": 740243532000,
                    "createdDate": 1196266216000,
                    "credittransactionsAmount": 609.297663,
                    "debitcardpostransactionsAmount": 21.134264,
                    "debitcardpostransactionsNumber": 502,
                    "fincominglocaltransactioncrAmount": 0.0,
                    "id": 2,
                    "incominginternationaltrncrAmount": 70.52733936,
                    "incominginternationaltrncrNumber": 9,
                    "incominglocaltransactioncrNumber": 876,
                    "intrestAmount": 310118,
                    "lastTransactionDate": 1169339429000,
                    "lastTransactionType": null,
                    "lastTransactionValue": 3,
                    "monthlyBalance": 6.59722841E8,
                    "monthlydebittransactionsAmount": 103262.90429936,
                    "mobilemoneycredittransactionAmount": 0.0,
                    "mobilemoneycredittransactionNumber": 946843,
                    "mobilemoneydebittransactionAmount": 0.0,
                    "mobilemoneydebittransactionNumber": 5523407,
                    "outgoinginttransactiondebitAmount": 5.473303560725E7,
                    "outgoinginttrndebitNumber": 646,
                    "outgoinglocaltransactiondebitAmount": 565972.1236,
                    "outgoinglocaltransactiondebitNumber": 2971,
                    "overdraftLimit": 0.0,
                    "overthecounterwithdrawalsAmount": 332.0,
                    "overthecounterwithdrawalsNumber": 87569,
                    "transactionValue": 1.0,
                    "updatedAt": 773556430000
                }
            ]
            """;



    public static SubscriptionRequest createSubscriptionRequest() {
        SubscriptionRequest request = new SubscriptionRequest();
        request.setCustomerNumber(TEST_CUSTOMER_NUMBER);
        return request;
    }



    public static LoanRequest createLoanRequest() {
        LoanRequest request = new LoanRequest();
        request.setCustomerNumber(TEST_CUSTOMER_NUMBER);
        request.setAmount(TEST_LOAN_AMOUNT);
        return request;
    }



    public static CustomerDto createCustomerDto() {
        return CustomerDto.builder()
                .customerNumber(TEST_CUSTOMER_NUMBER)
                .firstName("John")
                .lastName("Doe")
                .mobile("+254700000000")
                .email("john.doe@example.com")
                .gender("M")
                .idNumber("12345678")
                .idType("NATIONAL_ID")
                .monthlyIncome(5000.00)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();
    }



    public static Customer createCustomerEntity() {
        return Customer.builder()
                .customerNumber(TEST_CUSTOMER_NUMBER)
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("+254700000000")
                .email("john.doe@example.com")
                .isActive(true)
                .scoringToken(TEST_SCORING_TOKEN)
                .registrationDate(LocalDateTime.now())
                .build();
    }



    public static Loan createLoanEntity() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setCustomerNumber(TEST_CUSTOMER_NUMBER);
        loan.setRequestedAmount(TEST_LOAN_AMOUNT);
        loan.setStatus(LoanStatus.PENDING);
        loan.setApplicationDate(LocalDateTime.now());
        loan.setRetryCount(0);
        return loan;
    }



    public static Loan createApprovedLoanEntity() {
        Loan loan = createLoanEntity();
        loan.setStatus(LoanStatus.APPROVED);
        loan.setApprovedAmount(TEST_APPROVED_AMOUNT);
        loan.setCreditScore(TEST_CREDIT_SCORE);
        loan.setCreditLimit(TEST_CREDIT_LIMIT);
        loan.setExclusion("No Exclusion");
        loan.setApprovalDate(LocalDateTime.now());
        return loan;
    }



    public static Loan createScoringInProgressLoanEntity() {
        Loan loan = createLoanEntity();
        loan.setStatus(LoanStatus.SCORING_IN_PROGRESS);
        return loan;
    }



    public static ScoringResponse createScoringResponse() {
        ScoringResponse response = new ScoringResponse();
        response.setId(1L);
        response.setCustomerNumber(TEST_CUSTOMER_NUMBER);
        response.setScore(TEST_CREDIT_SCORE);
        response.setLimitAmount(TEST_CREDIT_LIMIT);
        response.setExclusion("No Exclusion");
        response.setExclusionReason(null);
        return response;
    }



    public static ClientRegistrationResponse createClientRegistrationResponse() {
        ClientRegistrationResponse response = new ClientRegistrationResponse();
        response.setId(1L);
        response.setUrl("http://localhost:8080/api/v1/transaction-data");
        response.setName("LMS Transaction Service");
        response.setUsername("lms_user");
        response.setPassword("lms_pass_123");
        response.setToken(TEST_CLIENT_TOKEN);
        return response;
    }



    public static TransactionDto createTransactionDto() {
        TransactionDto transaction = new TransactionDto();
        transaction.setAccountNumber("332216783322167555621628");
        transaction.setAlternativechanneltrnscrAmount(new BigDecimal("87988.2441"));
        transaction.setAlternativechanneltrnscrNumber(0);
        transaction.setAlternativechanneltrnsdebitAmount(new BigDecimal("675.3423"));
        transaction.setAlternativechanneltrnsdebitNumber(902403930);
        transaction.setAtmTransactionsNumber(4812921);
        transaction.setAtmtransactionsAmount(new BigDecimal("561.96661249"));
        transaction.setBouncedChequesDebitNumber(8);
        transaction.setBouncedchequescreditNumber(0);
        transaction.setBouncedchequetransactionscrAmount(new BigDecimal("748011.19"));
        transaction.setBouncedchequetransactionsdrAmount(new BigDecimal("43345.569028578"));
        transaction.setChequeDebitTransactionsAmount(new BigDecimal("4.6933076819151E8"));
        transaction.setChequeDebitTransactionsNumber(44);
        transaction.setCreatedAt(740243532000L);
        transaction.setCreatedDate(1196266216000L);
        transaction.setCredittransactionsAmount(new BigDecimal("609.297663"));
        transaction.setDebitcardpostransactionsAmount(new BigDecimal("21.134264"));
        transaction.setDebitcardpostransactionsNumber(502);
        transaction.setFincominglocaltransactioncrAmount(new BigDecimal("0.0"));
        transaction.setId(2);
        transaction.setIncominginternationaltrncrAmount(new BigDecimal("70.52733936"));
        transaction.setIncominginternationaltrncrNumber(9);
        transaction.setIncominglocaltransactioncrNumber(876);
        transaction.setIntrestAmount(new BigDecimal("310118"));
        transaction.setLastTransactionDate(1169339429000L);
        transaction.setLastTransactionType(null);
        transaction.setLastTransactionValue(3);
        transaction.setMonthlyBalance(new BigDecimal("6.59722841E8"));
        transaction.setMonthlydebittransactionsAmount(new BigDecimal("103262.90429936"));
        transaction.setMobilemoneycredittransactionAmount(new BigDecimal("0.0"));
        transaction.setMobilemoneycredittransactionNumber(946843);
        transaction.setMobilemoneydebittransactionAmount(new BigDecimal("0.0"));
        transaction.setMobilemoneydebittransactionNumber(5523407);
        transaction.setOutgoinginttransactiondebitAmount(new BigDecimal("5.473303560725E7"));
        transaction.setOutgoinginttrndebitNumber(646);
        transaction.setOutgoinglocaltransactiondebitAmount(new BigDecimal("565972.1236"));
        transaction.setOutgoinglocaltransactiondebitNumber(2971);
        transaction.setOverdraftLimit(new BigDecimal("0.0"));
        transaction.setOverthecounterwithdrawalsAmount(new BigDecimal("332.0"));
        transaction.setOverthecounterwithdrawalsNumber(87569);
        transaction.setTransactionValue(new BigDecimal("1.0"));
        transaction.setUpdatedAt(773556430000L);
        return transaction;
    }



    public static HttpResultDto createSuccessHttpResult(Object message) {
        return HttpResultDto.builder()
                .success(true)
                .statusCode(200)
                .message(message)
                .headers(new HttpHeaders())
                .build();
    }



    public static HttpResultDto createFailureHttpResult(int statusCode, String error) {
        return HttpResultDto.builder()
                .success(false)
                .statusCode(statusCode)
                .error(error)
                .build();
    }



    public static ResponseDto createSuccessResponseDto(String message, Object payload) {
        return ResponseDto.builder()
                .success(true)
                .message(message)
                .httpStatus(HttpStatus.OK)
                .data(payload)
                .build();
    }



    public static ResponseDto createFailureResponseDto(String message, HttpStatus status) {
        return ResponseDto.builder()
                .success(false)
                .message(message)
                .httpStatus(status)
                .build();
    }



    public static SubscriptionResponse createSubscriptionResponse() {
        return SubscriptionResponse.builder()
                .customerNumber(TEST_CUSTOMER_NUMBER)
                .customerName("John Doe")
                .phoneNumber("+254700000000")
                .email("john.doe@example.com")
                .build();
    }



    public static LoanResponse createLoanResponse() {
        return LoanResponse.builder()
                .loanId(1L)
                .customerNumber(TEST_CUSTOMER_NUMBER)
                .requestedAmount(TEST_LOAN_AMOUNT)
                .status("PENDING")
                .applicationDate(LocalDateTime.now())
                .build();
    }



    public static LoanStatusResponse createLoanStatusResponse() {
        return LoanStatusResponse.builder()
                .loanId(1L)
                .customerNumber(TEST_CUSTOMER_NUMBER)
                .requestedAmount(TEST_LOAN_AMOUNT)
                .approvedAmount(TEST_APPROVED_AMOUNT)
                .status("APPROVED")
                .creditScore(TEST_CREDIT_SCORE)
                .creditLimit(TEST_CREDIT_LIMIT)
                .exclusion("No Exclusion")
                .applicationDate(LocalDateTime.now())
                .approvalDate(LocalDateTime.now())
                .build();
    }



    public static HttpHeaders createTestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", TEST_AUTH_TOKEN);
        headers.set("X-Custom-Header", "test-value");
        return headers;
    }



    public static void assertSuccessResponse(ResponseDto response) {
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getMessage());
        assertNotNull(response.getHttpStatus());
    }



    public static void assertFailureResponse(ResponseDto response) {
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());
        assertNotNull(response.getHttpStatus());
    }



    public static void assertHttpResultSuccess(HttpResultDto result) {
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getMessage());
        assertEquals(200, result.getStatusCode());
    }



    public static void assertHttpResultFailure(HttpResultDto result, int expectedStatusCode) {
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
        assertEquals(expectedStatusCode, result.getStatusCode());
    }



    private static void assertNotNull(Object object) {
        org.junit.jupiter.api.Assertions.assertNotNull(object);
    }



    private static void assertTrue(boolean condition) {
        org.junit.jupiter.api.Assertions.assertTrue(condition);
    }



    private static void assertFalse(boolean condition) {
        org.junit.jupiter.api.Assertions.assertFalse(condition);
    }



    private static void assertEquals(Object expected, Object actual) {
        org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
    }
}
