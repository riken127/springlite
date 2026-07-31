package com.example.service.http.error;

import com.example.service.application.command.WidgetConflictException;
import com.example.service.application.query.WidgetNotFoundException;
import com.example.service.observability.CorrelationIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Central HTTP error mapping with no stack traces in client responses. */
@RestControllerAdvice
public final class GlobalExceptionHandler {
  private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ApiError> validation(
      MethodArgumentNotValidException exception, HttpServletRequest request) {
    String message =
        exception.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
    return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message, request);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  ResponseEntity<ApiError> badRequest(
      IllegalArgumentException exception, HttpServletRequest request) {
    return error(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", exception.getMessage(), request);
  }

  @ExceptionHandler(WidgetNotFoundException.class)
  ResponseEntity<ApiError> notFound(WidgetNotFoundException exception, HttpServletRequest request) {
    return error(HttpStatus.NOT_FOUND, "WIDGET_NOT_FOUND", exception.getMessage(), request);
  }

  @ExceptionHandler(WidgetConflictException.class)
  ResponseEntity<ApiError> conflict(WidgetConflictException exception, HttpServletRequest request) {
    return error(HttpStatus.CONFLICT, "WIDGET_CONFLICT", exception.getMessage(), request);
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<ApiError> unexpected(Exception exception, HttpServletRequest request) {
    LOG.error("Unhandled request failure", exception);
    return error(
        HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Unexpected server error", request);
  }

  private ResponseEntity<ApiError> error(
      HttpStatus status, String code, String message, HttpServletRequest request) {
    String correlationId =
        (String) request.getAttribute(CorrelationIdFilter.CORRELATION_ID_ATTRIBUTE);
    return ResponseEntity.status(status).body(new ApiError(code, message, correlationId));
  }
}
