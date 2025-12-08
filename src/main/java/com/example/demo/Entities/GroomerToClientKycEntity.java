package com.example.demo.Entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// Bean Validation
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.FutureOrPresent;

@Entity
@Table(name = "groomer_to_client_kyc")
public class GroomerToClientKycEntity extends BaseEntity {
    
    // Store petUid for reference
    @Column(length = 100)
    @NotBlank(message = "Pet UID is required")
    @Size(max = 100, message = "Pet UID must not exceed 100 characters")
    private String petUid;
    
    // Store userUid (extracted from access token)
    @Column(length = 100)
    @NotBlank(message = "User UID is required")
    @Size(max = 100, message = "User UID must not exceed 100 characters")
    private String userUid;

    // ==================== KYC Status ====================
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    @NotNull(message = "KYC status is required")
    private KycStatus status = KycStatus.PENDING;

    // ==================== Pet Relationship ====================
    
    @ManyToOne
    @JoinColumn(name = "pet_id", referencedColumnName = "id")
    @NotNull(message = "Pet reference is required")
    private PetsEntity pet;

    // ==================== Step 1: Grooming Preferences ====================
    
    @Column(length = 100)
    @NotBlank(message = "Grooming frequency is required")
    @Size(max = 100, message = "Grooming frequency must not exceed 100 characters")
    private String groomingFrequency; // "Every 4 weeks", "Every 6–8 weeks", etc.
    
    @PastOrPresent(message = "Last grooming date cannot be in the future")
    private LocalDate lastGroomingDate;
    
    @Column(length = 500)
    @Size(max = 500, message = "Preferred style must not exceed 500 characters")
    private String preferredStyle;
    
    @Column(length = 1000)
    @Size(max = 1000, message = "Avoid/focus areas must not exceed 1000 characters")
    private String avoidFocusAreas;

    // ==================== Step 2: Health & Safety ====================
    
    @ElementCollection(targetClass = HealthCondition.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "groomer_health_conditions", joinColumns = @JoinColumn(name = "kyc_id"))
    @Column(name = "health_condition")
    private List<HealthCondition> healthConditions; // Changed to List
    
    @Column(length = 300)
    @Size(max = 300, message = "Other health condition must not exceed 300 characters")
    private String otherHealthCondition;
    
    @NotNull(message = "On medication flag is required")
    private Boolean onMedication;
    
    @Column(length = 1000)
    @Size(max = 1000, message = "Medication details must not exceed 1000 characters")
    private String medicationDetails;
    
    @NotNull(message = "Injuries/surgery flag is required")
    private Boolean hadInjuriesSurgery;
    
    @Column(length = 1000)
    @Size(max = 1000, message = "Injury/surgery details must not exceed 1000 characters")
    private String injurySurgeryDetails;

    // ==================== Step 3: Behavior & Handling ====================
    
    @ElementCollection(targetClass = BehaviorIssue.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "groomer_behavior_issues", joinColumns = @JoinColumn(name = "kyc_id"))
    @Column(name = "behavior_issue")
    private List<BehaviorIssue> behaviorIssues; // Changed to List
    
    @Column(length = 1000)
    @Size(max = 1000, message = "Calming methods must not exceed 1000 characters")
    private String calmingMethods;
    
    @Column(length = 1000)
    @Size(max = 1000, message = "Triggers must not exceed 1000 characters")
    private String triggers;

    // ==================== Step 4: Services & Scheduling ====================
    
    @ElementCollection(targetClass = Service.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "groomer_services", joinColumns = @JoinColumn(name = "kyc_id"))
    @Column(name = "service")
    private List<Service> services; // Changed to List
    
    @Column(length = 300)
    @Size(max = 300, message = "Other service must not exceed 300 characters")
    private String otherService;
    
    @Column(length = 100)
    @NotBlank(message = "Grooming location is required")
    @Size(max = 100, message = "Grooming location must not exceed 100 characters")
    private String groomingLocation; // "Mobile/in-home grooming", etc.
    
    @FutureOrPresent(message = "Appointment date cannot be in the past")
    @NotNull(message = "Appointment date is required")
    private LocalDate appointmentDate;
    
    @NotNull(message = "Appointment time is required")
    private LocalTime appointmentTime;
    
    @Column(length = 2000)
    @Size(max = 2000, message = "Additional notes must not exceed 2000 characters")
    private String additionalNotes;
    
    @ElementCollection(targetClass = AddOn.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "groomer_addons", joinColumns = @JoinColumn(name = "kyc_id"))
    @Column(name = "addon")
    private List<AddOn> addOns;
    
