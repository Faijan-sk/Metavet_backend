package com.example.demo.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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

import com.example.demo.Dto.GroomerToClientKycRequestDto;
import com.example.demo.Entities.GroomerToClientKycEntity;
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

    /**
     * Create new Groomer KYC
     * POST /api/groomer-kyc
     */
    @PostMapping
    public ResponseEntity<?> createGroomerKyc(
            @Valid @RequestBody GroomerToClientKycRequestDto dto,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            BindingResult bindingResult) {

        try {
            logger.info("Received Groomer KYC creation request for petUid: {}", dto.getPetUid());
            
            // Check for validation errors from @Valid annotations
            if (bindingResult.hasErrors()) {
                logger.warn("Validation failed for GroomerToClientKycRequestDto");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(buildValidationErrorResponse(bindingResult));
            }

            // Extract and validate Authorization header
            String accessToken = extractAccessToken(authHeader);
            if (accessToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(buildErrorResponse("Unauthorized", "Authorization header is required. Please provide 'Authorization: Bearer <token>'"));
            }

            // Call service to create KYC
            GroomerToClientKycEntity createdKyc = groomerKycService.createGroomerKyc(dto, accessToken);
            
            logger.info("Groomer KYC created successfully with UID: {}", createdKyc.getUid());
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(buildSuccessResponse("Groomer KYC created successfully", createdKyc));

        } catch (ValidationException ve) {
            logger.warn("ValidationException: {}", ve.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(buildErrorResponse("Validation Error", ve.getMessage()));

        } catch (Exception ex) {
            logger.error("Unexpected error while creating Groomer KYC", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("Internal Server Error", "An unexpected error occurred. Please contact support."));
        }
    }

    /**
     * Update existing Groomer KYC
     * PUT /api/groomer-kyc/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGroomerKyc(
            @PathVariable Long id,
            @Valid @RequestBody GroomerToClientKycRequestDto dto,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            BindingResult bindingResult) {

        try {
            logger.info("Received Groomer KYC update request for ID: {}", id);
            
            // Check for validation errors
            if (bindingResult.hasErrors()) {
                logger.warn("Validation failed for updating Groomer KYC");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(buildValidationErrorResponse(bindingResult));
            }

            // Extract and validate Authorization header
            String accessToken = extractAccessToken(authHeader);
            if (accessToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(buildErrorResponse("Unauthorized", "Authorization header is required. Please provide 'Authorization: Bearer <token>'"));
            }

            // Call service to update KYC
            GroomerToClientKycEntity updatedKyc = groomerKycService.updateGroomerKyc(id, dto, accessToken);
            
            logger.info("Groomer KYC updated successfully with ID: {}", id);
            return ResponseEntity.ok(buildSuccessResponse("Groomer KYC updated successfully", updatedKyc));

        } catch (ValidationException ve) {
            logger.warn("ValidationException: {}", ve.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(buildErrorResponse("Validation Error", ve.getMessage()));

        } catch (Exception ex) {
            logger.error("Unexpected error while updating Groomer KYC", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("Internal Server Error", "An unexpected error occurred. Please contact support."));
        }
    }

    /**
     * Get All Groomer KYCs
     * GET /api/groomer-kyc
     */
    @GetMapping
    public ResponseEntity<?> getAllGroomerKycs() {
        try {
            List<GroomerToClientKycEntity> kycs = groomerKycService.getAllGroomerKycs();
            
            logger.info("Retrieved {} KYC records", kycs.size());
            return ResponseEntity.ok(buildSuccessResponse("KYC records retrieved successfully", kycs));

        } catch (Exception ex) {
            logger.error("Error fetching all Groomer KYCs", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("Internal Server Error", "Failed to retrieve KYC records"));
        }
    }

    /**
     * Get Groomer KYC by UID
     * GET /api/groomer-kyc/uid/{uid}
     */
    @GetMapping("/uid/{uid}")
    public ResponseEntity<?> getGroomerKycByUid(@PathVariable String uid) {
        try {
            Optional<GroomerToClientKycEntity> kyc = groomerKycService.getGroomerKycByUid(uid);
            
            if (kyc.isPresent()) {
                return ResponseEntity.ok(buildSuccessResponse("KYC record found", kyc.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(buildErrorResponse("Not Found", "Groomer KYC with UID '" + uid + "' does not exist"));
            }

        } catch (ValidationException ve) {
            logger.warn("ValidationException: {}", ve.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(buildErrorResponse("Invalid UID", ve.getMessage()));

        } catch (Exception ex) {
            logger.error("Error fetching Groomer KYC by UID: {}", uid, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("Internal Server Error", "Failed to retrieve KYC record"));
        }
    }

    /**
     * Get KYC Status by UID
     * GET /api/groomer-kyc/status/{uid}
     */
    @GetMapping("/status/{uid}")
    public ResponseEntity<?> getKycStatusByUid(@PathVariable String uid) {
        try {
            KycStatus status = groomerKycService.getKycStatusByUid(uid);
            
            Map<String, Object> data = new HashMap<>();
            data.put("uid", uid);
            data.put("status", status.toString());
            
            return ResponseEntity.ok(buildSuccessResponse("KYC status retrieved successfully", data));

        } catch (ValidationException ve) {
            logger.warn("ValidationException: {}", ve.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(buildErrorResponse("Validation Error", ve.getMessage()));

        } catch (Exception ex) {
            logger.error("Error fetching KYC status by UID: {}", uid, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("Internal Server Error", "Failed to retrieve KYC status"));
        }
    }

    /**
     * Update KYC Status by UID
     * PATCH /api/groomer-kyc/status/{uid}
     * Body: { "status": "APPROVED" }
     */
    @PatchMapping("/status/{uid}")
    public ResponseEntity<?> updateKycStatusByUid(
            @PathVariable String uid,
            @RequestBody Map<String, String> statusRequest) {
        
        try {
            String status = statusRequest.get("status");
            
            if (status == null || status.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(buildErrorResponse("Bad Request", "Status field is required in request body. Example: {\"status\": \"APPROVED\"}"));
            }

            GroomerToClientKycEntity updatedKyc = groomerKycService.updateKycStatusByUid(uid, status);
            
            logger.info("KYC status updated successfully for UID: {} to {}", uid, status);
            return ResponseEntity.ok(buildSuccessResponse("KYC status updated successfully", updatedKyc));

        } catch (ValidationException ve) {
            logger.warn("ValidationException: {}", ve.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(buildErrorResponse("Validation Error", ve.getMessage()));

        } catch (Exception ex) {
            logger.error("Error updating KYC status by UID: {}", uid, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("Internal Server Error", "Failed to update KYC status"));
        }
    }

    /**
     * Delete Groomer KYC by UID
     * DELETE /api/groomer-kyc/uid/{uid}
     */
    @DeleteMapping("/uid/{uid}")
    public ResponseEntity<?> deleteGroomerKycByUid(@PathVariable String uid) {
        try {
            groomerKycService.deleteGroomerKycByUid(uid);
            
            logger.info("Groomer KYC deleted successfully with UID: {}", uid);
            return ResponseEntity.ok(buildSuccessResponse("Groomer KYC deleted successfully", null));

        } catch (ValidationException ve) {
            logger.warn("ValidationException: {}", ve.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(buildErrorResponse("Not Found", ve.getMessage()));

        } catch (Exception ex) {
            logger.error("Error deleting Groomer KYC with UID: {}", uid, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("Internal Server Error", "Failed to delete KYC record"));
        }
    }

    /**
     * Get Groomer KYC by ID
     * GET /api/groomer-kyc/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getGroomerKycById(@PathVariable Long id) {
        try {
            Optional<GroomerToClientKycEntity> kyc = groomerKycService.getGroomerKycById(id);
            
            if (kyc.isPresent()) {
                return ResponseEntity.ok(buildSuccessResponse("KYC record found", kyc.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(buildErrorResponse("Not Found", "Groomer KYC with ID " + id + " does not exist"));
            }

        } catch (Exception ex) {
            logger.error("Error fetching Groomer KYC by ID: {}", id, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("Internal Server Error", "Failed to retrieve KYC record"));
        }
    }

    /**
     * Get Groomer KYC by Pet ID
     * GET /api/groomer-kyc/pet/{petId}
     */
    @GetMapping("/pet/{petId}")
    public ResponseEntity<?> getGroomerKycByPetId(@PathVariable Long petId) {
        try {
            Optional<GroomerToClientKycEntity> kyc = groomerKycService.getGroomerKycByPetId(petId);
            
            if (kyc.isPresent()) {
                return ResponseEntity.ok(buildSuccessResponse("KYC record found for pet", kyc.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(buildErrorResponse("Not Found", "Groomer KYC for Pet ID " + petId + " does not exist"));
            }

        } catch (Exception ex) {
            logger.error("Error fetching Groomer KYC by Pet ID: {}", petId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("Internal Server Error", "Failed to retrieve KYC record"));
        }
    }
    
    /**
     * Get Groomer KYC by Pet UID
     * GET /api/groomer-kyc/pet-uid/{petUid}
     */
    @GetMapping("/pet-uid/{petUid}")
    public ResponseEntity<?> getGroomerKycByPetUid(@PathVariable String petUid) {
        try {
            Optional<GroomerToClientKycEntity> kyc = groomerKycService.getGroomerKycByPetUid(petUid);
            
            if (kyc.isPresent()) {
                return ResponseEntity.ok(buildSuccessResponse("KYC record found for pet", kyc.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(buildErrorResponse("Not Found", "Groomer KYC for Pet UID '" + petUid + "' does not exist"));
            }

        } catch (Exception ex) {
            logger.error("Error fetching Groomer KYC by Pet UID: {}", petUid, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("Internal Server Error", "Failed to retrieve KYC record"));
        }
    }

    /**
     * Delete Groomer KYC by ID
     * DELETE /api/groomer-kyc/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroomerKyc(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            // Extract and validate Authorization header
            String accessToken = extractAccessToken(authHeader);
            if (accessToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(buildErrorResponse("Unauthorized", "Authorization header is required. Please provide 'Authorization: Bearer <token>'"));
            }

            groomerKycService.deleteGroomerKyc(id, accessToken);
            
            logger.info("Groomer KYC deleted successfully with ID: {}", id);
            return ResponseEntity.ok(buildSuccessResponse("Groomer KYC deleted successfully", null));

        } catch (ValidationException ve) {
            logger.warn("ValidationException: {}", ve.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(buildErrorResponse("Not Found or Unauthorized", ve.getMessage()));

        } catch (Exception ex) {
            logger.error("Error deleting Groomer KYC with ID: {}", id, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("Internal Server Error", "Failed to delete KYC record"));
        }
    }

    // ==================== Helper Methods ====================

    /**
     * Extract access token from Authorization header
     */
    private String extractAccessToken(String authHeader) {
        if (authHeader == null || authHeader.trim().isEmpty()) {
            return null;
        }
        
        if (authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        
        return null;
    }

    /**
     * Build validation error response with field-specific errors
     */
    private Map<String, Object> buildValidationErrorResponse(BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", "Validation Failed");
        response.put("message", "Please correct the following errors and try again");
        
        List<Map<String, String>> fieldErrors = new ArrayList<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            Map<String, String> fieldError = new HashMap<>();
            fieldError.put("field", error.getField());
            fieldError.put("message", error.getDefaultMessage());
            fieldError.put("rejectedValue", error.getRejectedValue() != null ? error.getRejectedValue().toString() : "null");
            fieldErrors.add(fieldError);
        }
        
        response.put("fieldErrors", fieldErrors);
        response.put("errorCount", fieldErrors.size());
        
        return response;
    }

    /**
     * Build success response
     */
    private Map<String, Object> buildSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        if (data != null) {
            response.put("data", data);
        }
        return response;
    }

    /**
     * Build error response
     */
    private Map<String, Object> buildErrorResponse(String error, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", error);
        response.put("message", message);
        return response;
    }
}