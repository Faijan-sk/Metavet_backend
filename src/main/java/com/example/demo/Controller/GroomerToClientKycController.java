package com.example.demo.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Config.SpringSecurityAuditorAware;
import com.example.demo.Dto.GroomerToClientKycRequestDto;
import com.example.demo.Entities.GroomerToClientKycEntity;
import com.example.demo.Entities.UsersEntity;
import com.example.demo.Entities.GroomerToClientKycEntity.KycStatus;
import com.example.demo.Service.GroomerToClientKycService;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;

@RestController
@RequestMapping(path = "/api/groomer-kyc")
public class GroomerToClientKycController {

    private static final Logger logger = LoggerFactory.getLogger(GroomerToClientKycController.class);

    @Autowired
    private GroomerToClientKycService groomerKycService;
    
    @Autowired
    private SpringSecurityAuditorAware auditorAware;
    
    

    // =========================================================
    // 01. Create new Groomer KYC
    // POST /api/groomer-kyc
    // =========================================================
    @PostMapping
    public ResponseEntity<?> createGroomerKyc(
            @Valid @RequestBody GroomerToClientKycRequestDto dto,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        try {
            logger.info("⚡ API Request: POST /api/groomer-kyc | PetUID: {}", dto.getPetUid());

            // Extract token
            String accessToken = extractAccessToken(authHeader);
            if (accessToken == null) {
                logger.warn("❌ Unauthorized: Missing Authorization header");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(buildErrorResponse(
                                "UNAUTHORIZED",
                                "Authorization Required",
                                "Authorization header is missing. Please provide 'Authorization: Bearer <your-token>' in the request headers.",
                                "Authorization",
                                "Missing or invalid Bearer token"));
            }

            GroomerToClientKycEntity createdKyc = groomerKycService.createGroomerKyc(dto, accessToken);

            logger.info("✅ Success: Groomer KYC created | UID: {} | Status: {}",
                    createdKyc.getUid(), createdKyc.getStatus());
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("kycUid", createdKyc.getUid());
            responseData.put("petUid", createdKyc.getPetUid());
            responseData.put("userUid", createdKyc.getUserUid());
            responseData.put("status", createdKyc.getStatus().toString());
            responseData.put("groomingFrequency", createdKyc.getGroomingFrequency());
            responseData.put("groomingLocation", createdKyc.getGroomingLocation());
            responseData.put("appointmentDate", createdKyc.getAppointmentDate());
            responseData.put("appointmentTime", createdKyc.getAppointmentTime());
            responseData.put("healthConditions", createdKyc.getHealthConditions());
            responseData.put("behaviorIssues", createdKyc.getBehaviorIssues());
            responseData.put("services", createdKyc.getServices());
            responseData.put("addOns", createdKyc.getAddOns());
            responseData.put("createdAt", createdKyc.getCreatedAt());
            responseData.put("fullRecord", createdKyc);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(buildSuccessResponse(
                            "KYC_CREATED_SUCCESSFULLY",
                            "Groomer KYC has been created successfully and is pending approval.",
                            responseData));

        } catch (ValidationException ve) {
            logger.warn("❌ Validation Error: {}", ve.getMessage());
            return handleValidationException(ve);

        } catch (Exception ex) {
            logger.error("💥 Unexpected Error: ", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse(
                            "INTERNAL_SERVER_ERROR",
                            "Unexpected Server Error",
                            "An unexpected error occurred while processing your request. Please try again later or contact support if the problem persists.",
                            null,
                            ex.getMessage()));
        }
    }

