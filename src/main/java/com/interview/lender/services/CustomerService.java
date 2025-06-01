package com.interview.lender.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.interview.lender.config.AppConfig;
import com.interview.lender.dto.CustomerDto;
import com.interview.lender.dto.CustomerSoapResponse;
import com.interview.lender.dto.HttpResultDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.interview.lender.util.Constants.KYC_REQUEST_TEMPLATE;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final AppConfig appConfig;
    private final XmlMapper xmlMapper = new XmlMapper();
    private final RestClientService restClientService;



    @PostConstruct
    public void initXmlMapper() {
        xmlMapper.registerModule(new JavaTimeModule());
    }



    public Optional<CustomerDto> getCustomerByNumber(String customerNumber) {
        String soapRequest = buildSoapRequest(customerNumber);
        String url = appConfig.getCbsKycUrl();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        headers.set("SOAPAction", "");
        headers.set("Accept", "text/xml, application/xml");
        HttpResultDto result = restClientService.sendRequest(url, null, headers, soapRequest, String.class);

        if (result.isSuccess()) {
            try {
                CustomerSoapResponse customerSoapResponse = xmlMapper.readValue(result.getMessage().toString(), CustomerSoapResponse.class);
                return customerSoapResponse.getCustomer();
            } catch (JsonProcessingException e) {
                log.error("Failed to parse XML to DTO | CustomerNumber: {}", customerNumber, e);
                return Optional.empty();
            }
        }

        return Optional.empty();
    }



    private String buildSoapRequest(String customerNumber) {
        String username = appConfig.getCbsUsername();
        String password = appConfig.getCbsPassword();

        return String.format(KYC_REQUEST_TEMPLATE, username, password, customerNumber);
    }
}