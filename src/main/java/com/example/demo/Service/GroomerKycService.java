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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Config.SpringSecurityAuditorAware;
import com.example.demo.Dto.ApiResponse;
import com.example.demo.Dto.GroomerKycRequestDto;
import com.example.demo.Entities.GroomerKyc;
import com.example.demo.Entities.UsersEntity;
import com.example.demo.Entities.GroomerKyc.ApplicationStatus;
import com.example.demo.Entities.GroomerKyc.ServiceOffered;
import com.example.demo.Entities.ServiceProvider;
import com.example.demo.Repository.GroomerKycRepo;
import com.example.demo.Repository.ServiceProviderRepo;

import jakarta.validation.ValidationException;

@Service
public class GroomerKycService {

	@Autowired
	private GroomerKycRepo groomerKycRepository;
	
	@Autowired
    private SpringSecurityAuditorAware auditorAware;
    
	@Autowired
	private ServiceProviderRepo serviceProviderRepository ;
	
	
	
	
	

	private static final String DOCUMENT_ROOT = System.getProperty("user.dir");
	private static final String QrFolder = "groomer_kyc";
	private static final String Qr_FILE_DIR = DOCUMENT_ROOT + File.separator + QrFolder + File.separator;
	private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("pdf", "jpeg", "jpg", "png", "doc", "docx");

