package com.interview.lender.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.interview.lender.config.AppConfig;
import com.interview.lender.dto.CustomerDto;
import com.interview.lender.dto.HttpResultDto;
import com.interview.lender.services.CustomerService;
import com.interview.lender.services.RestClientService;
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
import java.util.Optional;

import static com.interview.lender.util.TestUtil.MOCK_KYC_XML_RESPONSE_WITHOUT_CUSTOMER_DETAILS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CustomerServiceTest {

    @Mock
    private AppConfig appConfig;
    @Mock
    private RestClientService restClientService;

    @InjectMocks
    private CustomerService customerService;

    private HttpResultDto successResult;
    private HttpResultDto failureResult;



    @BeforeEach
    void setUp() {
        when(appConfig.getCbsKycUrl()).thenReturn(TestUtil.TEST_KYC_SOAP_URL);
        when(appConfig.getCbsUsername()).thenReturn(TestUtil.TEST_CBS_USERNAME);
        when(appConfig.getCbsPassword()).thenReturn(TestUtil.TEST_CBS_PASSWORD);

        successResult = TestUtil.createSuccessHttpResult(TestUtil.MOCK_KYC_XML_RESPONSE);
        failureResult = TestUtil.createFailureHttpResult(500, "Internal Server Error");
    }



    @Test
    void initXmlMapper_shouldRegisterJavaTimeModule() throws Exception {
        customerService.initXmlMapper();

        Field xmlMapperField = CustomerService.class.getDeclaredField("xmlMapper");
        xmlMapperField.setAccessible(true);
        XmlMapper xmlMapper = (XmlMapper) xmlMapperField.get(customerService);

        assertTrue(xmlMapper.getRegisteredModuleIds().contains("jackson-datatype-jsr310"));
    }



    @Test
    void getCustomerByNumber_shouldReturnCustomer_whenValidResponse() {
        HttpResultDto validSuccessResult = TestUtil.createSuccessHttpResult(TestUtil.MOCK_KYC_XML_RESPONSE);

        when(restClientService.sendRequest(anyString(), isNull(), any(HttpHeaders.class), anyString(), eq(String.class))).thenReturn(validSuccessResult);

        Optional<CustomerDto> result = customerService.getCustomerByNumber(TestUtil.TEST_CUSTOMER_NUMBER);

        assertTrue(result.isPresent());
        assertEquals(TestUtil.TEST_CUSTOMER_NUMBER, result.get().getCustomerNumber());
        assertEquals("John", result.get().getFirstName());
        assertEquals("Doe", result.get().getLastName());
        verify(restClientService).sendRequest(eq(TestUtil.TEST_KYC_SOAP_URL), isNull(), any(HttpHeaders.class), contains(TestUtil.TEST_CUSTOMER_NUMBER), eq(String.class));
    }



    @Test
    void getCustomerByNumber_shouldReturnEmpty_whenRequestFails() {
        when(restClientService.sendRequest(anyString(), isNull(), any(HttpHeaders.class), anyString(), eq(String.class))).thenReturn(failureResult);

        var result = customerService.getCustomerByNumber(TestUtil.TEST_CUSTOMER_NUMBER);

        assertFalse(result.isPresent());
    }



    @Test
    void getCustomerByNumber_shouldReturnEmpty_whenXmlParsingFails() {
        String malformedXml = "<?xml version='1.0' encoding='UTF-8'?><soap:Envelope><unclosed-tag>";
        HttpResultDto malformedResult = TestUtil.createSuccessHttpResult(malformedXml);

        when(restClientService.sendRequest(anyString(), isNull(), any(HttpHeaders.class), anyString(), eq(String.class))).thenReturn(malformedResult);

        var result = customerService.getCustomerByNumber(TestUtil.TEST_CUSTOMER_NUMBER);

        assertFalse(result.isPresent());
    }



    @Test
    void getCustomerByNumber_shouldReturnEmpty_whenCustomerNotFoundInResponse() {
        HttpResultDto emptyResult = TestUtil.createSuccessHttpResult(MOCK_KYC_XML_RESPONSE_WITHOUT_CUSTOMER_DETAILS);
        when(restClientService.sendRequest(anyString(), isNull(), any(HttpHeaders.class), anyString(), eq(String.class))).thenReturn(emptyResult);

        var result = customerService.getCustomerByNumber(TestUtil.TEST_CUSTOMER_NUMBER_INVALID);

        assertFalse(result.isPresent());
    }



    @Test
    void buildSoapRequest_shouldContainCorrectCredentialsAndCustomerNumber() {
        when(restClientService.sendRequest(anyString(), isNull(), any(HttpHeaders.class), anyString(), eq(String.class))).thenReturn(successResult);

        customerService.getCustomerByNumber(TestUtil.TEST_CUSTOMER_NUMBER);

        verify(restClientService).sendRequest(anyString(), isNull(), any(HttpHeaders.class),
                argThat(soapRequest -> soapRequest.toString().contains(TestUtil.TEST_CBS_USERNAME)
                                       && soapRequest.toString().contains(TestUtil.TEST_CBS_PASSWORD)
                                       && soapRequest.toString().contains(TestUtil.TEST_CUSTOMER_NUMBER)),
                eq(String.class));
    }
}
