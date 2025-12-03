package com.example.demo.Dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Entities.WalkerKyc.ApplicationStatus;
import com.example.demo.Entities.WalkerKyc.PreferredCommunication;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class WalkerKycRequestDto {

    // BaseEntity fields
    private Long id;
    private UUID uid;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Personal & Business Information
    private String fullLegalName;
    private String businessName;
    private String email;
    private String phone;
    private String address;
    private String serviceArea;
    private Integer yearsExperience;
    
    // Professional Credentials
    private Boolean hasPetCareCertifications;
    private String petCareCertificationsDetails;
    private MultipartFile petCareCertificationDoc;
    private String certificationFilePath;
    private String certificationFileURL;
    
    private Boolean bondedOrInsured;
    private MultipartFile bondedOrInsuredDoc;
    private String bondedFilePath;
    private String bondedFileURL;
    
    private Boolean hasFirstAid;
    private MultipartFile petFirstAidCertificateDoc;
    private String firstAidFilePath;
    private String firstAidFileURL;
    
    private Boolean criminalCheck;
    private MultipartFile criminalRecordDoc;
    private String criminalCheckFilePath;
    private String criminalCheckFileURL;
    
    // Liability & Compliance
    private Boolean liabilityInsurance;
    private String liabilityProvider;
    private String liabilityPolicyNumber;
    private LocalDate insuranceExpiry;
    private MultipartFile liabilityInsuranceDoc;
    private String liabilityFilePath;
    private String liabilityFileURL;
    
    private Boolean hasBusinessLicense;
    private MultipartFile businessLicenseDoc;
    private String businessLicenseFilePath;
    private String businessLicenseFileURL;
    
    // Availability & Operations
    private String walkRadius;
    private Integer maxPetsPerWalk;
    
    @Enumerated(EnumType.STRING)
    private PreferredCommunication preferredCommunication;
    
    // Declarations
    private Boolean declarationAccurate;
    private Boolean declarationVerifyOk;
    private Boolean declarationComply;
    private String signature;
    private LocalDate signatureDate;
    
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUid() {
        return uid;
    }

    public void setUid(UUID uid) {
        this.uid = uid;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

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

    public MultipartFile getPetCareCertificationDoc() {
        return petCareCertificationDoc;
    }

    public void setPetCareCertificationDoc(MultipartFile petCareCertificationDoc) {
        this.petCareCertificationDoc = petCareCertificationDoc;
    }

    public String getCertificationFilePath() {
        return certificationFilePath;
    }

    public void setCertificationFilePath(String certificationFilePath) {
        this.certificationFilePath = certificationFilePath;
    }

    public String getCertificationFileURL() {
        return certificationFileURL;
    }

    public void setCertificationFileURL(String certificationFileURL) {
        this.certificationFileURL = certificationFileURL;
    }

    public Boolean getBondedOrInsured() {
        return bondedOrInsured;
    }

    public void setBondedOrInsured(Boolean bondedOrInsured) {
        this.bondedOrInsured = bondedOrInsured;
    }

    public MultipartFile getBondedOrInsuredDoc() {
        return bondedOrInsuredDoc;
    }

    public void setBondedOrInsuredDoc(MultipartFile bondedOrInsuredDoc) {
        this.bondedOrInsuredDoc = bondedOrInsuredDoc;
    }

    public String getBondedFilePath() {
        return bondedFilePath;
    }

    public void setBondedFilePath(String bondedFilePath) {
        this.bondedFilePath = bondedFilePath;
    }

    public String getBondedFileURL() {
        return bondedFileURL;
    }

    public void setBondedFileURL(String bondedFileURL) {
        this.bondedFileURL = bondedFileURL;
    }

    public Boolean getHasFirstAid() {
        return hasFirstAid;
    }

    public void setHasFirstAid(Boolean hasFirstAid) {
        this.hasFirstAid = hasFirstAid;
    }

    public MultipartFile getPetFirstAidCertificateDoc() {
        return petFirstAidCertificateDoc;
    }

    public void setPetFirstAidCertificateDoc(MultipartFile petFirstAidCertificateDoc) {
        this.petFirstAidCertificateDoc = petFirstAidCertificateDoc;
    }

    public String getFirstAidFilePath() {
        return firstAidFilePath;
    }

    public void setFirstAidFilePath(String firstAidFilePath) {
        this.firstAidFilePath = firstAidFilePath;
    }

    public String getFirstAidFileURL() {
        return firstAidFileURL;
    }

    public void setFirstAidFileURL(String firstAidFileURL) {
        this.firstAidFileURL = firstAidFileURL;
    }

    public Boolean getCriminalCheck() {
        return criminalCheck;
    }

    public void setCriminalCheck(Boolean criminalCheck) {
        this.criminalCheck = criminalCheck;
    }

    public MultipartFile getCriminalRecordDoc() {
        return criminalRecordDoc;
    }

    public void setCriminalRecordDoc(MultipartFile criminalRecordDoc) {
        this.criminalRecordDoc = criminalRecordDoc;
    }

    public String getCriminalCheckFilePath() {
        return criminalCheckFilePath;
    }

    public void setCriminalCheckFilePath(String criminalCheckFilePath) {
        this.criminalCheckFilePath = criminalCheckFilePath;
    }

    public String getCriminalCheckFileURL() {
        return criminalCheckFileURL;
    }

    public void setCriminalCheckFileURL(String criminalCheckFileURL) {
        this.criminalCheckFileURL = criminalCheckFileURL;
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

    public MultipartFile getLiabilityInsuranceDoc() {
        return liabilityInsuranceDoc;
    }

    public void setLiabilityInsuranceDoc(MultipartFile liabilityInsuranceDoc) {
        this.liabilityInsuranceDoc = liabilityInsuranceDoc;
    }

    public String getLiabilityFilePath() {
        return liabilityFilePath;
    }

    public void setLiabilityFilePath(String liabilityFilePath) {
        this.liabilityFilePath = liabilityFilePath;
    }

    public String getLiabilityFileURL() {
        return liabilityFileURL;
    }

    public void setLiabilityFileURL(String liabilityFileURL) {
        this.liabilityFileURL = liabilityFileURL;
    }

    public Boolean getHasBusinessLicense() {
        return hasBusinessLicense;
    }

    public void setHasBusinessLicense(Boolean hasBusinessLicense) {
        this.hasBusinessLicense = hasBusinessLicense;
    }

    public MultipartFile getBusinessLicenseDoc() {
        return businessLicenseDoc;
    }

    public void setBusinessLicenseDoc(MultipartFile businessLicenseDoc) {
        this.businessLicenseDoc = businessLicenseDoc;
    }

    public String getBusinessLicenseFilePath() {
        return businessLicenseFilePath;
    }

    public void setBusinessLicenseFilePath(String businessLicenseFilePath) {
        this.businessLicenseFilePath = businessLicenseFilePath;
    }

    public String getBusinessLicenseFileURL() {
        return businessLicenseFileURL;
    }

    public void setBusinessLicenseFileURL(String businessLicenseFileURL) {
        this.businessLicenseFileURL = businessLicenseFileURL;
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
}