    // ==================== Enums ====================
    
    public enum KycStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
    
    public enum BehaviorIssue {
        NERVOUSNESS_ANXIETY,
        DIFFICULTY_STANDING_STILL,
        FEAR_OF_LOUD_TOOLS,
        GROWLING_OR_SNAPPING,
        NONE_OF_THE_ABOVE
    }
   
    public enum AddOn {
        SCENTED_FINISH,
        DE_MATTING,
        SEASONAL_ACCESSORIES
    }
   
    public enum HealthCondition {
        SKIN_ISSUES,
        EAR_INFECTION,
        ARTHRITIS,
        ALLERGIES,
        NONE,
        OTHER
    }
   
    public enum Service {
        FULL_GROOM,
        BATH_BRUSH_ONLY,
        NAIL_TRIM,
        EAR_CLEANING,
        DESHEDDING,
        SPECIALITY_CREATIVE_CUT,
        OTHER
    }
    
    // ==================== Getters & Setters ====================

    public String getPetUid() {
        return petUid;
    }

    public void setPetUid(String petUid) {
        this.petUid = petUid;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public KycStatus getStatus() {
        return status;
    }

    public void setStatus(KycStatus status) {
        this.status = status;
    }

    public PetsEntity getPet() {
        return pet;
    }

    public void setPet(PetsEntity pet) {
        this.pet = pet;
    }

    public String getGroomingFrequency() {
        return groomingFrequency;
    }

    public void setGroomingFrequency(String groomingFrequency) {
        this.groomingFrequency = groomingFrequency;
    }

    public List<BehaviorIssue> getBehaviorIssues() {
        return behaviorIssues;
    }

    public void setBehaviorIssues(List<BehaviorIssue> behaviorIssues) {
        this.behaviorIssues = behaviorIssues;
    }

    public LocalDate getLastGroomingDate() {
        return lastGroomingDate;
    }

    public void setLastGroomingDate(LocalDate lastGroomingDate) {
        this.lastGroomingDate = lastGroomingDate;
    }

    public String getPreferredStyle() {
        return preferredStyle;
    }

    public void setPreferredStyle(String preferredStyle) {
        this.preferredStyle = preferredStyle;
    }

    public String getAvoidFocusAreas() {
        return avoidFocusAreas;
    }

    public void setAvoidFocusAreas(String avoidFocusAreas) {
        this.avoidFocusAreas = avoidFocusAreas;
    }

    public List<HealthCondition> getHealthConditions() {
        return healthConditions;
    }

    public void setHealthConditions(List<HealthCondition> healthConditions) {
        this.healthConditions = healthConditions;
    }

    public String getOtherHealthCondition() {
        return otherHealthCondition;
    }

    public void setOtherHealthCondition(String otherHealthCondition) {
        this.otherHealthCondition = otherHealthCondition;
    }

    public Boolean getOnMedication() {
        return onMedication;
    }

    public void setOnMedication(Boolean onMedication) {
        this.onMedication = onMedication;
    }

    public String getMedicationDetails() {
        return medicationDetails;
    }

    public void setMedicationDetails(String medicationDetails) {
        this.medicationDetails = medicationDetails;
    }

    public Boolean getHadInjuriesSurgery() {
        return hadInjuriesSurgery;
    }

    public void setHadInjuriesSurgery(Boolean hadInjuriesSurgery) {
        this.hadInjuriesSurgery = hadInjuriesSurgery;
    }

    public String getInjurySurgeryDetails() {
        return injurySurgeryDetails;
    }

    public void setInjurySurgeryDetails(String injurySurgeryDetails) {
        this.injurySurgeryDetails = injurySurgeryDetails;
    }

    public String getCalmingMethods() {
        return calmingMethods;
    }

    public void setCalmingMethods(String calmingMethods) {
        this.calmingMethods = calmingMethods;
    }

    public String getTriggers() {
        return triggers;
    }

    public void setTriggers(String triggers) {
        this.triggers = triggers;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public String getOtherService() {
        return otherService;
    }

    public void setOtherService(String otherService) {
        this.otherService = otherService;
    }

    public String getGroomingLocation() {
        return groomingLocation;
    }

    public void setGroomingLocation(String groomingLocation) {
        this.groomingLocation = groomingLocation;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }

    public List<AddOn> getAddOns() {
        return addOns;
    }

    public void setAddOns(List<AddOn> addOns) {
        this.addOns = addOns;
    }
}