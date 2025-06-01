package com.interview.lender.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.interview.lender.config.AppConfig;
import com.interview.lender.dto.HttpResultDto;
import com.interview.lender.dto.ResponseDto;
import com.interview.lender.dto.TransactionDto;
import com.interview.lender.dto.TransactionSoapResponse;
import com.interview.lender.util.Util;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.interview.lender.util.Constants.MOCK_TRANSACTIONS_RESPONSE;
import static com.interview.lender.util.Constants.TRANSACTIONS_REQUEST_TEMPLATE;
import static org.springframework.http.HttpStatus.OK;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final AppConfig appConfig;
    private final RestClientService restClientService;
    private final XmlMapper xmlMapper = new XmlMapper();



    @PostConstruct
    public void initXmlMapper() {
        xmlMapper.registerModule(new JavaTimeModule());
    }



    public ResponseDto getTransactionHistory(String customerNumber) {
        String soapRequest = buildSoapRequest(customerNumber);
        String url = appConfig.getCbsTransactionUrl();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        headers.set("SOAPAction", "");
        headers.set("Accept", "text/xml, application/xml");
        HttpResultDto result = restClientService.sendRequest(url, null, headers, soapRequest, String.class);

        List<TransactionDto> transactions = extractTransactions(result, customerNumber);

        return Util.buildSuccessResponse("Transaction history data", transactions, OK);
    }



    private String buildSoapRequest(String customerNumber) {
        return String.format(
                TRANSACTIONS_REQUEST_TEMPLATE,
                appConfig.getCbsUsername(),
                appConfig.getCbsPassword(),
                customerNumber
        );
    }



    private List<TransactionDto> extractTransactions(HttpResultDto result, String customerNumber) {
        if (result.isSuccess()) {
            try {
                TransactionSoapResponse response = xmlMapper.readValue(result.getMessage().toString(), TransactionSoapResponse.class);

                if (response != null && response.getTransactions() != null) return response.getTransactions();
                log.warn("SOAP response returned with no transactions | CustomerNumber: {}", customerNumber);

            } catch (JsonProcessingException e) {
                log.error("Failed to parse SOAP XML response | CustomerNumber: {}", customerNumber, e);
            }
        } else {
            log.error("CBS transaction request failed, returning mock data | CustomerNumber: {}", customerNumber);
        }

        return getMockTransactionHistory();
    }



    public List<TransactionDto> getMockTransactionHistory() {
        try {
            return new ObjectMapper().readValue(MOCK_TRANSACTIONS_RESPONSE, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.error("Failed to parse mock transactions: ", e);
            return List.of();
        }
    }
}
