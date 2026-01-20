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

import com.example.demo.Config.SpringSecurityAuditorAware;
import com.example.demo.Dto.WalkerKycRequestDto;
import com.example.demo.Entities.ServiceProvider;
import com.example.demo.Entities.UsersEntity;
import com.example.demo.Entities.WalkerKyc;
import com.example.demo.Entities.WalkerKyc.ApplicationStatus;
import com.example.demo.Repository.ServiceProviderRepo;
import com.example.demo.Repository.WalkerKycRepo;

import jakarta.validation.ValidationException;

@Service
public class WalkerKycService {

//    private final Service.DoctorDaysService doctorDaysService;

    @Autowired
    private WalkerKycRepo walkerKycRepository;
    
	@Autowired
    private SpringSecurityAuditorAware auditorAware;
	
	@Autowired
	private ServiceProviderRepo serviceProviderRepository;
    
    

    private static final String DOCUMENT_ROOT = System.getProperty("user.dir");
    private static final String QrFolder = "walker_kyc";
    private static final String Qr_FILE_DIR = DOCUMENT_ROOT + File.separator + QrFolder + File.separator;
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("pdf", "jpeg", "jpg", "png", "doc", "docx");

//    WalkerKycService(Service.DoctorDaysService doctorDaysService) {
//        this.doctorDaysService = doctorDaysService;
//    }

    // ===================== CREATE METHOD =====================
    public WalkerKycRequestDto createWalkerKyc(WalkerKycRequestDto dto, List<String> documentNames,
            List<MultipartFile> documentFiles) throws IOException, ValidationException {

        // Check if email already exists for new registration
        if (walkerKycRepository.existsByEmail(dto.getEmail())) {
            throw new ValidationException("Email already exists. Cannot create duplicate KYC application.");
        }
        
        UsersEntity owner = auditorAware.getCurrentAuditor().orElse(null);
        
        ServiceProvider serviceProvider = serviceProviderRepository.findByOwnerUid(owner.getUid());
        if(serviceProvider.getServiceType() != ServiceProvider.ServiceType.Pet_Walker) {
        	
        	throw new ValidationException("Only Pet Walker can do this KYC ");
        	
        }
        
        WalkerKyc kyc = new WalkerKyc();
       
        kyc.setUser(owner);
      kyc.setServiceProvider(serviceProvider);
        
        
        // Required field validations
        if (dto.getFullLegalName() == null || dto.getFullLegalName().trim().isEmpty()) {
            throw new ValidationException("Full Legal Name is required.");
        }
        kyc.setFullLegalName(dto.getFullLegalName());

        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email is required.");
        }
        kyc.setEmail(dto.getEmail());

        if (dto.getPhone() == null || dto.getPhone().trim().isEmpty()) {
            throw new ValidationException("Phone is required.");
        }
        kyc.setPhone(dto.getPhone());

        if (dto.getAddress() == null || dto.getAddress().trim().isEmpty()) {
            throw new ValidationException("Address is required.");
        }
        kyc.setAddress(dto.getAddress());

        if (dto.getServiceArea() == null || dto.getServiceArea().trim().isEmpty()) {
            throw new ValidationException("Service Area is required.");
        }
        
        if(dto.getLatitude() == null || dto.getLongitude() ==null) {
        	throw new ValidationException("Latitude and Longitude are required");
        }
        	kyc.setLatitude(dto.getLatitude());
        	kyc.setLongitude(dto.getLongitude());
        
        kyc.setServiceArea(dto.getServiceArea());

        // Optional business details
        kyc.setBusinessName(dto.getBusinessName());
        kyc.setYearsExperience(dto.getYearsExperience());

        // Pet Care Certifications handling
        if (dto.getHasPetCareCertifications() != null && dto.getHasPetCareCertifications().equals(Boolean.TRUE)) {
            kyc.setHasPetCareCertifications(true);
            kyc.setPetCareCertificationsDetails(dto.getPetCareCertificationsDetails());
            
            if (dto.getPetCareCertificationDoc() != null && !dto.getPetCareCertificationDoc().isEmpty()) {
                byte[] fileBytes = dto.getPetCareCertificationDoc().getBytes();
                String path = saveFile(dto.getPetCareCertificationDoc(), "pet_care_certification", dto.getEmail());
                kyc.setPetCareCertificationDoc(fileBytes);
                kyc.setCertificationFilePath(path);
            } else {
                throw new ValidationException("Pet Care Certification document is required when 'Has Pet Care Certifications' is true.");
            }
        } else {
            kyc.setHasPetCareCertifications(false);
        }

