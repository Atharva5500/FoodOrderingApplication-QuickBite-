package com.quickbite.exception;

import com.quickbite.dto.response.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

// @RestControllerAdvice intercepts ALL exceptions thrown
// anywhere in the application — controllers, services, repositories
// One class handles everything — no try/catch needed in controllers
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 1. Our Custom Exceptions ─────────────────────────────────

    // Handles: throw new ResourceNotFoundException("Restaurant not found")
    // Returns: 404 Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(
            ResourceNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    // Handles: throw new BadRequestException("Cart is empty")
    // Returns: 400 Bad Request
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(
            BadRequestException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    // Handles: throw new UnauthorizedException("You don't own this restaurant")
    // Returns: 403 Forbidden
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(
            UnauthorizedException ex) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ex.getMessage()));
    }

    // ── 2. Validation Exception ──────────────────────────────────

    // Handles: @Valid fails on request DTO
    // Example: empty email, password too short, null role
    // Returns: 400 with a map of field → error message
    // Example response:
    // {
    //   "success": false,
    //   "message": "Validation failed",
    //   "data": {
    //     "email": "Enter a valid email",
    //     "password": "Password must be at least 6 characters"
    //   }
    // }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        // Collect all field errors into a map
        // key = field name, value = error message
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getAllErrors()
                .forEach(error -> {
                    String fieldName    = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Map<String, String>>builder()
                        .success(false)
                        .message("Validation failed")
                        .data(errors)
                        .build());
    }

    // ── 3. Spring Security Exceptions ────────────────────────────

    // Handles: JWT token missing, expired, or invalid
    // Returns: 401 Unauthorized
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(
            AuthenticationException ex) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Authentication failed: " + ex.getMessage()));
    }

    // Handles: Valid JWT but wrong role
    // Example: Customer trying to access /api/admin/**
    // Returns: 403 Forbidden
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(
            AccessDeniedException ex) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("Access denied: you don't have permission"));
    }

    // ── 4. Request Format Exceptions ─────────────────────────────

    // Handles: invalid JSON in request body
    // Example: missing closing bracket, wrong data type
    // Returns: 400 Bad Request
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidJson(
            HttpMessageNotReadableException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Invalid request format. Please check your JSON."));
    }

    // Handles: wrong type in @PathVariable or @RequestParam
    // Example: /api/restaurants/abc where id should be Long
    // Returns: 400 Bad Request
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {

        String message = String.format(
                "Invalid value '%s' for parameter '%s'. Expected type: %s",
                ex.getValue(),
                ex.getName(),
                ex.getRequiredType() != null ?
                        ex.getRequiredType().getSimpleName() : "unknown"
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message));
    }

    // Handles: required @RequestParam is missing from URL
    // Example: GET /api/restaurants/search without ?keyword=
    // Returns: 400 Bad Request
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParam(
            MissingServletRequestParameterException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        "Required parameter '" + ex.getParameterName() + "' is missing"));
    }

    // ── 5. Database Exception ────────────────────────────────────

    // Handles: DB constraint violations
    // Example: duplicate email (unique constraint on users.email)
    // Returns: 409 Conflict
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(
            DataIntegrityViolationException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(
                        "Data conflict: a record with this value already exists"));
    }

    // ── 6. Catch-All Exception ───────────────────────────────────

    // Handles: any unexpected exception not caught above
    // Returns: 500 Internal Server Error
    // IMPORTANT: Never expose the real error message to client in production
    // It could reveal internal system details to attackers
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex) {

        // Log the actual error internally (we'll add proper logging later)
        System.err.println("Unexpected error: " + ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Something went wrong. Please try again later."));
    }
}