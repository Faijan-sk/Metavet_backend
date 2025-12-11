package com.example.demo.Controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Dto.PetBehavioristKycRequestDto;
import com.example.demo.Entities.PetBehavioristKycEntity;
import com.example.demo.Service.PetBehavioristKycService;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;

@RestController
@RequestMapping(path = "/api/behaviorist-kyc")
public class PetBehavioristKycController {

    private static final Logger logger = LoggerFactory.getLogger(PetBehavioristKycController.class);

    @Autowired
    private PetBehavioristKycService behavioristKycService;

    // =====================================================
    // 01. CREATE KYC
    // POST /api/behaviorist-kyc
    // =====================================================
    @PostMapping
    public ResponseEntity<Map<String, Object>> createBehavioristKyc(
            @Valid @RequestBody PetBehavioristKycRequestDto dto) {

        logger.info("Creating Behaviorist KYC for petUid={}", dto.getPetUid());

        PetBehavioristKycEntity created = behavioristKycService.createBehavioristKyc(dto);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Behaviorist KYC created successfully.");
        response.put("timestamp", LocalDateTime.now());
        response.put("data", created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // =====================================================
    // 02. DELETE BY UID
    // DELETE /api/behaviorist-kyc/{uid}
    // =====================================================
    @DeleteMapping("/{uid}")
    public ResponseEntity<Map<String, Object>> deleteByUid(@PathVariable("uid") String uid) {

        logger.info("Deleting Behaviorist KYC for uid={}", uid);

        behavioristKycService.deleteByUid(uid);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Behaviorist KYC deleted successfully.");
        response.put("timestamp", LocalDateTime.now());
        response.put("uid", uid);

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // 03. GET ALL (UPDATED)
    // GET /api/behaviorist-kyc
    // =====================================================
    // =====================================================

@GetMapping
public ResponseEntity<?> getAllKycs() {
    try {
        logger.info("⚡ API Request: GET /api/behaviorist-kyc (Get All)");

        List<PetBehavioristKycEntity> kycs = behavioristKycService.getAllKycs();

        logger.info("✅ Success: Retrieved {} Behaviorist KYC records", kycs.size());

        // Build response data
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("totalRecords", kycs.size());
        responseData.put("records", kycs);

        // Build success response
        return ResponseEntity.ok(buildSuccessResponse(
                "KYC_RECORDS_RETRIEVED",
                "Behaviorist KYC records retrieved successfully.",
                responseData));

    } catch (Exception ex) {
        logger.error("💥 Error fetching all Behaviorist KYCs: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorResponse(
                        "INTERNAL_SERVER_ERROR",
                        "Failed to Retrieve Records",
                        "Unable to fetch KYC records. Please try again later.",
                        null,
                        ex.getMessage()));
    }
}

// ==================== Helper Methods ====================

private Map<String, Object> buildSuccessResponse(String code, String message, Map<String, Object> data) {
    Map<String, Object> response = new HashMap<>();
    response.put("success", true);
    response.put("code", code);
    response.put("message", message);
    response.put("data", data);
    response.put("timestamp", LocalDateTime.now());
    return response;
}

private Map<String, Object> buildErrorResponse(String errorCode, String error, String message, String field, String details) {
    Map<String, Object> response = new HashMap<>();
    response.put("success", false);
    response.put("errorCode", errorCode);
    response.put("error", error);
    response.put("message", message);

    if (field != null) {
        response.put("field", field);
    }

    if (details != null) {
        response.put("details", details);
    }

    response.put("timestamp", LocalDateTime.now());
    return response;
}

    // =====================================================
    // 04. GET BY UID
    // GET /api/behaviorist-kyc/{uid}
    // =====================================================
    @GetMapping("/{uid}")
    public ResponseEntity<Map<String, Object>> getByUid(@PathVariable("uid") String uid) {

        logger.info("Fetching Behaviorist KYC for uid={}", uid);

        PetBehavioristKycEntity entity = behavioristKycService.getByUid(uid);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("timestamp", LocalDateTime.now());
        response.put("data", entity);

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // 05. UPDATE STATUS BY UID
    // PATCH /api/behaviorist-kyc/{uid}/status
    // body: { "status": "APPROVED" }
    // =====================================================
    @PatchMapping("/{uid}/status")
    public ResponseEntity<Map<String, Object>> updateStatusByUid(
            @PathVariable("uid") String uid,
            @RequestBody Map<String, String> requestBody) {

        String status = requestBody.get("status");

        if (status == null || status.trim().isEmpty()) {
            throw new ValidationException("STATUS_REQUIRED: 'status' field is required in request body.");
        }

        logger.info("Updating Behaviorist KYC status for uid={} to status={}", uid, status);

        PetBehavioristKycEntity updated = behavioristKycService.updateStatusByUid(uid, status);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "KYC status updated successfully.");
        response.put("timestamp", LocalDateTime.now());
        response.put("data", updated);

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // EXCEPTION HANDLING (Validation + JSON + Generic)
    // =====================================================

    /**
     * Handle JSON type mismatch / unreadable body
     * e.g. behaviorFrequency expected String but got Array.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {

        logger.warn("HttpMessageNotReadableException: {}", ex.getMessage());

        String message = "Invalid request body. Please check the data types of your fields.";

        Throwable cause = ex.getCause();
        if (cause instanceof MismatchedInputException mie) {

            // Find which field/path had a type mismatch
            String path = mie.getPath().stream()
                    .map(ref -> {
                        String fieldName = ref.getFieldName();
                        if (fieldName != null) return fieldName;
                        if (ref.getIndex() >= 0) return "[" + ref.getIndex() + "]";
                        return "?";
                    })
                    .collect(Collectors.joining("."));

            if ("behaviorFrequency".equals(path)) {
                message = "Invalid type for field 'behaviorFrequency'. Only a string value is allowed, for example: "
                        + "\"Daily\", \"Weekly\", \"Occasionally\" or \"Only in specific situations\". "
                        + "Arrays like [\"Daily\"] or any non-string type are not allowed.";
            } else {
                message = "Invalid type for field '" + path + "'. Please send the correct data type.";
            }
        }

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("success", false);
        errorBody.put("errorCode", "INVALID_JSON_TYPE");
        errorBody.put("message", message);
        errorBody.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
    }

    /**
     * Handle jakarta.validation.ValidationException (service/business validation)
     * Example: invalid enum value, missing conditional field, pet not found etc.
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(ValidationException ex) {
        logger.warn("ValidationException: {}", ex.getMessage());

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("success", false);
        errorBody.put("errorCode", "VALIDATION_ERROR");
        errorBody.put("message", ex.getMessage());
        errorBody.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
    }

    /**
     * Handle Bean Validation errors from @Valid DTO
     * (MethodArgumentNotValidException)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        logger.warn("MethodArgumentNotValidException: {}", ex.getMessage());

        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (msg1, msg2) -> msg1 + "; " + msg2 // if duplicate keys
                ));

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("success", false);
        errorBody.put("errorCode", "DTO_VALIDATION_ERROR");
        errorBody.put("message", "Request body validation failed.");
        errorBody.put("timestamp", LocalDateTime.now());
        errorBody.put("fieldErrors", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
    }

    /**
     * Catch-all: any other unexpected exception.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {

        logger.error("Unexpected error in BehavioristKycController", ex);

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("success", false);
        errorBody.put("errorCode", "INTERNAL_SERVER_ERROR");
        errorBody.put("message", "An unexpected error occurred. Please try again later.");
        errorBody.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
    }
}