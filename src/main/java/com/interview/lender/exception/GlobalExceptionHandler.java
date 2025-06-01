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
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<ResponseDto> handlePersistenceException(PersistenceException exception) {
        log.error("[Persistence Error] Could not complete data operation: {}", exception.getMessage(), exception);
        return buildResponse(HttpStatus.BAD_REQUEST, "Request processing was not completed. Please try again.", exception.getMessage());
    }



    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseDto> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        log.error("[Data Integrity Violation] Conflict while saving data: {}", exception.getMessage(), exception);
        return buildResponse(HttpStatus.CONFLICT, "Data conflict has occurred. Please check your input and try again.", exception.getMessage());
    }



    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseDto> handleConstraintViolationException(ConstraintViolationException exception) {
        log.error("[Validation Error] Constraint violation occurred: {}", exception.getMessage(), exception);
        return buildResponse(HttpStatus.CONFLICT, "Some required fields are missing or incorrect. Please check your input and try again.", exception.getMessage());
    }



    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ResponseDto> handleDuplicateKeyException(DuplicateKeyException exception) {
        log.error("[Duplicate Key] Entry already exists: {}", exception.getMessage(), exception);
        return buildResponse(HttpStatus.CONFLICT, "Duplicate entry detected. Please use a unique value and try again.", exception.getMessage());
    }



    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseDto> handleIllegalArgumentException(IllegalArgumentException exception) {
        log.error("[Illegal Argument] Invalid input provided: {}", exception.getMessage(), exception);
        return buildResponse(HttpStatus.BAD_REQUEST, "Incorrect input provided. Please check your details and try again.", exception.getMessage());
    }



    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<ResponseDto> handleNoResultException(NoResultException exception) {
        log.warn("[No Result] No data found for the given criteria: {}", exception.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, "No matching result(s) found", exception.getMessage());
    }



    @ExceptionHandler(TransactionRequiredException.class)
    public ResponseEntity<ResponseDto> handleTransactionRequiredException(TransactionRequiredException exception) {
        log.error("[Transaction Error] Operation requires a transaction: {}", exception.getMessage(), exception);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Request processing was not completed. Please try again.", exception.getMessage());
    }



    @ExceptionHandler(HttpServerErrorException.InternalServerError.class)
    public ResponseEntity<ResponseDto> handleGeneralException(Exception exception) {
        log.error("[Internal Server Error] Unexpected server error occurred: {}", exception.getMessage(), exception);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong on our end. Please try again.", exception.getMessage());
    }



    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ResponseDto> handleIllegalStateException(Exception exception) {
        log.error("[Illegal State] Application state error: {}", exception.getMessage(), exception);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Operation cannot proceed due to an unexpected issue. Please try again", exception.getMessage());
    }



    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseDto> handleBadRequestException(Exception exception) {
        log.warn("[Bad Request] Client submitted a bad request: {}", exception.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid request. Please check your input and try again.", exception.getMessage());
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.warn("[Validation Error] Invalid method arguments: {}", exception.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid input(s) format. Please correct and try again.", exception.getMessage());
    }



    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
    public ResponseEntity<ResponseDto> handleUnauthorizedException(HttpClientErrorException.Unauthorized exception) {
        log.warn("[Unauthorized] Access denied: {}", exception.getMessage(), exception);
        return buildResponse(HttpStatus.UNAUTHORIZED, "Unauthorized access. Please log in.", exception.getMessage());
    }



    @ExceptionHandler(HttpClientErrorException.Forbidden.class)
    public ResponseEntity<ResponseDto> handleForbiddenException(HttpClientErrorException.Forbidden exception) {
        log.warn("[Forbidden] Access forbidden: {}", exception.getMessage(), exception);
        return buildResponse(HttpStatus.FORBIDDEN, "You do not have permission to access this resource.", exception.getMessage());
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