        // Bonded or Insured handling
        if (dto.getBondedOrInsured() != null && dto.getBondedOrInsured().equals(Boolean.TRUE)) {
            kyc.setBondedOrInsured(true);
            if (dto.getBondedOrInsuredDoc() != null && !dto.getBondedOrInsuredDoc().isEmpty()) {
                byte[] fileBytes = dto.getBondedOrInsuredDoc().getBytes();
                String path = saveFile(dto.getBondedOrInsuredDoc(), "bonded_or_insured", dto.getEmail());
                kyc.setBondedOrInsuredDoc(fileBytes);
                kyc.setBondedFilePath(path);
            } else {
                throw new ValidationException("Bonded or Insured document is required when 'Bonded or Insured' is true.");
            }
        } else {
            kyc.setBondedOrInsured(false);
        }

        // Pet First Aid Certificate handling
        if (dto.getHasFirstAid() != null && dto.getHasFirstAid().equals(Boolean.TRUE)) {
            kyc.setHasFirstAid(true);
            if (dto.getPetFirstAidCertificateDoc() != null && !dto.getPetFirstAidCertificateDoc().isEmpty()) {
                byte[] fileBytes = dto.getPetFirstAidCertificateDoc().getBytes();
                String path = saveFile(dto.getPetFirstAidCertificateDoc(), "pet_first_aid_certificate", dto.getEmail());
                kyc.setPetFirstAidCertificateDoc(fileBytes);
                kyc.setFirstAidFilePath(path);
            } else {
                throw new ValidationException("Pet First Aid Certificate document is required when 'Has First Aid' is true.");
            }
        } else {
            kyc.setHasFirstAid(false);
        }

        // Criminal Background Check handling
        if (dto.getCriminalCheck() != null && dto.getCriminalCheck().equals(Boolean.TRUE)) {
            kyc.setCriminalCheck(true);
            if (dto.getCriminalRecordDoc() != null && !dto.getCriminalRecordDoc().isEmpty()) {
                byte[] fileBytes = dto.getCriminalRecordDoc().getBytes();
                String path = saveFile(dto.getCriminalRecordDoc(), "criminal_record", dto.getEmail());
                kyc.setCriminalRecordDoc(fileBytes);
                kyc.setCriminalCheckFilePath(path);
            } else {
                throw new ValidationException("Criminal Record document is required when 'Criminal Check' is true.");
            }
        } else {
            kyc.setCriminalCheck(false);
        }

