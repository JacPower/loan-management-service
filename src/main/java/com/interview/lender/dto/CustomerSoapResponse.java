package com.interview.lender.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "Envelope", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
public class CustomerSoapResponse {

    @JacksonXmlProperty(localName = "Header")
    private Object header;

    @JacksonXmlProperty(localName = "Body")
    private Body body;



    public Optional<CustomerDto> getCustomer() {
        if (body != null && body.customerResponse != null) {
            return Optional.ofNullable(body.customerResponse.customer);
        }
        return Optional.empty();
    }



    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {

        @JacksonXmlProperty(localName = "CustomerResponse")
        private CustomerResponse customerResponse;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CustomerResponse {

        @JacksonXmlProperty(localName = "customer")
        private CustomerDto customer;
    }


}
