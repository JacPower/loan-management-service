package com.interview.lender.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpHeaders;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class HttpResultDto {
    private HttpHeaders headers;
    private int statusCode;
    @Builder.Default
    private Object message = "{}";
    @Builder.Default
    private String error = "";
    @Builder.Default
    private boolean success = false;
}
