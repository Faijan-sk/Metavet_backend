package com.example.demo.Entities;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

@Entity
public class BehaviouristKyc extends BaseEntity {

    // ---------------- Personal & Business Information ----------------
    @Column(nullable = false)
    private String fullLegalName;
    
    @OneToOne
	@JoinColumn(referencedColumnName = "uid", name = "user_uid")
	UsersEntity user;

	@OneToOne
	@JoinColumn(referencedColumnName = "uid", name = "service_provider_uid")
	ServiceProvider serviceProvider;
	
	

    private String businessName;

    @Column(nullable = false)
    @Email
    private String email;

    private String phone;

    @Column(length = 500)
    private String address;
    
    @Pattern(regexp = "^(-?(?:[0-8]?[0-9]|90)(?:\\.[0-9]+)?)$", message = "Invalid latitude format")
   	@Column(name = "latitude")
   	private String latitude;

   	// Longitude: -180 to +180
   	@Pattern(regexp = "^(-?(?:1[0-7][0-9]|[0-9]?[0-9]|180)(?:\\.[0-9]+)?)$", message = "Invalid longitude format")
   	@Column(name = "longitude")
   	private String longitude;
   	

    private String serviceArea;

    private String yearsExperience;

    // ---------------- Professional Credentials ----------------
    private Boolean hasBehaviouralCertifications = false;

    @Column(name = "behavioural_certificate_doc", columnDefinition = "BYTEA")
    private byte[] behaviouralCertificateDoc;

    @Column(length = 500)
    private String behaviouralCertificateDetails;

    private String behaviouralCertificateFilePath;

    @Column(length = 500)
    private String educationBackground;

    // ---------------- Insurance ----------------
    private Boolean hasInsurance = false;

    private String insuranceProvider;

    private String insurancePolicyNumber;

    private LocalDate insuranceExpiry;

    @Column(name = "insurance_doc", columnDefinition = "BYTEA")
    private byte[] insuranceDoc;

    private String insuranceDocPath;

    // ---------------- Criminal Check ----------------
    private Boolean hasCriminalCheck = false;

    @Column(name = "criminal_record_doc", columnDefinition = "BYTEA")
    private byte[] criminalRecordDoc;

    private String criminalDocPath;

    // ---------------- Liability Insurance ----------------
    private Boolean liabilityInsurance = false;

    @Column(name = "liability_insurance_doc", columnDefinition = "BYTEA")
    private byte[] liabilityInsuranceDoc;

    private String liabilityDocPath;

    // ---------------- Business License ----------------
    private Boolean hasBusinessLicense = false;

    @Column(name = "business_license_doc", columnDefinition = "BYTEA")
    private byte[] businessLicenseDoc;

    private String businessLicenseFilePath;

    // ---------------- Practice Details ----------------
    @Enumerated(EnumType.STRING)
    @Column(name = "services_offered")
    private List<ServiceOffered> servicesOffered;

    private String servicesOtherText;

    @Enumerated(EnumType.STRING)
    @Column(name = "specializations")
    private List<Specialization> specializations;

    private String specializationOtherText;

    @Column(length = 300)
    private String serviceRadius;

    // ---------------- Declarations & Signature ----------------
    @Column(nullable = false)
    private Boolean infoTrue = false;

    @Column(nullable = false)
    private Boolean verifyOk = false;

    @Column(nullable = false)
    private Boolean abideStandards = false;

    private String signature;

    private LocalDate signatureDate;

    // ---------------- Approval Status ----------------
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus status = ApprovalStatus.PENDING;

    // ---------------- Enums ----------------
    public enum ApprovalStatus {
        PENDING, APPROVED, REJECTED, UNDER_REVIEW
    }

    public enum ServiceOffered {
        BEHAVIOURAL_CONSULTATION("BEHAVIOURAL_CONSULTATION"),
        TRAINING("TRAINING"),
        FOLLOW_UP("FOLLOW_UP"),
        VIRTUAL_SESSIONS("VIRTUAL_SESSIONS"),
        OTHER("OTHER");

        private final String label;

