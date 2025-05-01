package com.interview.lender.exception;

import com.interview.lender.dto.ResponseDto;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TransactionRequiredException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseDto> handleBusinessException(BusinessException ex) {
        log.error("Business rule violation: {}", ex.getMessage());

        ResponseDto response = ResponseDto.error(HttpStatus.BAD_REQUEST, ex.getMessage());

        return new ResponseEntity<>(response, response.getHttpStatus());
    }



    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<ResponseDto> handlePersistenceException(PersistenceException exception) {
        log.error("PersistenceException occurred: ", exception);
        return buildResponse(HttpStatus.BAD_REQUEST, "Request processing was not completed. Please try again.", exception.getMessage());
    }



    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseDto> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        log.error("DataIntegrityViolationException occurred: ", exception);
        return buildResponse(HttpStatus.CONFLICT, "Data conflict has occurred. Please check your input and try again.", exception.getMessage());
    }



    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseDto> handleConstraintViolationException(ConstraintViolationException exception) {
        log.error("ConstraintViolationException occurred: ", exception);
        return buildResponse(HttpStatus.CONFLICT, "Some required fields are missing or incorrect. Please check your input and try again.", exception.getMessage());
    }



    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ResponseDto> handleDuplicateKeyException(DuplicateKeyException exception) {
        log.error("DuplicateKeyException occurred: ", exception);
        return buildResponse(HttpStatus.CONFLICT, "Duplicate entry detected. Please use a unique value and try again.", exception.getMessage());
    }



    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseDto> handleIllegalArgumentException(IllegalArgumentException exception) {
        log.error("IllegalArgumentException occurred: ", exception);
        return buildResponse(HttpStatus.BAD_REQUEST, "Incorrect input provided. Please check your details and try again.", exception.getMessage());
    }



    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<ResponseDto> handleNoResultException(NoResultException exception) {
        log.error("NoResultException occurred: ", exception);
        return buildResponse(HttpStatus.NOT_FOUND, "No matching result(s) found", exception.getMessage());
    }



    @ExceptionHandler(TransactionRequiredException.class)
    public ResponseEntity<ResponseDto> handleTransactionRequiredException(TransactionRequiredException exception) {
        log.error("TransactionRequiredException occurred: ", exception);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Request processing was not completed. Please try again.", exception.getMessage());
    }



    @ExceptionHandler(HttpServerErrorException.InternalServerError.class)
    public ResponseEntity<ResponseDto> handleGeneralException(Exception exception) {
        log.error("InternalServerError occurred: ", exception);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong on our end. Please try again.", exception.getMessage());
    }



    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ResponseDto> handleIllegalStateException(Exception exception) {
        log.error("IllegalStateException occurred: ", exception);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Operation cannot proceed due to an unexpected issue. Please try again", exception.getMessage());
    }



    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseDto> handleBadRequestException(Exception exception) {
        log.error("BadRequestException occurred: ", exception);
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid request. Please check your input and try again.", exception.getMessage());
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto> handleMethodArgumentNotValidException(Exception exception) {
        log.error("MethodArgumentNotValidException occurred: ", exception);
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid input(s) format. Please correct and try again..", exception.getMessage());
    }



    private ResponseEntity<ResponseDto> buildResponse(HttpStatus status, String message, Object payload) {
        ResponseDto responseDto = ResponseDto.builder()
                .httpStatus(status)
                .resultCode(status.value())
                .success(false)
                .message(message)
                .errors(payload)
                .build();
        return new ResponseEntity<>(responseDto, status);
    }
}
