package com.interview.lender.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto {
    @Builder.Default
    @JsonIgnore
    private HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    @Builder.Default
    private int resultCode = HttpStatus.BAD_REQUEST.value();

    @Builder.Default
    private boolean success = false;

    @Builder.Default
    private String message = "Request failed";

    private Object data;
    private Object errors;



    private static ResponseDto buildResponse(HttpStatus status, boolean success, String message, Object payload, Object errors) {
        return ResponseDto.builder()
                .httpStatus(status)
                .resultCode(status.value())
                .success(success)
                .message(message)
                .data(payload)
                .errors(errors)
                .build();
    }



    public static ResponseDto success(Object payload) {
        return buildResponse(HttpStatus.OK, true, "Request successful", payload, null);
    }



    public static ResponseDto success(Object payload, String message) {
        return buildResponse(HttpStatus.OK, true, message, payload, null);
    }



    public static ResponseDto created(Object payload) {
        return buildResponse(HttpStatus.CREATED, true, "Resource created successfully", payload, null);
    }



    public static ResponseDto error(HttpStatus status, String message) {
        return buildResponse(status, false, message, null, null);
    }



    public static ResponseDto error(HttpStatus status, String message, Object errors) {
        return buildResponse(status, false, message, null, errors);
    }
}
