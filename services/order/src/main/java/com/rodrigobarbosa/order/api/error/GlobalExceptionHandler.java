package com.rodrigobarbosa.order.api.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.OffsetDateTime;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 400 - Bean Validation on @RequestBody DTOs
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
        MethodArgumentNotValidException ex,
        HttpServletRequest request
    ) {
        List<ApiError.ApiErrorDetail> details =
            ex.getBindingResult().getFieldErrors().stream()
                .map(this::toDetail)
                .toList();

        return build(HttpStatus.BAD_REQUEST, "Validation failed", request, details);
    }

    // 400 - Constraint violations (e.g., @RequestParam / @PathVariable validations)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(
        ConstraintViolationException ex,
        HttpServletRequest request
    ) {
        var details =
            ex.getConstraintViolations().stream()
                .map(v -> new ApiError.ApiErrorDetail(v.getPropertyPath().toString(), v.getMessage()))
                .toList();

        return build(HttpStatus.BAD_REQUEST, "Validation failed", request, details);
    }

    // 404 - custom not found
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    // 409 - conflicts / illegal state
    @ExceptionHandler({ConflictException.class, IllegalStateException.class})
    public ResponseEntity<ApiError> handleConflict(RuntimeException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    // Spring exceptions that already carry an HTTP status (optional, but useful)
    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<ApiError> handleErrorResponse(ErrorResponseException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String msg = extractMessage(ex);
        return build(status, msg, request, null);
    }

    // 500 - unexpected
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", request, null);
    }

    private ResponseEntity<ApiError> build(
        HttpStatus status,
        String message,
        HttpServletRequest request,
        List<ApiError.ApiErrorDetail> details
    ) {
        ApiError body =
            new ApiError(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                details
            );

        return ResponseEntity.status(status).body(body);
    }

    private ApiError.ApiErrorDetail toDetail(FieldError fe) {
        return new ApiError.ApiErrorDetail(fe.getField(), fe.getDefaultMessage());
    }

    private String extractMessage(ErrorResponseException ex) {
        ProblemDetail pd = ex.getBody();
        if (pd != null && pd.getDetail() != null && !pd.getDetail().isBlank()) return pd.getDetail();
        return ex.getMessage();
    }
}
