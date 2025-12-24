	package com.example.demo.Dto;
	
	import java.time.LocalDate;
	import java.time.LocalTime;
	import java.util.List;
	
	import jakarta.validation.constraints.FutureOrPresent;
	import jakarta.validation.constraints.NotBlank;
	import jakarta.validation.constraints.NotNull;
	import jakarta.validation.constraints.NotEmpty;
	import jakarta.validation.constraints.PastOrPresent;
	import jakarta.validation.constraints.Pattern;
	import jakarta.validation.constraints.Size;
	
	import com.example.demo.Entities.GroomerToClientKycEntity.HealthCondition;
	import com.example.demo.Entities.GroomerToClientKycEntity.BehaviorIssue;
	import com.example.demo.Entities.GroomerToClientKycEntity.Service;
	import com.example.demo.Entities.GroomerToClientKycEntity.AddOn;
	
	public class GroomerToClientKycRequestDto {
	
	    // ==================== Pet Reference ====================
	    
	    @NotBlank(message = "Pet UID is required")
	    @Pattern(
	        regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
	        message = "Pet UID must be a valid UUID format"
	    )
	    @Size(max = 100, message = "Pet UID must not exceed 100 characters")
	    private String petUid;
	
	    // ==================== Step 1: Grooming Preferences ====================
	    
	    @NotBlank(message = "Grooming frequency is required")
	    @Size(max = 100, message = "Grooming frequency must not exceed 100 characters")
	    private String groomingFrequency;
	    
	    @PastOrPresent(message = "Last grooming date cannot be in the future")
	    private LocalDate lastGroomingDate;
	    
	    @Size(max = 500, message = "Preferred style cannot exceed 500 characters")
	    private String preferredStyle;
	    
	    @Size(max = 1000, message = "Avoid focus areas cannot exceed 1000 characters")
	    private String avoidFocusAreas;
	
	    // ==================== Step 2: Health & Safety ====================
	    
	    // REMOVED @Pattern - enums don't need pattern validation
	    @NotNull(message = "Health conditions are required")
	    @NotEmpty(message = "At least one health condition must be selected")
	    private List<HealthCondition> healthConditions;
	    
	    @Size(max = 300, message = "Other health condition cannot exceed 300 characters")
	    private String otherHealthCondition;
	    
	    @NotNull(message = "Medication status is required (true/false)")
	    private Boolean onMedication;
	    
	    @Size(max = 1000, message = "Medication details cannot exceed 1000 characters")
	    private String medicationDetails;
	    
	    @NotNull(message = "Injury/surgery history is required (true/false)")
	    private Boolean hadInjuriesSurgery;
	    
	    @Size(max = 1000, message = "Injury/surgery details cannot exceed 1000 characters")
	    private String injurySurgeryDetails;
	
	    // ==================== Step 3: Behavior & Handling ====================
	    
	    // REMOVED @Pattern - enums don't need pattern validation
	    @NotNull(message = "Behavior issues are required")
	    @NotEmpty(message = "At least one behavior issue must be selected")
	    private List<BehaviorIssue> behaviorIssues;
	    
	    @Size(max = 1000, message = "Calming methods cannot exceed 1000 characters")
	    private String calmingMethods;
	    
	    @Size(max = 1000, message = "Triggers cannot exceed 1000 characters")
	    private String triggers;
	
	    // ==================== Step 4: Services & Scheduling ====================
	    
	    // REMOVED @Pattern - enums don't need pattern validation
	    @NotNull(message = "Services are required")
	    @NotEmpty(message = "At least one service must be selected")
	    private List<Service> services;
	    
	    @Size(max = 300, message = "Other service cannot exceed 300 characters")
	    private String otherService;
	    
	    @NotBlank(message = "Grooming location preference is required")
	    @Pattern(
	        regexp = "^(Mobile/in-home grooming|Grooming salon|Either is Fine)$",
	        message = "Grooming location must be one of: 'Mobile/in-home grooming', 'Grooming salon', 'Either is Fine'"
	    )
	    @Size(max = 100, message = "Grooming location must not exceed 100 characters")
	    private String groomingLocation;
	    
	    @NotNull(message = "Appointment date is required")
	    @FutureOrPresent(message = "Appointment date cannot be in the past")
	    private LocalDate appointmentDate;
	    
	    @NotNull(message = "Appointment time is required")
	    private LocalTime appointmentTime;
	    
	    @Size(max = 2000, message = "Additional notes cannot exceed 2000 characters")
	    private String additionalNotes;
	    
	    // REMOVED @Pattern - enums don't need pattern validation
	    private List<AddOn> addOns;
	
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
	
	    public List<BehaviorIssue> getBehaviorIssues() {
	        return behaviorIssues;
	    }
	
	    public void setBehaviorIssues(List<BehaviorIssue> behaviorIssues) {
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