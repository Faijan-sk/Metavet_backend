package com.example.demo.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Dto.PetBehavioristKycRequestDto;
import com.example.demo.Entities.PetBehavioristKycEntity;
import com.example.demo.Service.PetBehavioristKycService;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;

@RestController
@RequestMapping("/api/behaviorist-kyc")
public class PetBehavioristKycController {

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
}