package com.learnrank.common.exception;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;



@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException  ex) {
        List<ErrorResponse.Detail> details = ex.getBindingResult().getFieldErrors().stream()
        		.map(fe -> new ErrorResponse.Detail(fe.getField(), fe.getDefaultMessage()))
                .toList();
        return ResponseEntity.badRequest().body(new ErrorResponse(
                "VALIDATION_ERROR", "One or more fields are invalid.",
                Instant.now().toString(), UUID.randomUUID().toString(), details));
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(
                "DUPLICATE_EMAIL", ex.getMessage(),
                Instant.now().toString(), UUID.randomUUID().toString(), List.of()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(
                "INVALID_CREDENTIALS", ex.getMessage(),
                Instant.now().toString(), UUID.randomUUID().toString(), List.of()));
    }


}
