package com.example.demo.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Config.SpringSecurityAuditorAware;
import com.example.demo.Dto.PetBehavioristKycRequestDto;
import com.example.demo.Entities.GroomerToClientKycEntity;
import com.example.demo.Entities.PetBehavioristKycEntity;
import com.example.demo.Entities.UsersEntity;
import com.example.demo.Service.PetBehavioristKycService;
import org.slf4j.Logger;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;

@RestController
@RequestMapping("/api/behaviorist-kyc")
public class PetBehavioristKycController {
	
	 @Autowired
	 private SpringSecurityAuditorAware auditorAware;
	    
	 
	 

    @Autowired
    private PetBehavioristKycService behavioristKycService;

    // ===================================================
    // 01. CREATE KYC
    // ===================================================
    @PostMapping
    public ResponseEntity<Map<String, Object>> createKyc(
            @Valid @RequestBody PetBehavioristKycRequestDto dto,
            BindingResult bindingResult) {
        
        Map<String, Object> response = new HashMap<>();
        
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());
            
            response.put("success", false);
            response.put("message", "Validation failed");
            response.put("errors", errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        try {
            PetBehavioristKycEntity createdKyc = behavioristKycService.createBehavioristKyc(dto);
            
            response.put("success", true);
            response.put("message", "Behaviorist KYC created successfully");
            response.put("data", createdKyc);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (ValidationException ex) {
            response.put("success", false);
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            
        } catch (Exception ex) {
            response.put("success", false);
            response.put("message", "An error occurred while creating KYC");
            response.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ===================================================
    // 02. GET ALL KYC RECORDS
    // ===================================================
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllKyc() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<PetBehavioristKycEntity> allKyc = behavioristKycService.findAll();
            
            response.put("success", true);
            response.put("message", "Behaviorist KYC records retrieved successfully");
            response.put("count", allKyc.size());
            response.put("data", allKyc);
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            response.put("success", false);
            response.put("message", "An error occurred while retrieving KYC records");
            response.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ===================================================
    // 03. GET KYC BY UID
    // ===================================================
    @GetMapping("/{uid}")
    public ResponseEntity<Map<String, Object>> getKycByUid(@PathVariable String uid) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            PetBehavioristKycEntity kyc = behavioristKycService.getByUid(uid);
            
            response.put("success", true);
            response.put("message", "Behaviorist KYC retrieved successfully");
            response.put("data", kyc);
            return ResponseEntity.ok(response);
            
        } catch (ValidationException ex) {
            response.put("success", false);
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            
        } catch (Exception ex) {
            response.put("success", false);
            response.put("message", "An error occurred while retrieving KYC");
            response.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ===================================================
    // 04. UPDATE KYC STATUS
    // ===================================================
    @PatchMapping("/{uid}/status")
    public ResponseEntity<Map<String, Object>> updateKycStatus(
            @PathVariable String uid,
            @RequestBody Map<String, String> statusRequest) {
        
        Map<String, Object> response = new HashMap<>();
        
        String status = statusRequest.get("status");
        if (status == null || status.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Status field is required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        try {
            PetBehavioristKycEntity updatedKyc = behavioristKycService.updateStatusByUid(uid, status);
            
            response.put("success", true);
            response.put("message", "KYC status updated successfully");
            response.put("data", updatedKyc);
            return ResponseEntity.ok(response);
            
        } catch (ValidationException ex) {
            response.put("success", false);
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            
        } catch (Exception ex) {
            response.put("success", false);
            response.put("message", "An error occurred while updating KYC status");
            response.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ===================================================
    // 05. DELETE KYC BY UID
    // ===================================================
    @DeleteMapping("/{uid}")
    public ResponseEntity<Map<String, Object>> deleteKyc(@PathVariable String uid) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            behavioristKycService.deleteByUid(uid);
            
            response.put("success", true);
            response.put("message", "Behaviorist KYC deleted successfully");
            return ResponseEntity.ok(response);
            
        } catch (ValidationException ex) {
            response.put("success", false);
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            
        } catch (Exception ex) {
            response.put("success", false);
            response.put("message", "An error occurred while deleting KYC");
            response.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    
 // =========================================================
    // 06. Get KYC Status for Logged-in User
    // GET /api/behaviorist-kyc/get-status
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
            Optional<PetBehavioristKycEntity> kycOpt =
            		behavioristKycService.getKycByUserUuid(userUid);
            
            if (!kycOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(buildErrorResponse(
                                "KYC_NOT_FOUND",
                                "KYC Not Found",
                                "No KYC record exists for the logged-in user.",
                                "userUid",
                                userUid));
            }
            
            PetBehavioristKycEntity kyc = kycOpt.get();
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("kycUid", kyc.getUid());
            responseData.put("status", kyc.getKycStatus());
           
            responseData.put("updatedAt", kyc.getUpdatedAt());
            
            return ResponseEntity.ok(buildSuccessResponse(
                    "KYC_STATUS_FETCHED",
                    "KYC status retrieved successfully.",
                    responseData));
            
        } catch (Exception ex) {
           
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse(
                            "INTERNAL_SERVER_ERROR",
                            "Failed to Fetch Status",
                            "Unable to fetch KYC status. Please try again later.",
                            null,
                            ex.getMessage()));
        }
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
    
}