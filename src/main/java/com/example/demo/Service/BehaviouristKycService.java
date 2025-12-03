package com.example.demo.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Dto.BehaviouristKycRequestDto;
import com.example.demo.Entities.BehaviouristKyc;
import com.example.demo.Entities.BehaviouristKyc.ApprovalStatus;
import com.example.demo.Entities.BehaviouristKyc.ServiceOffered;
import com.example.demo.Entities.BehaviouristKyc.Specialization;
import com.example.demo.Repository.BehaviouristKycRepo;

import jakarta.validation.ValidationException;

@Service
public class BehaviouristKycService {

    @Autowired
    private BehaviouristKycRepo behaviouristKycRepo;

    private static final String DOCUMENT_ROOT = System.getProperty("user.dir");
    private static final String QrFolder = "behaviourist_kyc";
    private static final String Qr_FILE_DIR = DOCUMENT_ROOT + File.separator + QrFolder + File.separator;
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("pdf", "jpeg", "jpg", "png", "doc", "docx");

    // ===================== CREATE METHOD =====================
    public BehaviouristKycRequestDto createBehaviouristKyc(BehaviouristKycRequestDto dto) 
            throws IOException, ValidationException {

        // Check if email already exists for new registration
        if (behaviouristKycRepo.existsByEmail(dto.getEmail())) {
            throw new ValidationException("Email already exists. Cannot create duplicate KYC application.");
        }

        BehaviouristKyc kyc = new BehaviouristKyc();

        // Required field validations
        if (dto.getFullLegalName() == null || dto.getFullLegalName().trim().isEmpty()) {
            throw new ValidationException("Full Legal Name is required.");
        }
        kyc.setFullLegalName(dto.getFullLegalName());

        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email is required.");
        }
        kyc.setEmail(dto.getEmail());

        // Optional business details
        kyc.setBusinessName(dto.getBusinessName());
        kyc.setPhone(dto.getPhone());
        kyc.setAddress(dto.getAddress());
        kyc.setServiceArea(dto.getServiceArea());
        kyc.setYearsExperience(dto.getYearsExperience());

        // Behavioural Certifications handling
        if (dto.getHasBehaviouralCertifications() != null && dto.getHasBehaviouralCertifications().equals(Boolean.TRUE)) {
            kyc.setHasBehaviouralCertifications(true);
            if (dto.getBehaviouralCertificateDoc() != null && !dto.getBehaviouralCertificateDoc().isEmpty()) {
                byte[] fileBytes = dto.getBehaviouralCertificateDoc().getBytes();
                String path = saveFile(dto.getBehaviouralCertificateDoc(), "behavioural_certificate", dto.getEmail());
                kyc.setBehaviouralCertificateDoc(fileBytes);
                kyc.setBehaviouralCertificateFilePath(path);
                kyc.setBehaviouralCertificateDetails(dto.getBehaviouralCertificateDetails());
            } else {
                throw new ValidationException("Behavioural Certificate document is required when 'Has Behavioural Certifications' is true.");
            }
        } else {
            kyc.setHasBehaviouralCertifications(false);
        }

        // Education Background
        kyc.setEducationBackground(dto.getEducationBackground());

        // Insurance handling
        if (dto.getHasInsurance() != null && dto.getHasInsurance().equals(Boolean.TRUE)) {
            kyc.setHasInsurance(true);
            if (dto.getInsuranceDoc() != null && !dto.getInsuranceDoc().isEmpty()) {
                if (dto.getInsuranceProvider() == null || dto.getInsuranceProvider().trim().isEmpty()) {
                    throw new ValidationException("Insurance Provider is required when 'Has Insurance' is true.");
                }
                if (dto.getInsurancePolicyNumber() == null || dto.getInsurancePolicyNumber().trim().isEmpty()) {
                    throw new ValidationException("Insurance Policy Number is required when 'Has Insurance' is true.");
                }
                if (dto.getInsuranceExpiry() == null) {
                    throw new ValidationException("Insurance Expiry Date is required when 'Has Insurance' is true.");
                }
                
                byte[] fileBytes = dto.getInsuranceDoc().getBytes();
                String path = saveFile(dto.getInsuranceDoc(), "insurance", dto.getEmail());
                kyc.setInsuranceDoc(fileBytes);
                kyc.setInsuranceExpiry(dto.getInsuranceExpiry());
                kyc.setInsurancePolicyNumber(dto.getInsurancePolicyNumber());
                kyc.setInsuranceProvider(dto.getInsuranceProvider());
                kyc.setInsuranceDocPath(path);
            } else {
                throw new ValidationException("Insurance document is required when 'Has Insurance' is true.");
            }
        } else {
            kyc.setHasInsurance(false);
        }