        ServiceOffered(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public enum Specialization {
        AGGRESSION("AGGRESSION"),
        SEPARATION_ANXIETY("SEPARATION_ANXIETY"),
        OBEDIENCE("OBEDIENCE"),
        PUPPY_TRAINING("PUPPY_TRAINING"),
        OTHER("OTHER");

        private final String label;

        Specialization(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    // ---------------- Getters & Setters ----------------
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

    public String getYearsExperience() {
        return yearsExperience;
    }

    public void setYearsExperience(String yearsExperience) {
        this.yearsExperience = yearsExperience;
    }

    public Boolean getHasBehaviouralCertifications() {
        return hasBehaviouralCertifications;
    }

    public void setHasBehaviouralCertifications(Boolean hasBehaviouralCertifications) {
        this.hasBehaviouralCertifications = hasBehaviouralCertifications;
    }

    public byte[] getBehaviouralCertificateDoc() {
        return behaviouralCertificateDoc;
    }

    public void setBehaviouralCertificateDoc(byte[] behaviouralCertificateDoc) {
        this.behaviouralCertificateDoc = behaviouralCertificateDoc;
    }

    public String getBehaviouralCertificateDetails() {
        return behaviouralCertificateDetails;
    }

    public void setBehaviouralCertificateDetails(String behaviouralCertificateDetails) {
        this.behaviouralCertificateDetails = behaviouralCertificateDetails;
    }

    public String getBehaviouralCertificateFilePath() {
        return behaviouralCertificateFilePath;
    }

    public void setBehaviouralCertificateFilePath(String behaviouralCertificateFilePath) {
        this.behaviouralCertificateFilePath = behaviouralCertificateFilePath;
    }

    public String getEducationBackground() {
        return educationBackground;
    }

    public void setEducationBackground(String educationBackground) {
        this.educationBackground = educationBackground;
    }

    public Boolean getHasInsurance() {
        return hasInsurance;
    }

    public void setHasInsurance(Boolean hasInsurance) {
        this.hasInsurance = hasInsurance;
    }

    public String getInsuranceProvider() {
        return insuranceProvider;
    }

    public void setInsuranceProvider(String insuranceProvider) {
        this.insuranceProvider = insuranceProvider;
    }

    public String getInsurancePolicyNumber() {
        return insurancePolicyNumber;
    }

    public void setInsurancePolicyNumber(String insurancePolicyNumber) {
        this.insurancePolicyNumber = insurancePolicyNumber;
    }

    public LocalDate getInsuranceExpiry() {
        return insuranceExpiry;
    }

    public void setInsuranceExpiry(LocalDate insuranceExpiry) {
        this.insuranceExpiry = insuranceExpiry;
    }

    public byte[] getInsuranceDoc() {
        return insuranceDoc;
    }

    public void setInsuranceDoc(byte[] insuranceDoc) {
        this.insuranceDoc = insuranceDoc;
    }

    public String getInsuranceDocPath() {
        return insuranceDocPath;
    }

    public void setInsuranceDocPath(String insuranceDocPath) {
        this.insuranceDocPath = insuranceDocPath;
    }

    public Boolean getHasCriminalCheck() {
        return hasCriminalCheck;
    }

    public void setHasCriminalCheck(Boolean hasCriminalCheck) {
        this.hasCriminalCheck = hasCriminalCheck;
    }

    public byte[] getCriminalRecordDoc() {
        return criminalRecordDoc;
    }

    public void setCriminalRecordDoc(byte[] criminalRecordDoc) {
        this.criminalRecordDoc = criminalRecordDoc;
    }

    public String getCriminalDocPath() {
        return criminalDocPath;
    }

    public void setCriminalDocPath(String criminalDocPath) {
        this.criminalDocPath = criminalDocPath;
    }

    public Boolean getLiabilityInsurance() {
        return liabilityInsurance;
    }

    public void setLiabilityInsurance(Boolean liabilityInsurance) {
        this.liabilityInsurance = liabilityInsurance;
    }

    public byte[] getLiabilityInsuranceDoc() {
        return liabilityInsuranceDoc;
    }

    public void setLiabilityInsuranceDoc(byte[] liabilityInsuranceDoc) {
        this.liabilityInsuranceDoc = liabilityInsuranceDoc;
    }

    public String getLiabilityDocPath() {
        return liabilityDocPath;
    }

    public void setLiabilityDocPath(String liabilityDocPath) {
        this.liabilityDocPath = liabilityDocPath;
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

    public List<ServiceOffered> getServicesOffered() {
        return servicesOffered;
    }

    public void setServicesOffered(List<ServiceOffered> servicesOffered) {
        this.servicesOffered = servicesOffered;
    }

    public String getServicesOtherText() {
        return servicesOtherText;
    }

    public void setServicesOtherText(String servicesOtherText) {
        this.servicesOtherText = servicesOtherText;
    }

    public List<Specialization> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(List<Specialization> specializations) {
        this.specializations = specializations;
    }

    public String getSpecializationOtherText() {
        return specializationOtherText;
    }

    public void setSpecializationOtherText(String specializationOtherText) {
        this.specializationOtherText = specializationOtherText;
    }

    public String getServiceRadius() {
        return serviceRadius;
    }

    public void setServiceRadius(String serviceRadius) {
        this.serviceRadius = serviceRadius;
    }

    public Boolean getInfoTrue() {
        return infoTrue;
    }

    public void setInfoTrue(Boolean infoTrue) {
        this.infoTrue = infoTrue;
    }

    public Boolean getVerifyOk() {
        return verifyOk;
    }

    public void setVerifyOk(Boolean verifyOk) {
        this.verifyOk = verifyOk;
    }

    public Boolean getAbideStandards() {
        return abideStandards;
    }

    public void setAbideStandards(Boolean abideStandards) {
        this.abideStandards = abideStandards;
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

    public ApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
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
    
    
}