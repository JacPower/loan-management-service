package com.interview.lender.util;

import com.interview.lender.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class Util {


    private Util() {
        throw new IllegalStateException("Utility class instantiation not allowed.");
    }



    public static ResponseDto buildSuccessResponse(String message, Object payload, HttpStatus httpStatus) {
        return ResponseDto.builder()
                .httpStatus(httpStatus)
                .resultCode(httpStatus.value())
                .success(true)
                .message(message)
                .data(payload)
                .build();
    }



    public static ResponseDto buildErrorResponse(String message, HttpStatus status) {
        return ResponseDto.builder()
                .httpStatus(status)
                .resultCode(status.value())
                .message(message)
                .build();
    }
}
