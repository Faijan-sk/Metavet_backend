package com.example.demo.Dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class GroomerToClientKycRequestDto {

    // ==================== Pet Reference ====================
    
    @NotBlank(message = "Pet UID is required")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", 
             message = "Pet UID must be a valid UUID format")
    private String petUid;

    // ==================== Step 1: Grooming Preferences ====================
    
    @NotBlank(message = "Grooming frequency is required")
    @Pattern(regexp = "^(Every 4 weeks|Every 6–8 weeks|Every 3 months|Other)$", 
             message = "Grooming frequency must be one of: 'Every 4 weeks', 'Every 6–8 weeks', 'Every 3 months', 'Other'")
    private String groomingFrequency;
    
    private LocalDate lastGroomingDate;
    
    @Size(max = 500, message = "Preferred style cannot exceed 500 characters")
    private String preferredStyle;
    
    @Size(max = 500, message = "Avoid focus areas cannot exceed 500 characters")
    private String avoidFocusAreas;

    // ==================== Step 2: Health & Safety ====================
    
    private List<@Pattern(regexp = "^(Skin issues|Ear infections|Allergies|Arthritis|Heart condition|None|Other)$",
                          message = "Invalid health condition. Allowed values: 'Skin issues', 'Ear infections', 'Allergies', 'Arthritis', 'Heart condition', 'None', 'Other'") 
                 String> healthConditions;
    
    @Size(max = 300, message = "Other health condition cannot exceed 300 characters")
    private String otherHealthCondition;
    
    @NotNull(message = "Medication status is required (true/false)")
    private Boolean onMedication;
    
    @Size(max = 500, message = "Medication details cannot exceed 500 characters")
    private String medicationDetails;
    
    @NotNull(message = "Injury/surgery history is required (true/false)")
    private Boolean hadInjuriesSurgery;
    
    @Size(max = 500, message = "Injury/surgery details cannot exceed 500 characters")
    private String injurySurgeryDetails;

    // ==================== Step 3: Behavior & Handling ====================
    
    private List<@Pattern(regexp = "^(Nervousness/anxiety|Fear of loud tools|Aggression|Biting|Excessive movement|None|Other)$",
                          message = "Invalid behavior issue. Allowed values: 'Nervousness/anxiety', 'Fear of loud tools', 'Aggression', 'Biting', 'Excessive movement', 'None', 'Other'")
                 String> behaviorIssues;
    
    @Size(max = 500, message = "Calming methods cannot exceed 500 characters")
    private String calmingMethods;
    
    @Size(max = 500, message = "Triggers cannot exceed 500 characters")
    private String triggers;

    // ==================== Step 4: Services & Scheduling ====================
    
    private List<@Pattern(regexp = "^(Full groom \\(bath \\+ cut\\)|Bath only|Nail trim|Ear cleaning|Teeth cleaning|De-shedding treatment|Other)$",
                          message = "Invalid service. Allowed values: 'Full groom (bath + cut)', 'Bath only', 'Nail trim', 'Ear cleaning', 'Teeth cleaning', 'De-shedding treatment', 'Other'")
                 String> services;
    
    @Size(max = 300, message = "Other service cannot exceed 300 characters")
    private String otherService;
    
    @NotBlank(message = "Grooming location preference is required")
    @Pattern(regexp = "^(Mobile/in-home grooming|Grooming salon|Veterinary clinic)$",
             message = "Grooming location must be one of: 'Mobile/in-home grooming', 'Grooming salon', 'Veterinary clinic'")
    private String groomingLocation;
    
    private LocalDate appointmentDate;
    
    private LocalTime appointmentTime;
    
    @Size(max = 1000, message = "Additional notes cannot exceed 1000 characters")
    private String additionalNotes;
    
    private List<@Pattern(regexp = "^(Scented finish|De-matting|Flea/tick treatment|Paw balm|Bow/bandana)$",
                          message = "Invalid add-on. Allowed values: 'Scented finish', 'De-matting', 'Flea/tick treatment', 'Paw balm', 'Bow/bandana'")
                 String> addOns;

    // ==================== Getters & Setters ====================

    public String getPetUid() {
        return petUid;
    }

    public void setPetUid(String petUid) {
        this.petUid = petUid;
    }

    public String getGroomingFrequency() {
        return groomingFrequency;
    }

    public void setGroomingFrequency(String groomingFrequency) {
        this.groomingFrequency = groomingFrequency;
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

    public List<String> getHealthConditions() {
        return healthConditions;
    }

    public void setHealthConditions(List<String> healthConditions) {
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

    public List<String> getBehaviorIssues() {
        return behaviorIssues;
    }

    public void setBehaviorIssues(List<String> behaviorIssues) {
        this.behaviorIssues = behaviorIssues;
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

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
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

    public List<String> getAddOns() {
        return addOns;
    }

    public void setAddOns(List<String> addOns) {
        this.addOns = addOns;
    }
}