        // Criminal Check handling
        if (dto.getHasCriminalCheck() != null && dto.getHasCriminalCheck().equals(Boolean.TRUE)) {
            kyc.setHasCriminalCheck(true);
            if (dto.getCriminalRecordDoc() != null && !dto.getCriminalRecordDoc().isEmpty()) {
                byte[] fileBytes = dto.getCriminalRecordDoc().getBytes();
                String path = saveFile(dto.getCriminalRecordDoc(), "criminal_record", dto.getEmail());
                kyc.setCriminalRecordDoc(fileBytes);
                kyc.setCriminalDocPath(path);
            } else {
                throw new ValidationException("Criminal Record document is required when 'Criminal Check' is true.");
            }
        } else {
            kyc.setHasCriminalCheck(false);
        }

        // Liability Insurance handling
        if (dto.getLiabilityInsurance() != null && dto.getLiabilityInsurance().equals(Boolean.TRUE)) {
            kyc.setLiabilityInsurance(true);
            if (dto.getLiabilityInsuranceDoc() != null && !dto.getLiabilityInsuranceDoc().isEmpty()) {
                byte[] fileBytes = dto.getLiabilityInsuranceDoc().getBytes();
                String path = saveFile(dto.getLiabilityInsuranceDoc(), "liability_insurance", dto.getEmail());
                kyc.setLiabilityInsuranceDoc(fileBytes);
                kyc.setLiabilityDocPath(path);
            } else {
                throw new ValidationException("Liability Insurance document is required when 'Liability Insurance' is true.");
            }
        } else {
            kyc.setLiabilityInsurance(false);
        }

        // Business License handling
        if (dto.getHasBusinessLicense() != null && dto.getHasBusinessLicense().equals(Boolean.TRUE)) {
            kyc.setHasBusinessLicense(true);
            if (dto.getBusinessLicenseDoc() != null && !dto.getBusinessLicenseDoc().isEmpty()) {
                byte[] fileBytes = dto.getBusinessLicenseDoc().getBytes();
                String path = saveFile(dto.getBusinessLicenseDoc(), "business_license", dto.getEmail());
                kyc.setBusinessLicenseDoc(fileBytes);
                kyc.setBusinessLicenseFilePath(path);
            } else {
                throw new ValidationException("Business License document is required when 'Has Business License' is true.");
            }
        } else {
            kyc.setHasBusinessLicense(false);
        }

        // Services Offered handling
        if (dto.getServicesOffered() != null && !dto.getServicesOffered().isEmpty()) {
            kyc.setServicesOffered(dto.getServicesOffered());
            if (dto.getServicesOffered().contains(ServiceOffered.OTHER)) {
                if (dto.getServicesOtherText() == null || dto.getServicesOtherText().trim().isEmpty()) {
                    throw new ValidationException("Services Other Text is required when 'OTHER' service is selected.");
                }
                kyc.setServicesOtherText(dto.getServicesOtherText());
            }
        }

        // Specializations handling
        if (dto.getSpecializations() != null && !dto.getSpecializations().isEmpty()) {
            kyc.setSpecializations(dto.getSpecializations());
            if (dto.getSpecializations().contains(Specialization.OTHER)) {
                if (dto.getSpecializationOtherText() == null || dto.getSpecializationOtherText().trim().isEmpty()) {
                    throw new ValidationException("Specialization Other Text is required when 'OTHER' specialization is selected.");
                }
                kyc.setSpecializationOtherText(dto.getSpecializationOtherText());
            }
        }

        // Service Radius
        kyc.setServiceRadius(dto.getServiceRadius());

        // Declaration validations (Required)
        if (dto.getInfoTrue() == null || !dto.getInfoTrue().equals(Boolean.TRUE)) {
            throw new ValidationException("Declaration - Information Accuracy is required and must be true.");
        }
        kyc.setInfoTrue(dto.getInfoTrue());

        if (dto.getVerifyOk() == null || !dto.getVerifyOk().equals(Boolean.TRUE)) {
            throw new ValidationException("Declaration - Consent to Verify is required and must be true.");
        }
        kyc.setVerifyOk(dto.getVerifyOk());

        if (dto.getAbideStandards() == null || !dto.getAbideStandards().equals(Boolean.TRUE)) {
            throw new ValidationException("Declaration - Abide by Standards is required and must be true.");
        }
        kyc.setAbideStandards(dto.getAbideStandards());

        // Signature details
        if (dto.getSignature() == null || dto.getSignature().trim().isEmpty()) {
            throw new ValidationException("Signature is required.");
        }
        kyc.setSignature(dto.getSignature());

