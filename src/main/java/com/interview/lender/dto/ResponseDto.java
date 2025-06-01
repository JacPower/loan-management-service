package com.interview.lender.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;


@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto {
    @Builder.Default
    @JsonIgnore
    private HttpStatus httpStatus = BAD_REQUEST;

    @Builder.Default
    private int resultCode = BAD_REQUEST.value();

    @Builder.Default
    private boolean success = false;

    private String message;

    private Object data;

    private Object errors;
}