	// ===================== CREATE METHOD =====================
	public GroomerKycRequestDto createGroomerKyc(GroomerKycRequestDto dto, List<String> documentNames,
			List<MultipartFile> documentFiles) throws IOException, ValidationException {

		// Check if email already exists for new registration
		if (groomerKycRepository.existsByEmail(dto.getEmail())) {
			throw new ValidationException("Email already exists. Cannot create duplicate KYC application.");
		}

		GroomerKyc kyc = new GroomerKyc();

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
		kyc.setServiceLocationType(dto.getServiceLocationType());
		kyc.setYearsExperience(dto.getYearsExperience());

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

		// Grooming Certificate handling
		if (dto.getHasGroomingCert() != null && dto.getHasGroomingCert().equals(Boolean.TRUE)) {
			kyc.setHasGroomingCert(true);
			if (dto.getGroomingCertificateDoc() != null && !dto.getGroomingCertificateDoc().isEmpty()) {
				byte[] fileBytes = dto.getGroomingCertificateDoc().getBytes();
				String path = saveFile(dto.getGroomingCertificateDoc(), "grooming_certificate", dto.getEmail());
				kyc.setGroomingCertDetails(dto.getGroomingCertDetails());
				kyc.setGroomingCertificateDoc(fileBytes);
				kyc.setGroomingCertificateDocPath(path);
			} else {
				throw new ValidationException("Grooming Certificate document is required when 'Has Grooming Certificate' is true.");
			}
		} else {
			kyc.setHasGroomingCert(false);
		}

		// First Aid Certificate handling
		if (dto.getHasFirstAidCert() != null && dto.getHasFirstAidCert().equals(Boolean.TRUE)) {
			kyc.setHasFirstAidCert(true);
			if (dto.getFirstAidCertificateDoc() != null && !dto.getFirstAidCertificateDoc().isEmpty()) {
				byte[] fileBytes = dto.getFirstAidCertificateDoc().getBytes();
				String path = saveFile(dto.getFirstAidCertificateDoc(), "first_aid_certificate", dto.getEmail());
				kyc.setFirstAidCertificateDoc(fileBytes);
				kyc.setFirstAidCertificatePath(path);
			} else {
				throw new ValidationException("First Aid Certificate document is required when 'Has First Aid Certificate' is true.");
			}
		} else {
			kyc.setHasFirstAidCert(false);
		}

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
				kyc.setInsuaranceDoccPath(path);
			} else {
				throw new ValidationException("Insurance document is required when 'Has Insurance' is true.");
			}
		} else {
			kyc.setHasInsurance(false);
		}

		// Criminal Check handling
		if (dto.getCriminalCheck() != null && dto.getCriminalCheck().equals(Boolean.TRUE)) {
			kyc.setCriminalCheck(true);
			if (dto.getCrimialRecordDoc() != null && !dto.getCrimialRecordDoc().isEmpty()) {
				byte[] fileBytes = dto.getCrimialRecordDoc().getBytes();
				String path = saveFile(dto.getCrimialRecordDoc(), "criminal_record", dto.getEmail());
				kyc.setCrimialRecordDoc(fileBytes);
				kyc.setCriminalDocPath(path);
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

			if (dto.getLiabilityExpiry() == null) {
				throw new ValidationException("Liability Expiry Date is required when 'Liability Insurance' is true.");
			}
			kyc.setLiabilityExpiry(dto.getLiabilityExpiry());

			if (dto.getLiabilityInsuaranceDoc() != null && !dto.getLiabilityInsuaranceDoc().isEmpty()) {
				byte[] fileBytes = dto.getLiabilityInsuaranceDoc().getBytes();
				String path = saveFile(dto.getLiabilityInsuaranceDoc(), "liability_insurance", dto.getEmail());
				kyc.setLiabilityInsuaranceDoc(fileBytes);
				kyc.setLiabilityDocPath(path);
			} else {
				throw new ValidationException("Liability Insurance document is required when 'Liability Insurance' is true.");
			}
		} else {
			kyc.setLiabilityInsurance(false);
		}

		// Incident Policy handling
		if (dto.getHasIncidentPolicy() != null && dto.getHasIncidentPolicy().equals(Boolean.TRUE)) {
			kyc.setHasIncidentPolicy(true);
			if (dto.getIncidentPolicyDetails() == null || dto.getIncidentPolicyDetails().trim().isEmpty()) {
				throw new ValidationException("Incident Policy Details are required when 'Has Incident Policy' is true.");
			}
			kyc.setIncidentPolicyDetails(dto.getIncidentPolicyDetails());
		} else {
			kyc.setHasIncidentPolicy(false);
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

		// Optional service details
		kyc.setServicesPrices(dto.getServicesPrices());
		kyc.setAverageAppointmentDuration(dto.getAverageAppointmentDuration());
		kyc.setServiceRadius(dto.getServiceRadius());

		// Declaration validations (Required)
		if (dto.getDeclarationAccuracy() == null || !dto.getDeclarationAccuracy().equals(Boolean.TRUE)) {
			throw new ValidationException("Declaration of Accuracy is required and must be true.");
		}
		kyc.setDeclarationAccuracy(dto.getDeclarationAccuracy());

		if (dto.getDeclarationConsentVerify() == null || !dto.getDeclarationConsentVerify().equals(Boolean.TRUE)) {
			throw new ValidationException("Declaration of Consent to Verify is required and must be true.");
		}
		kyc.setDeclarationConsentVerify(dto.getDeclarationConsentVerify());

		if (dto.getDeclarationComply() == null || !dto.getDeclarationComply().equals(Boolean.TRUE)) {
			throw new ValidationException("Declaration to Comply is required and must be true.");
		}
		kyc.setDeclarationComply(dto.getDeclarationComply());

		// Signature details
		kyc.setSignature(dto.getSignature());
		kyc.setSignatureDate(dto.getSignatureDate());

		// Set default status
		kyc.setStatus(ApplicationStatus.PENDING);

		
		
		
		
		// ----------- SERVICE PROVIDER VALIDATION (FIXED) ------------

		UsersEntity owner = auditorAware.getCurrentAuditor().orElse(null);

		// Step 1: check logged-in user
		if (owner == null) {
		    throw new ValidationException("❌ No logged-in user found");
		}

		// Step 2: check owner UID
		if (owner.getUid() == null) {
		    throw new ValidationException("❌ Logged-in user does not have a UID");
		}

		// Step 3: fetch service provider record
		ServiceProvider serviceProvider = serviceProviderRepository.findByOwnerUid(owner.getUid());

	
		// Step 4: check record found
		if (serviceProvider == null) {
		    throw new ValidationException("❌ You are not Pet Groomer");
		}

		// Step 5: check correct service type (Pet Groomer only)
		if (serviceProvider.getServiceType() != ServiceProvider.ServiceType.Pet_Groomer) {
		    throw new ValidationException("❌ Only Pet Groomer can fill this KYC");
		}
		
		kyc.setUser(owner);
		
		System.out.println("Service type validated: " + serviceProvider.getServiceType());

		// associate service provider with KYC
		kyc.setServiceProvider(serviceProvider);

		
	
		
		
		
		
		
		
		
		
		
		
		
		// Save and return
		GroomerKyc savedKyc = groomerKycRepository.save(kyc);
		return copyToDto(savedKyc);
	}

	// ===================== GET ALL METHOD =====================
	public List<GroomerKycRequestDto> getAll() {
		List<GroomerKyc> allDocuments = groomerKycRepository.findAllByOrderByCreatedAtDesc();
		
		
	
		return allDocuments.stream().map(this::copyToDto).collect(Collectors.toList());
	}

	// ===================== GET BY UID METHOD =====================
	public GroomerKycRequestDto getGroomerKycByUid(UUID uid) throws ValidationException {
		GroomerKyc kyc = groomerKycRepository.findByUid(uid)
				.orElseThrow(() -> new ValidationException("Groomer KYC not found with uid: " + uid));
		return copyToDto(kyc);
	}

	// ===================== GET BY ID METHOD =====================
	public GroomerKycRequestDto getGroomerKycById(Long id) throws ValidationException {
		GroomerKyc kyc = groomerKycRepository.findById(id)
				.orElseThrow(() -> new ValidationException("Groomer KYC not found with id: " + id));
		return copyToDto(kyc);
	}
	
	// ===================== GET BY EMAIL METHOD =====================
	public GroomerKycRequestDto getGroomerKycByEmail(String email) throws ValidationException {
		GroomerKyc kyc = groomerKycRepository.findByEmail(email)
				.orElseThrow(() -> new ValidationException("Groomer KYC not found with email: " + email));
		return copyToDto(kyc);
	}
	
	// ===================== SEARCH BY NAME OR BUSINESS =====================
	public List<GroomerKycRequestDto> searchByNameOrBusiness(String query) {
		if (query == null || query.trim().isEmpty()) {
			return getAll();
		}
		List<GroomerKyc> results = groomerKycRepository.searchByNameOrBusiness(query.trim());
		return results.stream().map(this::copyToDto).collect(Collectors.toList());
	}
	
	// ===================== GET BY STATUS =====================
	public List<GroomerKycRequestDto> getByStatus(ApplicationStatus status) {
		List<GroomerKyc> results = groomerKycRepository.findByStatusOrderByCreatedAtDesc(status);
		return results.stream().map(this::copyToDto).collect(Collectors.toList());
	}

	// ===================== DELETE BY UID METHOD =====================
	public void deleteGroomerKycByUid(UUID uid) throws ValidationException {
		GroomerKyc kyc = groomerKycRepository.findByUid(uid)
				.orElseThrow(() -> new ValidationException("Groomer KYC not found with uid: " + uid));
		deleteAssociatedFiles(kyc);
		groomerKycRepository.delete(kyc);
	}

	// ===================== DELETE BY ID METHOD =====================
	public void deleteGroomerKycById(Long id) throws ValidationException {
		GroomerKyc kyc = groomerKycRepository.findById(id)
				.orElseThrow(() -> new ValidationException("Groomer KYC not found with id: " + id));
		deleteAssociatedFiles(kyc);
		groomerKycRepository.delete(kyc);
	}

	// ===================== GET STATUS BY UID METHOD =====================
	public ApplicationStatus getStatusByUid(UUID uid) throws ValidationException {
		GroomerKyc kyc = groomerKycRepository.findByUid(uid)
				.orElseThrow(() -> new ValidationException("Groomer KYC not found with uid: " + uid));
		return kyc.getStatus();
	}

	// ===================== GET STATUS BY ID METHOD =====================
	public ApplicationStatus getStatusById(Long id) throws ValidationException {
		GroomerKyc kyc = groomerKycRepository.findById(id)
				.orElseThrow(() -> new ValidationException("Groomer KYC not found with id: " + id));
		return kyc.getStatus();
	}

	// ===================== UPDATE STATUS BY UID METHOD =====================
	public GroomerKycRequestDto updateApplicationStatusByUid(UUID uid, ApplicationStatus status)
			throws ValidationException {
		GroomerKyc kyc = groomerKycRepository.findByUid(uid)
				.orElseThrow(() -> new ValidationException("Groomer KYC not found with uid: " + uid));
		kyc.setStatus(status);
		GroomerKyc updatedKyc = groomerKycRepository.save(kyc);
		return copyToDto(updatedKyc);
	}

	// ===================== UPDATE STATUS BY ID METHOD =====================
	public GroomerKycRequestDto updateApplicationStatusById(Long id, ApplicationStatus status)
			throws ValidationException {
		GroomerKyc kyc = groomerKycRepository.findById(id)
				.orElseThrow(() -> new ValidationException("Groomer KYC not found with id: " + id));
		kyc.setStatus(status);
		GroomerKyc updatedKyc = groomerKycRepository.save(kyc);
		return copyToDto(updatedKyc);
	}
	
	// ===================== GET ENTITY BY UID (FOR CONTROLLER) =====================
	public GroomerKyc getEntityByUid(UUID uid) throws ValidationException {
		return groomerKycRepository.findByUid(uid)
				.orElseThrow(() -> new ValidationException("Groomer KYC not found with uid: " + uid));
	}

	// ===================== HELPER METHODS =====================

	private GroomerKycRequestDto copyToDto(GroomerKyc entity) {
		GroomerKycRequestDto dto = new GroomerKycRequestDto();
		BeanUtils.copyProperties(entity, dto);

		dto.setId(entity.getId());
		dto.setUid(entity.getUid());
		dto.setCreatedAt(entity.getCreatedAt());
		dto.setUpdatedAt(entity.getUpdatedAt());

		String base = "/groomerkyc/uploaded_files/" + entity.getUid();

		if (entity.getBusinessLicenseFilePath() != null && !entity.getBusinessLicenseFilePath().isBlank()) {
			dto.setBusinessLicenseFilePath(entity.getBusinessLicenseFilePath());
			dto.setBusinessLicenseFileURL(base + "/business_license");
		}
		if (entity.getGroomingCertificateDocPath() != null && !entity.getGroomingCertificateDocPath().isBlank()) {
			dto.setGroomingCertificateDocPath(entity.getGroomingCertificateDocPath());
			dto.setGroomingCertificateDocURL(base + "/grooming_certificate");
		}
		if (entity.getFirstAidCertificatePath() != null && !entity.getFirstAidCertificatePath().isBlank()) {
			dto.setFirstAidCertificatePath(entity.getFirstAidCertificatePath());
			dto.setFirstAidCertificateURL(base + "/first_aid_certificate");
		}
		if (entity.getInsuaranceDoccPath() != null && !entity.getInsuaranceDoccPath().isBlank()) {
			dto.setInsuaranceDoccPath(entity.getInsuaranceDoccPath());
			dto.setInsuaranceDoccURL(base + "/insurance");
		}
		if (entity.getCriminalDocPath() != null && !entity.getCriminalDocPath().isBlank()) {
			dto.setCriminalDocPath(entity.getCriminalDocPath());
			dto.setCriminalDocURL(base + "/criminal_record");
		}
		if (entity.getLiabilityDocPath() != null && !entity.getLiabilityDocPath().isBlank()) {
			dto.setLiabilityDocPath(entity.getLiabilityDocPath());
			dto.setLiabilityDocURL(base + "/liability_insurance");
		}

		return dto;
	}

	private void deleteAssociatedFiles(GroomerKyc kyc) {
		deleteFileIfExists(kyc.getBusinessLicenseFilePath());
		deleteFileIfExists(kyc.getGroomingCertificateDocPath());
		deleteFileIfExists(kyc.getFirstAidCertificatePath());
		deleteFileIfExists(kyc.getInsuaranceDoccPath());
		deleteFileIfExists(kyc.getCriminalDocPath());
		deleteFileIfExists(kyc.getLiabilityDocPath());
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
	
	public ResponseEntity<ApiResponse<?>>statusCheck() {
		UsersEntity owner = auditorAware.getCurrentAuditor().orElse(null);

	    // check login
	    if (owner == null) {
	        return ResponseEntity
	                .status(401)
	                .body(ApiResponse.error("Unauthorized user"));
	    }
	    
		GroomerKyc kyc = groomerKycRepository.findByUserUid(owner.getUid());
		
		
		if (kyc == null) {
	        return ResponseEntity
	                .status(404)
	                .body(ApiResponse.error("KYC is not completed"));
	    }

	    // ✔ KYC exists → send data
	    return ResponseEntity
	            .status(200)
	            .body(ApiResponse.success("KYC fetched successfully"));
	
	}
	
	
	
	
}