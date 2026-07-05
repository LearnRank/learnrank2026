package com.learnrank.common.exception;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;



@RestControllerAdvice
public class GlobalExceptionHandler {

	    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	    // ---- 400: Bean Validation failures ----
	    @ExceptionHandler(MethodArgumentNotValidException.class)
	    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
	        List<ErrorResponse.Detail> details = ex.getBindingResult().getFieldErrors().stream()
	                .map(fe -> new ErrorResponse.Detail(fe.getField(), fe.getDefaultMessage()))
	                .toList();
	        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
	                "One or more fields are invalid.", details);
	    }

	    // ---- 400: manual business-rule validation ----
	    @ExceptionHandler(ValidationException.class)
	    public ResponseEntity<ErrorResponse> handleBusinessValidation(ValidationException ex) {
	        var detail = new ErrorResponse.Detail(ex.getField(), ex.getMessage());
	        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", ex.getMessage(), List.of(detail));
	    }

	    // ---- 401: bad credentials / expired or revoked tokens ----
	    @ExceptionHandler(InvalidCredentialsException.class)
	    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
	        return build(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", ex.getMessage(), List.of());
	    }

	    @ExceptionHandler(TokenExpiredException.class)
	    public ResponseEntity<ErrorResponse> handleTokenExpired(TokenExpiredException ex) {
	        return build(HttpStatus.UNAUTHORIZED, "TOKEN_INVALID", ex.getMessage(), List.of());
	    }

	    // ---- 403: role / ownership checks (Spring Security) ----
	    @ExceptionHandler(AccessDeniedException.class)
	    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
	        return build(HttpStatus.FORBIDDEN, "ACCESS_DENIED",
	                "You do not have permission to perform this action.", List.of());
	    }

	    // ---- 404: entity lookups ----
	    @ExceptionHandler(ResourceNotFoundException.class)
	    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
	        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), List.of());
	    }

	    // ---- 409: conflicts (duplicate email, illegal state transitions) ----
	    @ExceptionHandler(DuplicateEmailException.class)
	    public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException ex) {
	        return build(HttpStatus.CONFLICT, "DUPLICATE_EMAIL", ex.getMessage(), List.of());
	    }

	    @ExceptionHandler(IllegalStateActionException.class)
	    public ResponseEntity<ErrorResponse> handleIllegalStateAction(IllegalStateActionException ex) {
	        return build(HttpStatus.CONFLICT, "ILLEGAL_STATE_ACTION", ex.getMessage(), List.of());
	    }

	    // ---- 500: fallback — never leak internals to the client ----
	    @ExceptionHandler(Exception.class)
	    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
	        String traceId = UUID.randomUUID().toString();
	        log.error("Unhandled exception [traceId={}]", traceId, ex);   // full stack trace goes to logs only
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(
	                "INTERNAL_ERROR", "Something went wrong. Please try again.",
	                Instant.now().toString(), traceId, List.of()));
	    }

	    private ResponseEntity<ErrorResponse> build(HttpStatus status, String code, String message,
	                                                 List<ErrorResponse.Detail> details) {
	        return ResponseEntity.status(status).body(new ErrorResponse(
	                code, message, Instant.now().toString(), UUID.randomUUID().toString(), details));
	    }
	}


