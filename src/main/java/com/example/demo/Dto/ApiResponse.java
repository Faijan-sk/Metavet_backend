package com.example.demo.Dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private Integer statusCode;
    private String error;                     // general error / error code
    private String status;                    // custom status like PENDING, APPROVED
    private Map<String, String> errors;       // field-wise validation errors

    // ---------- CONSTRUCTORS ----------

    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(boolean success, String message, T data) {
        this();
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public ApiResponse(boolean success, String message, T data, Integer statusCode) {
        this(success, message, data);
        this.statusCode = statusCode;
    }

    public ApiResponse(boolean success, String message, T data, String error) {
        this(success, message, data);
        this.error = error;
    }

    public ApiResponse(boolean success, String message, T data, Integer statusCode, Map<String, String> errors) {
        this(success, message, data, statusCode);
        this.errors = errors;
    }

    // ---------- STATIC FACTORY METHODS ----------

    // Success with data
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Operation successful", data, 200);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, 200);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null, 200);
    }

    // Error without custom status
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, 400);
    }

    public static <T> ApiResponse<T> error(String message, Integer statusCode ) {
        return new ApiResponse<>(false, message, null, statusCode);
    }

    // ❗ FIXED: `status` ab alag key me jayega, error me nahi
    public static <T> ApiResponse<T> error(String message, Integer statusCode, String status) {
        ApiResponse<T> response = new ApiResponse<>(false, message, null, statusCode);
        response.setStatus(status);  // <-- yaha status set ho raha hai
        return response;
    }

    public static <T> ApiResponse<T> error(String message, String error) {
        return new ApiResponse<>(false, message, null, error);
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(false, message, null, 404);
    }

    public static <T> ApiResponse<T> unauthorized(String message) {
        return new ApiResponse<>(false, message, null, 401);
    }

    public static <T> ApiResponse<T> forbidden(String message) {
        return new ApiResponse<>(false, message, null, 403);
    }

    public static <T> ApiResponse<T> serverError(String message) {
        return new ApiResponse<>(false, message, null, 500);
    }

    // Validation errors
    public static <T> ApiResponse<T> validationError(String message, Map<String, String> fieldErrors) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(message);
        response.setStatusCode(400);
        response.setErrors(fieldErrors);
        return response;
    }

    public static <T> ApiResponse<T> validationError(String message, String field, String fieldError) {
        Map<String, String> errors = new HashMap<>();
        errors.put(field, fieldError);
        return validationError(message, errors);
    }

    // ---------- GETTERS & SETTERS ----------

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Integer getStatusCode() { return statusCode; }
    public void setStatusCode(Integer statusCode) { this.statusCode = statusCode; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Map<String, String> getErrors() { return errors; }
    public void setErrors(Map<String, String> errors) { this.errors = errors; }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                ", statusCode=" + statusCode +
                ", error='" + error + '\'' +
                ", status='" + status + '\'' +
                ", errors=" + errors +
                '}';
    }
}
