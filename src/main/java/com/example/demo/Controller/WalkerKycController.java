package com.example.demo.Controller;

import org.springframework.data.domain.Page;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Dto.ApiResponse;
import com.example.demo.Dto.GetAllWalkerResponse;
import com.example.demo.Dto.WalkerKycRequestDto;
import com.example.demo.Entities.WalkerKyc;
import com.example.demo.Entities.WalkerKyc.ApplicationStatus;
import com.example.demo.Repository.WalkerKycRepo;
import com.example.demo.Service.WalkerKycService;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;


@RestController
@RequestMapping("/walkerkyc")
public class WalkerKycController {

    private static final Logger logger = LoggerFactory.getLogger(WalkerKycController.class);

    @Autowired
    private WalkerKycService walkerKycService;

    @Autowired
    private WalkerKycRepo walkerKycRepo;

    private static final String DOCUMENT_ROOT = System.getProperty("user.dir");
    private static final String QrFolder = "walker_kyc";

    // ===================== CREATE KYC =====================
    /**
     * Response Scenarios:
     * 1. Success (201): Returns created KYC details with uid, id, and all file URLs
     * 2. Validation Error (400): Returns field-specific validation errors
     * 3. Duplicate Email (400): Email already exists
     * 4. File Processing Error (500): Failed to save uploaded files
     * 5. Unexpected Error (500): Any other server error
     */
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<WalkerKycRequestDto>> createWalkerKyc(
            @Valid @ModelAttribute WalkerKycRequestDto dto,
            BindingResult bindingResult,
            @RequestParam(value = "documentNames", required = false) List<String> documentNames,
            @RequestParam(value = "documentFiles", required = false) List<MultipartFile> documentFiles) {

        try {
            // Handle Spring validation errors
            if (bindingResult != null && bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                for (FieldError error : bindingResult.getFieldErrors()) {
                    errors.put(error.getField(), error.getDefaultMessage());
                }
                logger.warn("Validation failed for WalkerKycRequestDto: {}", errors);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.validationError("Validation failed. Please check the submitted data.", errors));
            }

            // Create KYC
            WalkerKycRequestDto createdKyc = walkerKycService.createWalkerKyc(dto, documentNames, documentFiles);
            logger.info("Walker KYC created successfully with uid: {}", createdKyc.getUid());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Walker KYC application submitted successfully.", createdKyc));

        } catch (ValidationException ve) {
            logger.warn("ValidationException while creating KYC: {}", ve.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ve.getMessage(), 400));

        } catch (IOException ioe) {
            logger.error("IOException while creating KYC", ioe);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.serverError("Failed to process uploaded files. Please try again."));

        } catch (Exception ex) {
            logger.error("Unexpected error while creating KYC", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.serverError("An unexpected error occurred while processing your request."));
        }
    }

    // ===================== GET ALL KYC =====================
    /**
     * Response Scenarios:
     * 1. Success (200): Returns list of all KYC applications (empty list if none)
     * 2. Error (500): Database/server error
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<WalkerKycRequestDto>>> getAllKyc() {
        try {
            List<WalkerKycRequestDto> allKyc = walkerKycService.getAll();
            logger.info("Retrieved {} KYC records", allKyc.size());
            return ResponseEntity.ok(ApiResponse.success("KYC records retrieved successfully.", allKyc));
        } catch (Exception ex) {
            logger.error("Error retrieving all KYC records", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.serverError("Failed to retrieve KYC records."));
        }
    }

    // ===================== GET SINGLE KYC BY UID =====================
    /**
     * Response Scenarios:
     * 1. Success (200): Returns KYC details with all file URLs
     * 2. Not Found (404): KYC with given uid doesn't exist
     * 3. Error (500): Database/server error
     */
    @GetMapping("/uid/{uid}")
    public ResponseEntity<ApiResponse<WalkerKycRequestDto>> getWalkerKycByUid(@PathVariable UUID uid) {
        try {
            logger.info("Fetching KYC record with uid: {}", uid);
            WalkerKycRequestDto kyc = walkerKycService.getWalkerKycByUid(uid);
            return ResponseEntity.ok(ApiResponse.success("KYC record retrieved successfully.", kyc));
        } catch (ValidationException ve) {
            logger.warn("KYC not found with uid: {}", uid);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound(ve.getMessage()));
        } catch (Exception ex) {
            logger.error("Error fetching KYC with uid: {}", uid, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.serverError("Failed to fetch KYC record."));
        }
    }

    // ===================== GET SINGLE KYC BY ID =====================
    /**
     * Response Scenarios:
     * 1. Success (200): Returns KYC details
     * 2. Not Found (404): KYC with given id doesn't exist
     * 3. Error (500): Database/server error
     */
    @GetMapping("/id/{id}")
    public ResponseEntity<ApiResponse<WalkerKycRequestDto>> getWalkerKycById(@PathVariable Long id) {
        try {
            logger.info("Fetching KYC record with id: {}", id);
            WalkerKycRequestDto kyc = walkerKycService.getWalkerKycById(id);
            return ResponseEntity.ok(ApiResponse.success("KYC record retrieved successfully.", kyc));
        } catch (ValidationException ve) {
            logger.warn("KYC not found with id: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound(ve.getMessage()));
        } catch (Exception ex) {
            logger.error("Error fetching KYC with id: {}", id, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.serverError("Failed to fetch KYC record."));
        }
    }

    // ===================== GET SINGLE KYC BY EMAIL =====================
    /**
     * Response Scenarios:
     * 1. Success (200): Returns KYC details
     * 2. Not Found (404): KYC with given email doesn't exist
     * 3. Error (500): Database/server error
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<WalkerKycRequestDto>> getWalkerKycByEmail(@PathVariable String email) {
        try {
            logger.info("Fetching KYC record with email: {}", email);
            WalkerKycRequestDto kyc = walkerKycService.getWalkerKycByEmail(email);
            return ResponseEntity.ok(ApiResponse.success("KYC record retrieved successfully.", kyc));
        } catch (ValidationException ve) {
            logger.warn("KYC not found with email: {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound(ve.getMessage()));
        } catch (Exception ex) {
            logger.error("Error fetching KYC with email: {}", email, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.serverError("Failed to fetch KYC record."));
        }
    }

    // ===================== SEARCH BY NAME OR BUSINESS =====================
    /**
     * Response Scenarios:
     * 1. Success (200): Returns matching KYC records (empty list if none match)
     * 2. Error (500): Database/server error
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<WalkerKycRequestDto>>> searchKyc(
            @RequestParam(required = false) String query) {
        try {
            List<WalkerKycRequestDto> results = walkerKycService.searchByNameOrBusiness(query);
            logger.info("Search query '{}' returned {} results", query, results.size());
            return ResponseEntity.ok(ApiResponse.success("Search completed successfully.", results));
        } catch (Exception ex) {
            logger.error("Error searching KYC records with query: {}", query, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.serverError("Failed to search KYC records."));
        }
    }

    // ===================== GET BY STATUS =====================
    /**
     * Response Scenarios:
     * 1. Success (200): Returns KYC records with given status
     * 2. Error (400): Invalid status value
     * 3. Error (500): Database/server error
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<WalkerKycRequestDto>>> getByStatus(@PathVariable String status) {
        try {
            ApplicationStatus appStatus = ApplicationStatus.valueOf(status.toUpperCase());
            List<WalkerKycRequestDto> results = walkerKycService.getByStatus(appStatus);
            logger.info("Found {} KYC records with status: {}", results.size(), status);
            return ResponseEntity.ok(ApiResponse.success("Records retrieved successfully.", results));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid status value: {}", status);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid status. Valid values: PENDING, APPROVED, REJECTED, UNDER_REVIEW", 400));
        } catch (Exception ex) {
            logger.error("Error fetching KYC records by status: {}", status, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.serverError("Failed to fetch KYC records."));
        }
    }

    // ===================== GET UPLOADED FILE =====================
    /**
     * Response Scenarios:
     * 1. Success (200): Returns file with proper content-type and disposition headers
     * 2. Not Found (404): Document/file not found
     * 3. Error (400): Invalid file type or path traversal attempt
     * 4. Error (500): File read error
     */
    @GetMapping("/uploaded_files/{uid}/{fileType}")
    public ResponseEntity<?> getDocument(@PathVariable UUID uid, @PathVariable String fileType) {
        try {
            WalkerKyc document = walkerKycService.getEntityByUid(uid);

            byte[] fileBytes = null;
            String fileName = null;

            switch (fileType.toLowerCase()) {
            case "pet_care_certification":
                fileBytes = document.getPetCareCertificationDoc();
                fileName = document.getCertificationFilePath();
                break;
            case "bonded_or_insured":
                fileBytes = document.getBondedOrInsuredDoc();
                fileName = document.getBondedFilePath();
                break;
            case "pet_first_aid_certificate":
                fileBytes = document.getPetFirstAidCertificateDoc();
                fileName = document.getFirstAidFilePath();
                break;
            case "criminal_record":
                fileBytes = document.getCriminalRecordDoc();
                fileName = document.getCriminalCheckFilePath();
                break;
            case "liability_insurance":
                fileBytes = document.getLiabilityInsuranceDoc();
                fileName = document.getLiabilityFilePath();
                break;
            case "business_license":
                fileBytes = document.getBusinessLicenseDoc();
                fileName = document.getBusinessLicenseFilePath();
                break;
            default:
                logger.warn("Unknown file type requested: {}", fileType);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Unknown file type: " + fileType, 400));
            }

            if ((fileName == null || fileName.isBlank()) && (fileBytes == null || fileBytes.length == 0)) {
                logger.warn("File not available for uid: {} and type: {}", uid, fileType);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.notFound("Requested file not available for this record."));
            }

            // Serve from database bytes if available
            if (fileBytes != null && fileBytes.length > 0) {
                ByteArrayResource resource = new ByteArrayResource(fileBytes);
                MediaType mediaType = MediaTypeFactory.getMediaType(fileName != null ? fileName : "file")
                        .orElse(MediaType.APPLICATION_OCTET_STREAM);
                String dispositionName = (fileName != null && !fileName.isBlank())
                        ? Paths.get(fileName).getFileName().toString()
                        : fileType + "-" + uid.toString();

                return ResponseEntity.ok()
                        .contentType(mediaType)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dispositionName + "\"")
                        .body(resource);
            }

            // Serve from disk
            Path filePath = Paths.get(fileName).normalize();
            Path rootPath = Paths.get(DOCUMENT_ROOT).toAbsolutePath().normalize();

            if (!filePath.toAbsolutePath().startsWith(rootPath)) {
                logger.error("Path traversal attempt detected for file: {}", fileName);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Invalid file path", 400));
            }

            if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
                logger.warn("File not found or not readable: {}", filePath);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.notFound("File not found on server."));
            }

            Resource resource = new UrlResource(filePath.toUri());
            MediaType mediaType = MediaTypeFactory.getMediaType(filePath.getFileName().toString())
                    .orElse(MediaType.APPLICATION_OCTET_STREAM);

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getFileName().toString() + "\"")
                    .body(resource);

        } catch (ValidationException ve) {
            logger.warn("Document not found for uid: {}", uid);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound(ve.getMessage()));
        } catch (MalformedURLException e) {
            logger.error("Malformed URL for file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.serverError("Error accessing file."));
        } catch (Exception ex) {
            logger.error("Error retrieving document for uid: {} and type: {}", uid, fileType, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.serverError("Failed to retrieve document."));
        }
    }

    // ===================== DELETE KYC BY UID =====================
    /**
     * Response Scenarios:
     * 1. Success (200): KYC and associated files deleted
     * 2. Not Found (404): KYC with given uid doesn't exist
     * 3. Error (500): Deletion failed
     */
    @DeleteMapping("/uid/{uid}")
    public ResponseEntity<ApiResponse<Void>> deleteWalkerKycByUid(@PathVariable UUID uid) {
        try {
            logger.info("Deleting KYC record with uid: {}", uid);
            walkerKycService.deleteWalkerKycByUid(uid);
            return ResponseEntity.ok(ApiResponse.success("Walker KYC deleted successfully."));
        } catch (ValidationException ve) {
            logger.warn("KYC not found for deletion with uid: {}", uid);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound(ve.getMessage()));
        } catch (Exception ex) {
            logger.error("Error deleting KYC with uid: {}", uid, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.serverError("Failed to delete KYC record."));
        }
    }

    // ===================== DELETE KYC BY ID =====================
    /**
     * Response Scenarios:
     * 1. Success (200): KYC deleted
     * 2. Not Found (404): KYC with given id doesn't exist
     * 3. Error (500): Deletion failed
     */
    @DeleteMapping("/id/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteWalkerKycById(@PathVariable Long id) {
        try {
            logger.info("Deleting KYC record with id: {}", id);
            walkerKycService.deleteWalkerKycById(id);
            return ResponseEntity.ok(ApiResponse.success("Walker KYC deleted successfully."));
        } catch (ValidationException ve) {
            logger.warn("KYC not found for deletion with id: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound(ve.getMessage()));
        } catch (Exception ex) {
            logger.error("Error deleting KYC with id: {}", id, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.serverError("Failed to delete KYC record."));
        }
    }

    // ===================== GET STATUS BY UID =====================
    /**
     * Response Scenarios:
     * 1. Success (200): Returns {uid, status}
     * 2. Not Found (404): KYC with given uid doesn't exist
     * 3. Error (500): Database error
     */
    @GetMapping("/uid/{uid}/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatusByUid(@PathVariable UUID uid) {
        try {
            logger.info("Fetching status for KYC with uid: {}", uid);
            ApplicationStatus status = walkerKycService.getStatusByUid(uid);

            Map<String, Object> response = new HashMap<>();
            response.put("uid", uid);
            response.put("status", status);

            return ResponseEntity.ok(ApiResponse.success("Status retrieved successfully.", response));
        } catch (ValidationException ve) {
            logger.warn("KYC not found with uid: {}", uid);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound(ve.getMessage()));
        } catch (Exception ex) {
            logger.error("Error fetching status for KYC with uid: {}", uid, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.serverError("Failed to fetch status."));
        }
    }

    // ===================== GET STATUS BY ID =====================
    /**
     * Response Scenarios:
     * 1. Success (200): Returns {id, status}
     * 2. Not Found (404): KYC with given id doesn't exist
     * 3. Error (500): Database error
     */
    @GetMapping("/id/{id}/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatusById(@PathVariable Long id) {
        try {
            logger.info("Fetching status for KYC with id: {}", id);
            ApplicationStatus status = walkerKycService.getStatusById(id);

            Map<String, Object> response = new HashMap<>();
            response.put("id", id);
            response.put("status", status);

            return ResponseEntity.ok(ApiResponse.success("Status retrieved successfully.", response));
        } catch (ValidationException ve) {
            logger.warn("KYC not found with id: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound(ve.getMessage()));
        } catch (Exception ex) {
            logger.error("Error fetching status for KYC with id: {}", id, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.serverError("Failed to fetch status."));
        }
    }

    // ===================== UPDATE APPLICATION STATUS BY UID =====================
    /**
     * Response Scenarios:
     * 1. Success (200): Returns updated KYC details
     * 2. Not Found (404): KYC with given uid doesn't exist
     * 3. Error (400): Invalid status value
     * 4. Error (500): Update failed
     */
    @PatchMapping("/uid/{uid}/status")
    public ResponseEntity<ApiResponse<WalkerKycRequestDto>> updateApplicationStatusByUid(
            @PathVariable UUID uid,
            @RequestParam ApplicationStatus status) {
        try {
            logger.info("Updating status for KYC with uid: {} to {}", uid, status);
            WalkerKycRequestDto updatedKyc = walkerKycService.updateApplicationStatusByUid(uid, status);
            return ResponseEntity.ok(ApiResponse.success("Application status updated successfully.", updatedKyc));
        } catch (ValidationException ve) {
            logger.warn("KYC not found for status update with uid: {}", uid);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound(ve.getMessage()));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid status value: {}", status);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid status value", 400));
        } catch (Exception ex) {
            logger.error("Error updating status for KYC with uid: {}", uid, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.serverError("Failed to update application status."));
        }
    }

    // ===================== UPDATE APPLICATION STATUS BY ID =====================
    /**
     * Response Scenarios:
     * 1. Success (200): Returns updated KYC details
     * 2. Not Found (404): KYC with given id doesn't exist
     * 3. Error (400): Invalid status value
     * 4. Error (500): Update failed
     */
    @PatchMapping("/id/{id}/status")
    public ResponseEntity<ApiResponse<WalkerKycRequestDto>> updateApplicationStatusById(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status) {
        try {
            logger.info("Updating status for KYC with id: {} to {}", id, status);
            WalkerKycRequestDto updatedKyc = walkerKycService.updateApplicationStatusById(id, status);
            return ResponseEntity.ok(ApiResponse.success("Application status updated successfully.", updatedKyc));
        } catch (ValidationException ve) {
            logger.warn("KYC not found for status update with id: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound(ve.getMessage()));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid status value: {}", status);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid status value", 400));
        } catch (Exception ex) {
            logger.error("Error updating status for KYC with id: {}", id, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.serverError("Failed to update application status."));
        }
    }
    
    
    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<Page<GetAllWalkerResponse>>> getNearbyWalkers(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "10.0") Double maxDistance,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String serviceArea,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            logger.info("Fetching nearby walkers for location: {}, {}", latitude, longitude);
            
            Page<GetAllWalkerResponse> walkers = walkerKycService.getAllWalkers(
                    latitude, longitude, maxDistance, searchTerm, serviceArea, page, size);

            logger.info("Found {} walkers within {} km", walkers.getTotalElements(), maxDistance);
            
            return ResponseEntity.ok(ApiResponse.success("Walkers retrieved successfully", walkers));
            
        } catch (Exception e) {
            logger.error("Error fetching nearby walkers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.serverError("Error: " + e.getMessage()));
        }
    }
    
    
}