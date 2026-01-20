package com.example.demo.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

@Entity
@Table(name = "metavet_to_walker_kyc")
public class WalkerKyc extends BaseEntity {
	
	

	@OneToOne
	@JoinColumn(referencedColumnName = "uid", name = "user_uid")
	UsersEntity user;

	@OneToOne
	@JoinColumn(referencedColumnName = "uid", name = "service_provider_uid")
	ServiceProvider serviceProvider;
	
    
    // Personal & Business Information
    @Column(name = "full_legal_name", nullable = false)
    private String fullLegalName;
    
    @Column(name = "business_name")
    private String businessName;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(name = "phone", nullable = false)
    private String phone;
    
    @Column(name = "address", nullable = false, length = 500)
    private String address;
    
    @Pattern(regexp = "^(-?(?:[0-8]?[0-9]|90)(?:\\.[0-9]+)?)$", message = "Invalid latitude format")
	@Column(name = "latitude")
	private String latitude;

	// Longitude: -180 to +180
	@Pattern(regexp = "^(-?(?:1[0-7][0-9]|[0-9]?[0-9]|180)(?:\\.[0-9]+)?)$", message = "Invalid longitude format")
	@Column(name = "longitude")
	private String longitude;

	
    
    @Column(name = "service_area", nullable = false)
    private String serviceArea;
    
    @Column(name = "years_experience")
    private Integer yearsExperience;
    
    // Professional Credentials
    @Column(name = "has_pet_care_certifications")
    private Boolean hasPetCareCertifications = false;
    
    @Column(name = "pet_care_certification_details", length = 1000)
    private String petCareCertificationsDetails;
    
    @Column(name = "pet_care_certificate", columnDefinition = "BYTEA")
    private byte[] petCareCertificationDoc;
    
    @Column(name = "certification_file_path")
    private String certificationFilePath;
    
    @Column(name = "bonded_or_insured")
    private Boolean bondedOrInsured = false;
    
    @Column(name = "bonded_or_insured_doc", columnDefinition = "BYTEA")
    private byte[] bondedOrInsuredDoc;
    
    @Column(name = "bonded_file_path")
    private String bondedFilePath;
    
    @Column(name = "has_first_aid")
    private Boolean hasFirstAid = false;
    
    @Column(name = "pet_first_aid_certificate_doc", columnDefinition = "BYTEA")
    private byte[] petFirstAidCertificateDoc;
    
    @Column(name = "first_aid_file_path")
    private String firstAidFilePath;
    
    @Column(name = "criminal_check")
    private Boolean criminalCheck = false;
    
    @Column(name = "criminal_record_doc", columnDefinition = "BYTEA")
    private byte[] criminalRecordDoc;
    
    @Column(name = "criminal_check_file_path")
    private String criminalCheckFilePath;
    
    // Liability & Compliance
    @Column(name = "liability_insurance")
    private Boolean liabilityInsurance = false;
    
    @Column(name = "liability_provider")
    private String liabilityProvider;
    
    @Column(name = "liability_policy_number")
    private String liabilityPolicyNumber;
    
    @Column(name = "insurance_expiry")
    private LocalDate insuranceExpiry;
    
    @Column(name = "liability_insurance_doc", columnDefinition = "BYTEA")
    private byte[] liabilityInsuranceDoc;
    
    @Column(name = "liability_file_path")
    private String liabilityFilePath;
    
    @Column(name = "has_business_license")
    private Boolean hasBusinessLicense = false;
    
    @Column(name = "business_license_doc", columnDefinition = "BYTEA")
    private byte[] businessLicenseDoc;
    
    @Column(name = "business_license_file_path")
    private String businessLicenseFilePath;
    
    // Availability & Operations
    @Column(name = "walk_radius")
    private String walkRadius;
    
    @Column(name = "max_pets_per_walk")
    private Integer maxPetsPerWalk;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_communication")
    private PreferredCommunication preferredCommunication;
    