        // Liability Insurance handling
        if (dto.getLiabilityInsurance() != null && dto.getLiabilityInsurance().equals(Boolean.TRUE)) {
            kyc.setLiabilityInsurance(true);
            
            if (dto.getLiabilityProvider() == null || dto.getLiabilityProvider().trim().isEmpty()) {
                throw new ValidationException("Liability Provider name is required when 'Liability Insurance' is true.");
            }
            kyc.setLiabilityProvider(dto.getLiabilityProvider());

            if (dto.getLiabilityPolicyNumber() == null || dto.getLiabilityPolicyNumber().trim().isEmpty()) {
                throw new ValidationException("Liability Policy Number is required when 'Liability Insurance' is true.");
            }
            kyc.setLiabilityPolicyNumber(dto.getLiabilityPolicyNumber());

            if (dto.getInsuranceExpiry() == null) {
                throw new ValidationException("Insurance Expiry Date is required when 'Liability Insurance' is true.");
            }
            kyc.setInsuranceExpiry(dto.getInsuranceExpiry());

            if (dto.getLiabilityInsuranceDoc() != null && !dto.getLiabilityInsuranceDoc().isEmpty()) {
                byte[] fileBytes = dto.getLiabilityInsuranceDoc().getBytes();
                String path = saveFile(dto.getLiabilityInsuranceDoc(), "liability_insurance", dto.getEmail());
                kyc.setLiabilityInsuranceDoc(fileBytes);
                kyc.setLiabilityFilePath(path);
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

        // Operations details
        kyc.setWalkRadius(dto.getWalkRadius());
        kyc.setMaxPetsPerWalk(dto.getMaxPetsPerWalk());
        kyc.setPreferredCommunication(dto.getPreferredCommunication());

        // Declaration validations (Required)
        if (dto.getDeclarationAccurate() == null || !dto.getDeclarationAccurate().equals(Boolean.TRUE)) {
            throw new ValidationException("Declaration of Accuracy is required and must be true.");
        }
        kyc.setDeclarationAccurate(dto.getDeclarationAccurate());

        if (dto.getDeclarationVerifyOk() == null || !dto.getDeclarationVerifyOk().equals(Boolean.TRUE)) {
            throw new ValidationException("Declaration to Verify is required and must be true.");
        }
        kyc.setDeclarationVerifyOk(dto.getDeclarationVerifyOk());

        if (dto.getDeclarationComply() == null || !dto.getDeclarationComply().equals(Boolean.TRUE)) {
            throw new ValidationException("Declaration to Comply is required and must be true.");
        }
        kyc.setDeclarationComply(dto.getDeclarationComply());

        // Signature details
        kyc.setSignature(dto.getSignature());
        kyc.setSignatureDate(dto.getSignatureDate());

        // Set default status
        kyc.setStatus(ApplicationStatus.PENDING);

        // Save and return
        WalkerKyc savedKyc = walkerKycRepository.save(kyc);
        return copyToDto(savedKyc);
    }

    // ===================== GET ALL METHOD =====================
    public List<WalkerKycRequestDto> getAll() {
        List<WalkerKyc> allDocuments = walkerKycRepository.findAllByOrderByCreatedAtDesc();
        return allDocuments.stream().map(this::copyToDto).collect(Collectors.toList());
    }

    // ===================== GET BY UID METHOD =====================
    public WalkerKycRequestDto getWalkerKycByUid(UUID uid) throws ValidationException {
        WalkerKyc kyc = walkerKycRepository.findByUid(uid)
                .orElseThrow(() -> new ValidationException("Walker KYC not found with uid: " + uid));
        return copyToDto(kyc);
    }

    // ===================== GET BY ID METHOD =====================
    public WalkerKycRequestDto getWalkerKycById(Long id) throws ValidationException {
        WalkerKyc kyc = walkerKycRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Walker KYC not found with id: " + id));
        return copyToDto(kyc);
    }

    // ===================== GET BY EMAIL METHOD =====================
    public WalkerKycRequestDto getWalkerKycByEmail(String email) throws ValidationException {
        WalkerKyc kyc = walkerKycRepository.findByEmail(email)
                .orElseThrow(() -> new ValidationException("Walker KYC not found with email: " + email));
        return copyToDto(kyc);
    }

    // ===================== SEARCH BY NAME OR BUSINESS =====================
    public List<WalkerKycRequestDto> searchByNameOrBusiness(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAll();
        }
        List<WalkerKyc> results = walkerKycRepository.searchByNameOrBusiness(query.trim());
        return results.stream().map(this::copyToDto).collect(Collectors.toList());
    }

    // ===================== GET BY STATUS =====================
    public List<WalkerKycRequestDto> getByStatus(ApplicationStatus status) {
        List<WalkerKyc> results = walkerKycRepository.findByStatusOrderByCreatedAtDesc(status);
        return results.stream().map(this::copyToDto).collect(Collectors.toList());
    }

    // ===================== DELETE BY UID METHOD =====================
    public void deleteWalkerKycByUid(UUID uid) throws ValidationException {
        WalkerKyc kyc = walkerKycRepository.findByUid(uid)
                .orElseThrow(() -> new ValidationException("Walker KYC not found with uid: " + uid));
        deleteAssociatedFiles(kyc);
        walkerKycRepository.delete(kyc);
    }

    // ===================== DELETE BY ID METHOD =====================
    public void deleteWalkerKycById(Long id) throws ValidationException {
        WalkerKyc kyc = walkerKycRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Walker KYC not found with id: " + id));
        deleteAssociatedFiles(kyc);
        walkerKycRepository.delete(kyc);
    }

