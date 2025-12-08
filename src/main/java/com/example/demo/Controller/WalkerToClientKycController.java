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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Dto.WalkerToClientKycRequestDto;
import com.example.demo.Entities.WalkerToClientKycEntity;
import com.example.demo.Entities.WalkerToClientKycEntity.KycStatus;
import com.example.demo.Service.WalkerToClientKycService;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;

@RestController
@RequestMapping(path = "/api/walker-kyc")
public class WalkerToClientKycController {

    private static final Logger logger = LoggerFactory.getLogger(WalkerToClientKycController.class);

    @Autowired
    private WalkerToClientKycService walkerKycService;

    // =========================================================
    // 01. Create new Walker KYC
    // POST /api/walker-kyc
    // =========================================================
    @PostMapping
    public ResponseEntity<?> createWalkerKyc(
            @Valid @RequestBody WalkerToClientKycRequestDto dto,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        try {
            logger.info("⚡ API Request: POST /api/walker-kyc | PetUID: {}", dto.getPetUid());

            // Extract token (optional for Walker KYC, but following pattern)
            String accessToken = extractAccessToken(authHeader);

            WalkerToClientKycEntity createdKyc = walkerKycService.createWalkerKyc(dto);

            logger.info("✅ Success: Walker KYC created | UID: {} | Status: {}",
                    createdKyc.getUid(), createdKyc.getStatus());

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("kycUid", createdKyc.getUid());
            responseData.put("petUid", createdKyc.getPetUid());
            responseData.put("status", createdKyc.getStatus().toString());
            responseData.put("petNames", createdKyc.getPetNames());
            responseData.put("energyLevel", createdKyc.getEnergyLevel());
            responseData.put("walkingExperience", createdKyc.getWalkingExperience());
            responseData.put("preferredWalkType", createdKyc.getPreferredWalkType());
            responseData.put("preferredStartDate", createdKyc.getPreferredStartDate());
            responseData.put("consent", createdKyc.getConsent());
            responseData.put("signature", createdKyc.getSignature());
            responseData.put("signatureDate", createdKyc.getSignatureDate());
            responseData.put("createdAt", createdKyc.getCreatedAt());
            responseData.put("fullRecord", createdKyc);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(buildSuccessResponse(
                            "KYC_CREATED_SUCCESSFULLY",
                            "Walker KYC has been created successfully and is pending approval.",
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
    // 02. Update existing Walker KYC
    // PUT /api/walker-kyc/{id}
    // =========================================================
    @PutMapping("/{id}")
    public ResponseEntity<?> updateWalkerKyc(
            @PathVariable Long id,
            @Valid @RequestBody WalkerToClientKycRequestDto dto) {

        try {
            logger.info("⚡ API Request: PUT /api/walker-kyc/{}", id);

            WalkerToClientKycEntity updatedKyc = walkerKycService.updateWalkerKyc(id, dto);

            logger.info("✅ Success: Walker KYC updated | ID: {} | UID: {}", id, updatedKyc.getUid());

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("kycId", id);
            responseData.put("kycUid", updatedKyc.getUid());
            responseData.put("petUid", updatedKyc.getPetUid());
            responseData.put("status", updatedKyc.getStatus().toString());
            responseData.put("updatedAt", updatedKyc.getUpdatedAt());
            responseData.put("fullRecord", updatedKyc);

            return ResponseEntity.ok(buildSuccessResponse(
                    "KYC_UPDATED_SUCCESSFULLY",
                    "Walker KYC has been updated successfully.",
                    responseData));

        } catch (ValidationException ve) {
            logger.warn("❌ Validation Error: {}", ve.getMessage());
            return handleValidationException(ve);

        } catch (Exception ex) {
            logger.error("💥 Error updating Walker KYC with ID: {}", id, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse(
                            "INTERNAL_SERVER_ERROR",
                            "Failed to Update Record",
                            "Unable to update KYC record. Please try again later.",
                            "id",
                            id.toString()));
        }
    }

    // =========================================================
    // 03. Get All Walker KYCs
    // GET /api/walker-kyc
    // =========================================================
    @GetMapping
    public ResponseEntity<?> getAllWalkerKycs() {

        try {
            logger.info("⚡ API Request: GET /api/walker-kyc (Get All)");

            List<WalkerToClientKycEntity> kycs = walkerKycService.getAllWalkerKycs();

            logger.info("✅ Success: Retrieved {} KYC records", kycs.size());

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("totalRecords", kycs.size());
            responseData.put("records", kycs);

            return ResponseEntity.ok(buildSuccessResponse(
                    "KYC_RECORDS_RETRIEVED",
                    "Walker KYC records retrieved successfully.",
                    responseData));

        } catch (Exception ex) {
            logger.error("💥 Error fetching all Walker KYCs: ", ex);
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
    // 04. Get Walker KYC by UID
    // GET /api/walker-kyc/uid/{uid}
    // =========================================================
    @GetMapping("/uid/{uid}")
    public ResponseEntity<?> getWalkerKycByUid(@PathVariable String uid) {

        try {
            logger.info("⚡ API Request: GET /api/walker-kyc/uid/{}", uid);

            UUID uuid = UUID.fromString(uid);
            Optional<WalkerToClientKycEntity> kyc = walkerKycService.getWalkerKycByUid(uuid);

            if (kyc.isPresent()) {
                logger.info("✅ Success: KYC record found | UID: {}", uid);

                WalkerToClientKycEntity entity = kyc.get();

                Map<String, Object> responseData = new HashMap<>();
                responseData.put("kycUid", entity.getUid());
                responseData.put("petUid", entity.getPetUid());
                responseData.put("status", entity.getStatus().toString());
                responseData.put("statusDescription", getStatusDescription(entity.getStatus()));
                responseData.put("petNames", entity.getPetNames());
                responseData.put("breedType", entity.getBreedType());
                responseData.put("age", entity.getAge());
                responseData.put("energyLevel", entity.getEnergyLevel());
                responseData.put("walkingExperience", entity.getWalkingExperience());
                responseData.put("preferredWalkType", entity.getPreferredWalkType());
                responseData.put("preferredStartDate", entity.getPreferredStartDate());
                responseData.put("consent", entity.getConsent());
                responseData.put("signature", entity.getSignature());
                responseData.put("createdAt", entity.getCreatedAt());
                responseData.put("updatedAt", entity.getUpdatedAt());
                responseData.put("fullRecord", entity);

                return ResponseEntity.ok(buildSuccessResponse(
                        "KYC_RECORD_FOUND",
                        "Walker KYC record retrieved successfully.",
                        responseData));
            } else {
                logger.warn("❌ Not Found: KYC with UID '{}' does not exist", uid);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(buildErrorResponse(
                                "KYC_NOT_FOUND",
                                "Record Not Found",
                                "Walker KYC with the specified UID does not exist in the database.",
                                "uid",
                                uid));
            }

        } catch (IllegalArgumentException e) {
            logger.warn("❌ Invalid UID format: {}", uid);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(buildErrorResponse(
                            "INVALID_UID_FORMAT",
                            "Invalid UID Format",
                            "UID must be in valid UUID format (e.g., '550e8400-e29b-41d4-a716-446655440000').",
                            "uid",
                            uid));

        } catch (Exception ex) {
            logger.error("💥 Error fetching Walker KYC by UID: {}", uid, ex);
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
    // 05. Get Walker KYC by Pet UID
    // GET /api/walker-kyc/pet-uid/{petUid}
    // =========================================================
    @GetMapping("/pet-uid/{petUid}")
    public ResponseEntity<?> getWalkerKycByPetUid(@PathVariable String petUid) {

        try {
            logger.info("⚡ API Request: GET /api/walker-kyc/pet-uid/{}", petUid);

            Optional<WalkerToClientKycEntity> kyc = walkerKycService.getWalkerKycByPetUid(petUid);

            if (kyc.isPresent()) {
                logger.info("✅ Success: KYC record found for PetUID: {}", petUid);

                WalkerToClientKycEntity entity = kyc.get();

                Map<String, Object> responseData = new HashMap<>();
                responseData.put("kycUid", entity.getUid());
                responseData.put("petUid", entity.getPetUid());
                responseData.put("status", entity.getStatus().toString());
                responseData.put("fullRecord", entity);

                return ResponseEntity.ok(buildSuccessResponse(
                        "KYC_RECORD_FOUND",
                        "Walker KYC record found for the specified pet.",
                        responseData));
            } else {
                logger.warn("❌ Not Found: KYC for PetUID '{}' does not exist", petUid);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(buildErrorResponse(
                                "KYC_NOT_FOUND",
                                "Record Not Found",
                                "No Walker KYC found for the specified pet UID.",
                                "petUid",
                                petUid));
            }

        } catch (Exception ex) {
            logger.error("💥 Error fetching Walker KYC by PetUID: {}", petUid, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse(
                            "INTERNAL_SERVER_ERROR",
                            "Failed to Retrieve Record",
                            "Unable to fetch KYC record. Please try again later.",
                            "petUid",
                            petUid));
        }
    }

    // =========================================================
    // 06. Update KYC Status by UID
    // PATCH /api/walker-kyc/status/{uid}
    // Body: { "status": "APPROVED" }
    // =========================================================
    @PatchMapping("/status/{uid}")
    public ResponseEntity<?> updateKycStatusByUid(
            @PathVariable String uid,
            @RequestBody Map<String, String> statusRequest) {

        try {
            logger.info("⚡ API Request: PATCH /api/walker-kyc/status/{}", uid);

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

            UUID uuid = UUID.fromString(uid);
            WalkerToClientKycEntity updatedKyc = walkerKycService.updateStatusByUid(uuid, status);

            logger.info("✅ Success: KYC status updated | UID: {} | New Status: {}", uid, updatedKyc.getStatus());

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("kycUid", uid);
            responseData.put("newStatus", updatedKyc.getStatus().toString());
            responseData.put("statusDescription", getStatusDescription(updatedKyc.getStatus()));
            responseData.put("updatedAt", updatedKyc.getUpdatedAt());
            responseData.put("fullRecord", updatedKyc);

            return ResponseEntity.ok(buildSuccessResponse(
                    "KYC_STATUS_UPDATED",
                    "Walker KYC status has been updated successfully.",
                    responseData));

        } catch (IllegalArgumentException e) {
            logger.warn("❌ Invalid UID format: {}", uid);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(buildErrorResponse(
                            "INVALID_UID_FORMAT",
                            "Invalid UID Format",
                            "UID must be in valid UUID format.",
                            "uid",
                            uid));

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
    // 07. Delete Walker KYC by UID
    // DELETE /api/walker-kyc/uid/{uid}
    // =========================================================
    @DeleteMapping("/uid/{uid}")
    public ResponseEntity<?> deleteWalkerKycByUid(@PathVariable String uid) {

        try {
            logger.info("⚡ API Request: DELETE /api/walker-kyc/uid/{}", uid);

            UUID uuid = UUID.fromString(uid);
            walkerKycService.deleteWalkerKycByUid(uuid);

            logger.info("✅ Success: Walker KYC deleted | UID: {}", uid);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("deletedKycUid", uid);
            responseData.put("deletionTimestamp", java.time.LocalDateTime.now());

            return ResponseEntity.ok(buildSuccessResponse(
                    "KYC_DELETED_SUCCESSFULLY",
                    "Walker KYC has been permanently deleted from the database.",
                    responseData));

        } catch (IllegalArgumentException e) {
            logger.warn("❌ Invalid UID format: {}", uid);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(buildErrorResponse(
                            "INVALID_UID_FORMAT",
                            "Invalid UID Format",
                            "UID must be in valid UUID format.",
                            "uid",
                            uid));

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
            logger.error("💥 Error deleting Walker KYC with UID: {}", uid, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse(
                            "INTERNAL_SERVER_ERROR",
                            "Failed to Delete Record",
                            "Unable to delete KYC record. Please try again later.",
                            "uid",
                            uid));
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
                return "KYC is under review and pending approval from the walking service.";
            case APPROVED:
                return "KYC has been approved. Walking services can now be scheduled.";
            case REJECTED:
                return "KYC has been rejected. Please contact support for more information.";
            default:
                return "Unknown status";
        }
    }
}