    // Declarations
    @Column(name = "declaration_accurate", nullable = false)
    private Boolean declarationAccurate = false;
    
    @Column(name = "declaration_verify_ok", nullable = false)
    private Boolean declarationVerifyOk = false;
    
    @Column(name = "declaration_comply", nullable = false)
    private Boolean declarationComply = false;
    
    @Column(name = "signature")
    private String signature;
    
    @Column(name = "signature_date")
    private LocalDate signatureDate;
    
    // Application Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;
    
    // Enums
    public enum ApplicationStatus {
        PENDING, APPROVED, REJECTED, UNDER_REVIEW
    }
    
    public enum PreferredCommunication {
        IN_APP, TEXT, CALL
    }
    
    // Getters and Setters
    public String getFullLegalName() {
        return fullLegalName;
    }

    public void setFullLegalName(String fullLegalName) {
        this.fullLegalName = fullLegalName;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getServiceArea() {
        return serviceArea;
    }

    public void setServiceArea(String serviceArea) {
        this.serviceArea = serviceArea;
    }

    public Integer getYearsExperience() {
        return yearsExperience;
    }

    public void setYearsExperience(Integer yearsExperience) {
        this.yearsExperience = yearsExperience;
    }

    public Boolean getHasPetCareCertifications() {
        return hasPetCareCertifications;
    }

    public void setHasPetCareCertifications(Boolean hasPetCareCertifications) {
        this.hasPetCareCertifications = hasPetCareCertifications;
    }

    public String getPetCareCertificationsDetails() {
        return petCareCertificationsDetails;
    }

    public void setPetCareCertificationsDetails(String petCareCertificationsDetails) {
        this.petCareCertificationsDetails = petCareCertificationsDetails;
    }

    public byte[] getPetCareCertificationDoc() {
        return petCareCertificationDoc;
    }

    public void setPetCareCertificationDoc(byte[] petCareCertificationDoc) {
        this.petCareCertificationDoc = petCareCertificationDoc;
    }

    public String getCertificationFilePath() {
        return certificationFilePath;
    }

    public void setCertificationFilePath(String certificationFilePath) {
        this.certificationFilePath = certificationFilePath;
    }

    public Boolean getBondedOrInsured() {
        return bondedOrInsured;
    }

    public void setBondedOrInsured(Boolean bondedOrInsured) {
        this.bondedOrInsured = bondedOrInsured;
    }

    public byte[] getBondedOrInsuredDoc() {
        return bondedOrInsuredDoc;
    }

    public void setBondedOrInsuredDoc(byte[] bondedOrInsuredDoc) {
        this.bondedOrInsuredDoc = bondedOrInsuredDoc;
    }

    public String getBondedFilePath() {
        return bondedFilePath;
    }

    public void setBondedFilePath(String bondedFilePath) {
        this.bondedFilePath = bondedFilePath;
    }

    public Boolean getHasFirstAid() {
        return hasFirstAid;
    }

    public void setHasFirstAid(Boolean hasFirstAid) {
        this.hasFirstAid = hasFirstAid;
    }

    public byte[] getPetFirstAidCertificateDoc() {
        return petFirstAidCertificateDoc;
    }

    public void setPetFirstAidCertificateDoc(byte[] petFirstAidCertificateDoc) {
        this.petFirstAidCertificateDoc = petFirstAidCertificateDoc;
    }

    public String getFirstAidFilePath() {
        return firstAidFilePath;
    }

    public void setFirstAidFilePath(String firstAidFilePath) {
        this.firstAidFilePath = firstAidFilePath;
    }

    public Boolean getCriminalCheck() {
        return criminalCheck;
    }

    public void setCriminalCheck(Boolean criminalCheck) {
        this.criminalCheck = criminalCheck;
    }

    public byte[] getCriminalRecordDoc() {
        return criminalRecordDoc;
    }

    public void setCriminalRecordDoc(byte[] criminalRecordDoc) {
        this.criminalRecordDoc = criminalRecordDoc;
    }

    public String getCriminalCheckFilePath() {
        return criminalCheckFilePath;
    }

    public void setCriminalCheckFilePath(String criminalCheckFilePath) {
        this.criminalCheckFilePath = criminalCheckFilePath;
    }

    public Boolean getLiabilityInsurance() {
        return liabilityInsurance;
    }

    public void setLiabilityInsurance(Boolean liabilityInsurance) {
        this.liabilityInsurance = liabilityInsurance;
    }

    public String getLiabilityProvider() {
        return liabilityProvider;
    }

    public void setLiabilityProvider(String liabilityProvider) {
        this.liabilityProvider = liabilityProvider;
    }

    public String getLiabilityPolicyNumber() {
        return liabilityPolicyNumber;
    }

    public void setLiabilityPolicyNumber(String liabilityPolicyNumber) {
        this.liabilityPolicyNumber = liabilityPolicyNumber;
    }

    public LocalDate getInsuranceExpiry() {
        return insuranceExpiry;
    }

    public void setInsuranceExpiry(LocalDate insuranceExpiry) {
        this.insuranceExpiry = insuranceExpiry;
    }

    public byte[] getLiabilityInsuranceDoc() {
        return liabilityInsuranceDoc;
    }

    public void setLiabilityInsuranceDoc(byte[] liabilityInsuranceDoc) {
        this.liabilityInsuranceDoc = liabilityInsuranceDoc;
    }

    public String getLiabilityFilePath() {
        return liabilityFilePath;
    }

    public void setLiabilityFilePath(String liabilityFilePath) {
        this.liabilityFilePath = liabilityFilePath;
    }

    public Boolean getHasBusinessLicense() {
        return hasBusinessLicense;
    }

    public void setHasBusinessLicense(Boolean hasBusinessLicense) {
        this.hasBusinessLicense = hasBusinessLicense;
    }

    public byte[] getBusinessLicenseDoc() {
        return businessLicenseDoc;
    }

    public void setBusinessLicenseDoc(byte[] businessLicenseDoc) {
        this.businessLicenseDoc = businessLicenseDoc;
    }

    public String getBusinessLicenseFilePath() {
        return businessLicenseFilePath;
    }

    public void setBusinessLicenseFilePath(String businessLicenseFilePath) {
        this.businessLicenseFilePath = businessLicenseFilePath;
    }

    public String getWalkRadius() {
        return walkRadius;
    }

    public void setWalkRadius(String walkRadius) {
        this.walkRadius = walkRadius;
    }

    public Integer getMaxPetsPerWalk() {
        return maxPetsPerWalk;
    }

    public void setMaxPetsPerWalk(Integer maxPetsPerWalk) {
        this.maxPetsPerWalk = maxPetsPerWalk;
    }

    public PreferredCommunication getPreferredCommunication() {
        return preferredCommunication;
    }

    public void setPreferredCommunication(PreferredCommunication preferredCommunication) {
        this.preferredCommunication = preferredCommunication;
    }

    public Boolean getDeclarationAccurate() {
        return declarationAccurate;
    }

    public void setDeclarationAccurate(Boolean declarationAccurate) {
        this.declarationAccurate = declarationAccurate;
    }

    public Boolean getDeclarationVerifyOk() {
        return declarationVerifyOk;
    }

    public void setDeclarationVerifyOk(Boolean declarationVerifyOk) {
        this.declarationVerifyOk = declarationVerifyOk;
    }

    public Boolean getDeclarationComply() {
        return declarationComply;
    }

    public void setDeclarationComply(Boolean declarationComply) {
        this.declarationComply = declarationComply;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public LocalDate getSignatureDate() {
        return signatureDate;
    }

    public void setSignatureDate(LocalDate signatureDate) {
        this.signatureDate = signatureDate;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public UsersEntity getUser() {
		return user;
	}

	public void setUser(UsersEntity user) {
		this.user = user;
	}

	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}
    
    
}