    // ===================== GET STATUS BY UID METHOD =====================
    public ApplicationStatus getStatusByUid(UUID uid) throws ValidationException {
        WalkerKyc kyc = walkerKycRepository.findByUid(uid)
                .orElseThrow(() -> new ValidationException("Walker KYC not found with uid: " + uid));
        return kyc.getStatus();
    }

    // ===================== GET STATUS BY ID METHOD =====================
    public ApplicationStatus getStatusById(Long id) throws ValidationException {
        WalkerKyc kyc = walkerKycRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Walker KYC not found with id: " + id));
        return kyc.getStatus();
    }

    // ===================== UPDATE STATUS BY UID METHOD =====================
    public WalkerKycRequestDto updateApplicationStatusByUid(UUID uid, ApplicationStatus status)
            throws ValidationException {
        WalkerKyc kyc = walkerKycRepository.findByUid(uid)
                .orElseThrow(() -> new ValidationException("Walker KYC not found with uid: " + uid));
        kyc.setStatus(status);
        WalkerKyc updatedKyc = walkerKycRepository.save(kyc);
        return copyToDto(updatedKyc);
    }

    // ===================== UPDATE STATUS BY ID METHOD =====================
    public WalkerKycRequestDto updateApplicationStatusById(Long id, ApplicationStatus status)
            throws ValidationException {
        WalkerKyc kyc = walkerKycRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Walker KYC not found with id: " + id));
        kyc.setStatus(status);
        WalkerKyc updatedKyc = walkerKycRepository.save(kyc);
        return copyToDto(updatedKyc);
    }

    // ===================== GET ENTITY BY UID (FOR CONTROLLER) =====================
    public WalkerKyc getEntityByUid(UUID uid) throws ValidationException {
        return walkerKycRepository.findByUid(uid)
                .orElseThrow(() -> new ValidationException("Walker KYC not found with uid: " + uid));
    }

    // ===================== HELPER METHODS =====================

    private WalkerKycRequestDto copyToDto(WalkerKyc entity) {
        WalkerKycRequestDto dto = new WalkerKycRequestDto();
        BeanUtils.copyProperties(entity, dto);

        dto.setId(entity.getId());
        dto.setUid(entity.getUid());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        String base = "/walkerkyc/uploaded_files/" + entity.getUid();

        if (entity.getCertificationFilePath() != null && !entity.getCertificationFilePath().isBlank()) {
            dto.setCertificationFilePath(entity.getCertificationFilePath());
            dto.setCertificationFileURL(base + "/pet_care_certification");
        }
        if (entity.getBondedFilePath() != null && !entity.getBondedFilePath().isBlank()) {
            dto.setBondedFilePath(entity.getBondedFilePath());
            dto.setBondedFileURL(base + "/bonded_or_insured");
        }
        if (entity.getFirstAidFilePath() != null && !entity.getFirstAidFilePath().isBlank()) {
            dto.setFirstAidFilePath(entity.getFirstAidFilePath());
            dto.setFirstAidFileURL(base + "/pet_first_aid_certificate");
        }
        if (entity.getCriminalCheckFilePath() != null && !entity.getCriminalCheckFilePath().isBlank()) {
            dto.setCriminalCheckFilePath(entity.getCriminalCheckFilePath());
            dto.setCriminalCheckFileURL(base + "/criminal_record");
        }
        if (entity.getLiabilityFilePath() != null && !entity.getLiabilityFilePath().isBlank()) {
            dto.setLiabilityFilePath(entity.getLiabilityFilePath());
            dto.setLiabilityFileURL(base + "/liability_insurance");
        }
        if (entity.getBusinessLicenseFilePath() != null && !entity.getBusinessLicenseFilePath().isBlank()) {
            dto.setBusinessLicenseFilePath(entity.getBusinessLicenseFilePath());
            dto.setBusinessLicenseFileURL(base + "/business_license");
        }

        return dto;
    }

    private void deleteAssociatedFiles(WalkerKyc kyc) {
        deleteFileIfExists(kyc.getCertificationFilePath());
        deleteFileIfExists(kyc.getBondedFilePath());
        deleteFileIfExists(kyc.getFirstAidFilePath());
        deleteFileIfExists(kyc.getCriminalCheckFilePath());
        deleteFileIfExists(kyc.getLiabilityFilePath());
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