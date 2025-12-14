package com.logistics.parcelandcarrier.exception;

import com.logistics.parcelandcarrier.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for REST controllers
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handle ResourceNotFoundException
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFound(
    ResourceNotFoundException ex,
    WebRequest request
  ) {
    log.error("Resource not found: {}", ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.of(
      HttpStatus.NOT_FOUND.value(),
      ex.getMessage(),
      getPath(request)
    );

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  /**
   * Handle UnauthorizedException
   */
  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ErrorResponse> handleUnauthorized(
    UnauthorizedException ex,
    WebRequest request
  ) {
    log.error("Unauthorized access: {}", ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.of(
      HttpStatus.UNAUTHORIZED.value(),
      ex.getMessage(),
      getPath(request)
    );

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }

  /**
   * Handle SpecialtyIncompatibleException
   */
  @ExceptionHandler(SpecialtyIncompatibleException.class)
  public ResponseEntity<ErrorResponse> handleSpecialtyIncompatible(
    SpecialtyIncompatibleException ex,
    WebRequest request
  ) {
    log.error("Specialty incompatible: {}", ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.of(
      HttpStatus.BAD_REQUEST.value(),
      ex.getMessage(),
      getPath(request)
    );

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  /**
   * Handle TransporterUnavailableException
   */
  @ExceptionHandler(TransporterUnavailableException.class)
  public ResponseEntity<ErrorResponse> handleTransporterUnavailable(
    TransporterUnavailableException ex,
    WebRequest request
  ) {
    log.error("Transporter unavailable: {}", ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.of(
      HttpStatus.CONFLICT.value(),
      ex.getMessage(),
      getPath(request)
    );

    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  /**
   * Handle validation errors (Bean Validation)
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationErrors(
    MethodArgumentNotValidException ex,
    WebRequest request
  ) {
    log.error("Validation failed: {}", ex.getMessage());

    List<String> errors = ex.getBindingResult()
      .getFieldErrors()
      .stream()
      .map(error -> error.getField() + ": " + error.getDefaultMessage())
      .collect(Collectors.toList());

    ErrorResponse errorResponse = ErrorResponse.of(
      HttpStatus.BAD_REQUEST.value(),
      "Validation failed",
      getPath(request),
      errors
    );

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  /**
   * Handle IllegalArgumentException
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(
    IllegalArgumentException ex,
    WebRequest request
  ) {
    log.error("Illegal argument: {}", ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.of(
      HttpStatus.BAD_REQUEST.value(),
      ex.getMessage(),
      getPath(request)
    );

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  /**
   * Handle IllegalStateException
   */
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponse> handleIllegalState(
    IllegalStateException ex,
    WebRequest request
  ) {
    log.error("Illegal state: {}", ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.of(
      HttpStatus.CONFLICT.value(),
      ex.getMessage(),
      getPath(request)
    );

    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  /**
   * Handle AccessDeniedException (Spring Security)
   */
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDenied(
    AccessDeniedException ex,
    WebRequest request
  ) {
    log.error("Access denied: {}", ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.of(
      HttpStatus.FORBIDDEN.value(),
      "Access denied. You don't have permission to access this resource.",
      getPath(request)
    );

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
  }

  /**
   * Handle AuthenticationException (Spring Security)
   */
  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponse> handleAuthenticationException(
    AuthenticationException ex,
    WebRequest request
  ) {
    log.error("Authentication failed: {}", ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.of(
      HttpStatus.UNAUTHORIZED.value(),
      "Authentication failed. Please check your credentials.",
      getPath(request)
    );

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }

  /**
   * Handle BadCredentialsException
   */
  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleBadCredentials(
    BadCredentialsException ex,
    WebRequest request
  ) {
    log.error("Bad credentials: {}", ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.of(
      HttpStatus.UNAUTHORIZED.value(),
      "Invalid username or password",
      getPath(request)
    );

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }

  /**
   * Handle all other exceptions
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(
    Exception ex,
    WebRequest request
  ) {
    log.error("Unexpected error occurred", ex);

    ErrorResponse errorResponse = ErrorResponse.of(
      HttpStatus.INTERNAL_SERVER_ERROR.value(),
      "An unexpected error occurred. Please try again later.",
      getPath(request)
    );

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }

  /**
   * Extract path from WebRequest
   */
  private String getPath(WebRequest request) {
    return request.getDescription(false).replace("uri=", "");
  }
}
