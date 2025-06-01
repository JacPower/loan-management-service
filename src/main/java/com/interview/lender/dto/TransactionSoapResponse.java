package com.interview.lender.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "Envelope", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
public class TransactionSoapResponse {

    @JacksonXmlProperty(localName = "Body")
    private Body body;



    public List<TransactionDto> getTransactions() {
        if (body != null && body.transactionsResponse != null) {
            return body.transactionsResponse.transactions;
        }
        return List.of();
    }



    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {

        @JacksonXmlProperty(localName = "TransactionsResponse", namespace = "http://credable.io/cbs/transaction")
        private TransactionsResponse transactionsResponse;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransactionsResponse {

        @JacksonXmlProperty(localName = "transactions", namespace = "http://credable.io/cbs/transaction")
        private List<TransactionDto> transactions;
    }
}
