package com.example.demo.Exceptions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ==================== Handle @Valid DTO Validation Errors ====================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        logger.warn("❌ DTO Validation Failed: {} field errors detected", ex.getBindingResult().getFieldErrorCount());

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("errorCode", "VALIDATION_FAILED");
        response.put("error", "Input Validation Failed");
        response.put("message", "One or more fields contain invalid data. Please review and correct the errors below.");

        List<Map<String, String>> fieldErrors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            Map<String, String> fieldError = new HashMap<>();
            fieldError.put("field", error.getField());
            fieldError.put("rejectedValue", error.getRejectedValue() != null ? error.getRejectedValue().toString() : "null");
            fieldError.put("errorMessage", error.getDefaultMessage());
            fieldError.put("errorType", "Field validation error");
            fieldErrors.add(fieldError);
        }

        response.put("validationErrors", fieldErrors);
        response.put("totalErrors", fieldErrors.size());
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ==================== Handle Enum Deserialization Errors ====================
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            WebRequest request) {

        logger.warn("❌ JSON Parsing Error: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("errorCode", "INVALID_REQUEST_FORMAT");
        response.put("error", "Invalid Request Data");
        response.put("timestamp", LocalDateTime.now());

        // Check if it's an InvalidFormatException (enum parsing error)
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) cause;
            
            String fieldName = "unknown";
            if (!ife.getPath().isEmpty()) {
                JsonMappingException.Reference reference = ife.getPath().get(ife.getPath().size() - 1);
                fieldName = reference.getFieldName();
            }

            String rejectedValue = ife.getValue() != null ? ife.getValue().toString() : "null";
            Class<?> targetType = ife.getTargetType();

            // Check if it's an enum type
            if (targetType.isEnum()) {
                Object[] enumConstants = targetType.getEnumConstants();
                List<String> validValues = new ArrayList<>();
                for (Object constant : enumConstants) {
                    validValues.add(constant.toString());
                }

                response.put("message", String.format(
                    "Invalid value for field '%s'. The value '%s' is not a valid enum constant.",
                    fieldName, rejectedValue
                ));

                Map<String, Object> fieldError = new HashMap<>();
                fieldError.put("field", fieldName);
                fieldError.put("rejectedValue", rejectedValue);
                fieldError.put("errorMessage", String.format(
                    "Invalid enum value. Must be one of: %s", validValues
                ));
                fieldError.put("validValues", validValues);
                fieldError.put("errorType", "Enum validation error");

                response.put("validationErrors", List.of(fieldError));
                response.put("totalErrors", 1);

            } else {
                // Other type conversion errors
                response.put("message", String.format(
                    "Cannot convert value '%s' to required type for field '%s'",
                    rejectedValue, fieldName
                ));

                Map<String, Object> fieldError = new HashMap<>();
                fieldError.put("field", fieldName);
                fieldError.put("rejectedValue", rejectedValue);
                fieldError.put("errorMessage", "Type conversion failed");
                fieldError.put("expectedType", targetType.getSimpleName());
                fieldError.put("errorType", "Type conversion error");

                response.put("validationErrors", List.of(fieldError));
                response.put("totalErrors", 1);
            }

        } else {
            // Generic JSON parsing error
            response.put("message", "The request body contains invalid JSON or malformed data. Please check your request format.");
            response.put("details", "Unable to parse JSON. Check for missing commas, quotes, or invalid JSON structure.");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ==================== Handle Constraint Violation (Path Variable/Param Validation) ====================
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(
            ConstraintViolationException ex,
            WebRequest request) {

        logger.warn("❌ Constraint Violation: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("errorCode", "VALIDATION_FAILED");
        response.put("error", "Validation Constraint Violated");
        response.put("message", "One or more constraints were violated in your request.");

        List<Map<String, String>> violations = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            Map<String, String> violationDetail = new HashMap<>();
            violationDetail.put("field", violation.getPropertyPath().toString());
            violationDetail.put("rejectedValue", violation.getInvalidValue() != null ? 
                violation.getInvalidValue().toString() : "null");
            violationDetail.put("errorMessage", violation.getMessage());
            violationDetail.put("errorType", "Constraint violation");
            violations.add(violationDetail);
        }

        response.put("validationErrors", violations);
        response.put("totalErrors", violations.size());
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ==================== Handle Custom ValidationException from Service Layer ====================
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidationException(
            ValidationException ex,
            WebRequest request) {

        logger.warn("❌ Service Validation Error: {}", ex.getMessage());

        String errorMessage = ex.getMessage();
        String errorCode = "VALIDATION_ERROR";
        String details = errorMessage;

        // Parse error code from message if format: "ERROR_CODE: message"
        if (errorMessage != null && errorMessage.contains(":")) {
            String[] parts = errorMessage.split(":", 2);
            errorCode = parts[0].trim();
            details = parts.length > 1 ? parts[1].trim() : errorMessage;
        }

        HttpStatus status = determineHttpStatus(errorCode);

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("errorCode", errorCode);
        response.put("error", getErrorTitle(errorCode));
        response.put("message", details);
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(status).body(response);
    }

    // ==================== Handle Generic Exceptions ====================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(
            Exception ex,
            WebRequest request) {

        logger.error("💥 Unexpected Error: ", ex);

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("errorCode", "INTERNAL_SERVER_ERROR");
        response.put("error", "Unexpected Server Error");
        response.put("message", "An unexpected error occurred while processing your request. Please try again later or contact support if the problem persists.");
        response.put("timestamp", LocalDateTime.now());

        // In development, you might want to add stack trace
        // response.put("details", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // ==================== Helper Methods ====================

    private HttpStatus determineHttpStatus(String errorCode) {
        if (errorCode.contains("NOT_FOUND")) {
            return HttpStatus.NOT_FOUND;
        } else if (errorCode.contains("UNAUTHORIZED")) {
            return HttpStatus.UNAUTHORIZED;
        } else if (errorCode.contains("INVALID") || errorCode.contains("REQUIRED") || 
                   errorCode.contains("VALIDATION")) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.BAD_REQUEST;
    }

    private String getErrorTitle(String errorCode) {
        if (errorCode.contains("NOT_FOUND"))
            return "Resource Not Found";
        if (errorCode.contains("UNAUTHORIZED"))
            return "Unauthorized Access";
        if (errorCode.contains("INVALID"))
            return "Invalid Input";
        if (errorCode.contains("REQUIRED"))
            return "Required Field Missing";
        if (errorCode.contains("VALIDATION"))
            return "Validation Error";
        return "Error";
    }
}