    // =========================================================
    // 02. Delete Groomer KYC by UID
    // DELETE /api/groomer-kyc/uid/{uid}
    // =========================================================
    @DeleteMapping("/uid/{uid}")
    public ResponseEntity<?> deleteGroomerKycByUid(@PathVariable String uid) {

        try {
            logger.info("⚡ API Request: DELETE /api/groomer-kyc/uid/{}", uid);

            groomerKycService.deleteGroomerKycByUid(uid);

            logger.info("✅ Success: Groomer KYC deleted | UID: {}", uid);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("deletedKycUid", uid);
            responseData.put("deletionTimestamp", java.time.LocalDateTime.now());

            return ResponseEntity.ok(buildSuccessResponse(
                    "KYC_DELETED_SUCCESSFULLY",
                    "Groomer KYC has been permanently deleted from the database.",
                    responseData));

        } catch (ValidationException ve) {
            logger.warn("❌ Validation Error: {}", ve.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(buildErrorResponse(
                            "KYC_NOT_FOUND",
                            "Deletion Failed",
                            ve.getMessage(),
                            "uid",
                            uid));

        } catch (Exception ex) {
            logger.error("💥 Error deleting Groomer KYC with UID: {}", uid, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse(
                            "INTERNAL_SERVER_ERROR",
                            "Failed to Delete Record",
                            "Unable to delete KYC record. Please try again later.",
                            "uid",
                            uid));
        }
    }

    // =========================================================
    // 03. Update KYC Status by UID
    // PATCH /api/groomer-kyc/status/{uid}
    // Body: { "status": "APPROVED" }
    // =========================================================
    @PatchMapping("/status/{uid}")
    public ResponseEntity<?> updateKycStatusByUid(
            @PathVariable String uid,
            @RequestBody Map<String, String> statusRequest) {

        try {
            logger.info("⚡ API Request: PATCH /api/groomer-kyc/status/{}", uid);

            String status = statusRequest.get("status");

            if (status == null || status.trim().isEmpty()) {
                logger.warn("❌ Bad Request: Status field is missing in request body");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(buildErrorResponse(
                                "STATUS_FIELD_REQUIRED",
                                "Missing Required Field",
                                "The 'status' field is required in the request body. Please provide one of: 'PENDING', 'APPROVED', 'REJECTED'",
                                "status",
                                "Field is missing or empty. Expected format: {\"status\": \"APPROVED\"}"));
            }

            GroomerToClientKycEntity updatedKyc = groomerKycService.updateKycStatusByUid(uid, status);

            logger.info("✅ Success: KYC status updated | UID: {} | New Status: {}", uid, updatedKyc.getStatus());

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("kycUid", uid);
            responseData.put("newStatus", updatedKyc.getStatus().toString());
            responseData.put("statusDescription", getStatusDescription(updatedKyc.getStatus()));
            responseData.put("updatedAt", updatedKyc.getUpdatedAt());
            responseData.put("fullRecord", updatedKyc);

            return ResponseEntity.ok(buildSuccessResponse(
                    "KYC_STATUS_UPDATED",
                    "KYC status has been updated successfully.",
                    responseData));

        } catch (ValidationException ve) {
            logger.warn("❌ Validation Error: {}", ve.getMessage());
            return handleValidationException(ve);

        } catch (Exception ex) {
            logger.error("💥 Error updating KYC status by UID: {}", uid, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse(
                            "INTERNAL_SERVER_ERROR",
                            "Failed to Update Status",
                            "Unable to update KYC status. Please try again later.",
                            "uid",
                            uid));
        }
    }

    // =========================================================
    // 04. Get Groomer KYC by UID
    // GET /api/groomer-kyc/uid/{uid}
    // =========================================================
    @GetMapping("/uid/{uid}")
    public ResponseEntity<?> getGroomerKycByUid(@PathVariable String uid) {

        try {
            logger.info("⚡ API Request: GET /api/groomer-kyc/uid/{}", uid);

            Optional<GroomerToClientKycEntity> kyc = groomerKycService.getGroomerKycByUid(uid);

            if (kyc.isPresent()) {
                logger.info("✅ Success: KYC record found | UID: {}", uid);

                GroomerToClientKycEntity entity = kyc.get();

                Map<String, Object> responseData = new HashMap<>();
                responseData.put("kycUid", entity.getUid());
                responseData.put("petUid", entity.getPetUid());
                responseData.put("userUid", entity.getUserUid());
                responseData.put("status", entity.getStatus().toString());
                responseData.put("statusDescription", getStatusDescription(entity.getStatus()));
                responseData.put("groomingFrequency", entity.getGroomingFrequency());
                responseData.put("groomingLocation", entity.getGroomingLocation());
                responseData.put("appointmentDate", entity.getAppointmentDate());
                responseData.put("appointmentTime", entity.getAppointmentTime());
                responseData.put("healthConditions", entity.getHealthConditions());
                responseData.put("behaviorIssues", entity.getBehaviorIssues());
                responseData.put("services", entity.getServices());
                responseData.put("addOns", entity.getAddOns());
                responseData.put("createdAt", entity.getCreatedAt());
                responseData.put("updatedAt", entity.getUpdatedAt());
                responseData.put("fullRecord", entity);

                return ResponseEntity.ok(buildSuccessResponse(
                        "KYC_RECORD_FOUND",
                        "KYC record retrieved successfully.",
                        responseData));
            } else {
                logger.warn("❌ Not Found: KYC with UID '{}' does not exist", uid);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(buildErrorResponse(
                                "KYC_NOT_FOUND",
                                "Record Not Found",
                                "Groomer KYC with the specified UID does not exist in the database.",
                                "uid",
                                uid));
            }

        } catch (ValidationException ve) {
            logger.warn("❌ Validation Error: {}", ve.getMessage());
            return handleValidationException(ve);

        } catch (Exception ex) {
            logger.error("💥 Error fetching Groomer KYC by UID: {}", uid, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse(
                            "INTERNAL_SERVER_ERROR",
                            "Failed to Retrieve Record",
                            "Unable to fetch KYC record. Please try again later.",
                            "uid",
                            uid));
        }
    }

    // =========================================================
    // 05. Get All Groomer KYCs
    // GET /api/groomer-kyc
    // =========================================================
    @GetMapping
    public ResponseEntity<?> getAllGroomerKycs() {

        try {
            logger.info("⚡ API Request: GET /api/groomer-kyc (Get All)");

            List<GroomerToClientKycEntity> kycs = groomerKycService.getAllGroomerKycs();

            logger.info("✅ Success: Retrieved {} KYC records", kycs.size());

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("totalRecords", kycs.size());
            responseData.put("records", kycs);

            return ResponseEntity.ok(buildSuccessResponse(
                    "KYC_RECORDS_RETRIEVED",
                    "KYC records retrieved successfully.",
                    responseData));

        } catch (Exception ex) {
            logger.error("💥 Error fetching all Groomer KYCs: ", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse(
                            "INTERNAL_SERVER_ERROR",
                            "Failed to Retrieve Records",
                            "Unable to fetch KYC records. Please try again later.",
                            null,
                            ex.getMessage()));
        }
    }

    // =========================================================
    // 06. Get kyc Status
    // GET /api/groomer-kyc
    // =========================================================
    
    @GetMapping("/get-status")
public ResponseEntity<?> getOwnKycStatus() {
    try {
        Optional<UsersEntity> currentUserOpt = auditorAware.getCurrentAuditor();
        
        if (!currentUserOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(buildErrorResponse(
                            "UNAUTHORIZED",
                            "Unauthorized Access",
                            "User is not authenticated.",
                            null,
                            null));
        }
        
        UsersEntity loggedInUser = currentUserOpt.get();
        String userUid = loggedInUser.getUid().toString();
        
        // ✅ FIXED: Use userUid to find KYC instead of treating userUid as kycUid
        Optional<GroomerToClientKycEntity> kycOpt =
                groomerKycService.getGroomerKycByUserUid(userUid);
        
        if (!kycOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(buildErrorResponse(
                            "KYC_NOT_FOUND",
                            "KYC Not Found",
                            "No KYC record exists for the logged-in user.",
                            "userUid",
                            userUid));
        }
        
        GroomerToClientKycEntity kyc = kycOpt.get();
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("kycUid", kyc.getUid());
        responseData.put("status", kyc.getStatus());
        responseData.put("statusDescription", getStatusDescription(kyc.getStatus()));
        responseData.put("updatedAt", kyc.getUpdatedAt());
        
        return ResponseEntity.ok(buildSuccessResponse(
                "KYC_STATUS_FETCHED",
                "KYC status retrieved successfully.",
                responseData));
        
    } catch (Exception ex) {
        logger.error("💥 Error fetching KYC status: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorResponse(
                        "INTERNAL_SERVER_ERROR",
                        "Failed to Fetch Status",
                        "Unable to fetch KYC status. Please try again later.",
                        null,
                        ex.getMessage()));
    }
}

    
    
    
    // ==================== Helper Methods ====================