        if (dto.getSignatureDate() == null) {
            throw new ValidationException("Signature Date is required.");
        }
        kyc.setSignatureDate(dto.getSignatureDate());

        // Set default status
        kyc.setStatus(ApprovalStatus.PENDING);

        // Save and return
        BehaviouristKyc savedKyc = behaviouristKycRepo.save(kyc);
        return copyToDto(savedKyc);
    }

    // ===================== GET ALL METHOD =====================
    public List<BehaviouristKycRequestDto> getAll() {
        List<BehaviouristKyc> allDocuments = behaviouristKycRepo.findAllByOrderByCreatedAtDesc();
        return allDocuments.stream().map(this::copyToDto).collect(Collectors.toList());
    }

    // ===================== GET BY UID METHOD =====================
    public BehaviouristKycRequestDto getBehaviouristKycByUid(UUID uid) throws ValidationException {
        BehaviouristKyc kyc = behaviouristKycRepo.findByUid(uid)
                .orElseThrow(() -> new ValidationException("Behaviourist KYC not found with uid: " + uid));
        return copyToDto(kyc);
    }

    // ===================== GET BY ID METHOD =====================
    public BehaviouristKycRequestDto getBehaviouristKycById(Long id) throws ValidationException {
        BehaviouristKyc kyc = behaviouristKycRepo.findById(id)
                .orElseThrow(() -> new ValidationException("Behaviourist KYC not found with id: " + id));
        return copyToDto(kyc);
    }

    // ===================== GET BY EMAIL METHOD =====================
    public BehaviouristKycRequestDto getBehaviouristKycByEmail(String email) throws ValidationException {
        BehaviouristKyc kyc = behaviouristKycRepo.findByEmail(email)
                .orElseThrow(() -> new ValidationException("Behaviourist KYC not found with email: " + email));
        return copyToDto(kyc);
    }

    // ===================== SEARCH BY NAME OR BUSINESS =====================
    public List<BehaviouristKycRequestDto> searchByNameOrBusiness(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAll();
        }
        List<BehaviouristKyc> results = behaviouristKycRepo.searchByNameOrBusiness(query.trim());
        return results.stream().map(this::copyToDto).collect(Collectors.toList());
    }

    // ===================== GET BY STATUS =====================
    public List<BehaviouristKycRequestDto> getByStatus(ApprovalStatus status) {
        List<BehaviouristKyc> results = behaviouristKycRepo.findByStatusOrderByCreatedAtDesc(status);
        return results.stream().map(this::copyToDto).collect(Collectors.toList());
    }

    // ===================== GET BY SERVICE AREA =====================
    public List<BehaviouristKycRequestDto> getByServiceArea(String area) {
        List<BehaviouristKyc> results = behaviouristKycRepo.findByServiceArea(area);
        return results.stream().map(this::copyToDto).collect(Collectors.toList());
    }

    // ===================== DELETE BY UID METHOD =====================
    public void deleteBehaviouristKycByUid(UUID uid) throws ValidationException {
        BehaviouristKyc kyc = behaviouristKycRepo.findByUid(uid)
                .orElseThrow(() -> new ValidationException("Behaviourist KYC not found with uid: " + uid));
        deleteAssociatedFiles(kyc);
        behaviouristKycRepo.delete(kyc);
    }

    // ===================== DELETE BY ID METHOD =====================
    public void deleteBehaviouristKycById(Long id) throws ValidationException {
        BehaviouristKyc kyc = behaviouristKycRepo.findById(id)
                .orElseThrow(() -> new ValidationException("Behaviourist KYC not found with id: " + id));
        deleteAssociatedFiles(kyc);
        behaviouristKycRepo.delete(kyc);
    }

    // ===================== GET STATUS BY UID METHOD =====================
    public ApprovalStatus getStatusByUid(UUID uid) throws ValidationException {
        BehaviouristKyc kyc = behaviouristKycRepo.findByUid(uid)
                .orElseThrow(() -> new ValidationException("Behaviourist KYC not found with uid: " + uid));
        return kyc.getStatus();
    }

    // ===================== GET STATUS BY ID METHOD =====================
    public ApprovalStatus getStatusById(Long id) throws ValidationException {
        BehaviouristKyc kyc = behaviouristKycRepo.findById(id)
                .orElseThrow(() -> new ValidationException("Behaviourist KYC not found with id: " + id));
        return kyc.getStatus();
    }

    // ===================== UPDATE STATUS BY UID METHOD =====================
    public BehaviouristKycRequestDto updateApplicationStatusByUid(UUID uid, ApprovalStatus status)
            throws ValidationException {
        BehaviouristKyc kyc = behaviouristKycRepo.findByUid(uid)
                .orElseThrow(() -> new ValidationException("Behaviourist KYC not found with uid: " + uid));
        kyc.setStatus(status);
        BehaviouristKyc updatedKyc = behaviouristKycRepo.save(kyc);
        return copyToDto(updatedKyc);
    }

    // ===================== UPDATE STATUS BY ID METHOD =====================
    public BehaviouristKycRequestDto updateApplicationStatusById(Long id, ApprovalStatus status)
            throws ValidationException {
        BehaviouristKyc kyc = behaviouristKycRepo.findById(id)
                .orElseThrow(() -> new ValidationException("Behaviourist KYC not found with id: " + id));
        kyc.setStatus(status);
        BehaviouristKyc updatedKyc = behaviouristKycRepo.save(kyc);
        return copyToDto(updatedKyc);
    }

    // ===================== GET ENTITY BY UID (FOR CONTROLLER) =====================
    public BehaviouristKyc getEntityByUid(UUID uid) throws ValidationException {
        return behaviouristKycRepo.findByUid(uid)
                .orElseThrow(() -> new ValidationException("Behaviourist KYC not found with uid: " + uid));
    }

    // ===================== HELPER METHODS =====================

    private BehaviouristKycRequestDto copyToDto(BehaviouristKyc entity) {
        BehaviouristKycRequestDto dto = new BehaviouristKycRequestDto();
        BeanUtils.copyProperties(entity, dto);

        dto.setId(entity.getId());
        dto.setUid(entity.getUid());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        String base = "/behaviouristkyc/uploaded_files/" + entity.getUid();

        if (entity.getBehaviouralCertificateFilePath() != null && !entity.getBehaviouralCertificateFilePath().isBlank()) {
            dto.setBehaviouralCertificateFilePath(entity.getBehaviouralCertificateFilePath());
            dto.setBehaviouralCertificateFileURL(base + "/behavioural_certificate");
        }
        if (entity.getInsuranceDocPath() != null && !entity.getInsuranceDocPath().isBlank()) {
            dto.setInsuranceDocPath(entity.getInsuranceDocPath());
            dto.setInsuranceDocURL(base + "/insurance");
        }
        if (entity.getCriminalDocPath() != null && !entity.getCriminalDocPath().isBlank()) {
            dto.setCriminalDocPath(entity.getCriminalDocPath());
            dto.setCriminalDocURL(base + "/criminal_record");
        }
        if (entity.getLiabilityDocPath() != null && !entity.getLiabilityDocPath().isBlank()) {
            dto.setLiabilityDocPath(entity.getLiabilityDocPath());
            dto.setLiabilityDocURL(base + "/liability_insurance");
        }
        if (entity.getBusinessLicenseFilePath() != null && !entity.getBusinessLicenseFilePath().isBlank()) {
            dto.setBusinessLicenseFilePath(entity.getBusinessLicenseFilePath());
            dto.setBusinessLicenseFileURL(base + "/business_license");
        }

        return dto;
    }

    private void deleteAssociatedFiles(BehaviouristKyc kyc) {
        deleteFileIfExists(kyc.getBehaviouralCertificateFilePath());
        deleteFileIfExists(kyc.getInsuranceDocPath());
        deleteFileIfExists(kyc.getCriminalDocPath());
        deleteFileIfExists(kyc.getLiabilityDocPath());
        deleteFileIfExists(kyc.getBusinessLicenseFilePath());
    }

    private void deleteFileIfExists(String filePath) {
        if (filePath != null && !filePath.isEmpty()) {
            try {
                Path path = Paths.get(filePath);
                Files.deleteIfExists(path);
            } catch (IOException e) {
                System.err.println("Failed to delete file: " + filePath + " - " + e.getMessage());
            }
        }
    }

    private String saveFile(MultipartFile file, String type, String identifier) throws IOException, ValidationException {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("File cannot be null or empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new ValidationException("Invalid file name");
        }

        String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();

        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new ValidationException("Invalid file type for: " + originalFilename + ". Allowed types: " + String.join(", ", ALLOWED_EXTENSIONS));
        }

        String folderPath = DOCUMENT_ROOT + File.separator + QrFolder + File.separator + identifier + File.separator;
        Path directory = Paths.get(folderPath);
        Files.createDirectories(directory);

        String fileName = type + "_" + System.currentTimeMillis() + "." + ext;
        String filePath = folderPath + fileName;

        File destFile = new File(filePath);
        file.transferTo(destFile);

        return filePath;
    }
}