    private String extractAccessToken(String authHeader) {
        if (authHeader == null || authHeader.trim().isEmpty()) {
            return null;
        }
        if (authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private Map<String, Object> buildSuccessResponse(String code, String message, Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("code", code);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", java.time.LocalDateTime.now());
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

        response.put("timestamp", java.time.LocalDateTime.now());
        return response;
    }

    private ResponseEntity<?> handleValidationException(ValidationException ve) {
        String errorMessage = ve.getMessage();
        String errorCode = "VALIDATION_ERROR";
        String field = null;
        String details = errorMessage;

        // Parse error code from message if format: "ERROR_CODE: message"
        if (errorMessage != null && errorMessage.contains(":")) {
            String[] parts = errorMessage.split(":", 2);
            errorCode = parts[0].trim();
            details = parts.length > 1 ? parts[1].trim() : errorMessage;
        }

        HttpStatus status = determineHttpStatus(errorCode);

        return ResponseEntity.status(status)
                .body(buildErrorResponse(errorCode, getErrorTitle(errorCode), details, field, null));
    }

    private HttpStatus determineHttpStatus(String errorCode) {
        if (errorCode.contains("NOT_FOUND")) {
            return HttpStatus.NOT_FOUND;
        } else if (errorCode.contains("UNAUTHORIZED")) {
            return HttpStatus.UNAUTHORIZED;
        } else if (errorCode.contains("INVALID") || errorCode.contains("REQUIRED") || errorCode.contains("VALIDATION")) {
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

    private String getStatusDescription(KycStatus status) {
        switch (status) {
            case PENDING:
                return "KYC is under review and pending approval from the grooming service.";
            case APPROVED:
                return "KYC has been approved. Grooming services can now be scheduled.";
            case REJECTED:
                return "KYC has been rejected. Please contact support for more information.";	
            default:
                return "Unknown status";
        }
    }
}