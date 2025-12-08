package com.example.demo.Dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.FutureOrPresent;

public class WalkerToClientKycRequestDto {

    // ==================== Status Field ====================
    
    @Pattern(
        regexp = "^(PENDING|APPROVED|REJECTED)$",
        message = "Status must be one of: 'PENDING', 'APPROVED', 'REJECTED'"
    )
    private String status;

    // ==================== Pet & Routine Overview ====================
    
    @Pattern(
        regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
        message = "Pet UID must be a valid UUID format"
    )
    @Size(max = 100, message = "Pet UID must not exceed 100 characters")
    private String petUid;
    
    @Size(max = 500, message = "Pet names must not exceed 500 characters")
    private String petNames;
    
    @Size(max = 200, message = "Breed type must not exceed 200 characters")
    private String breedType;
    
    @Min(value = 0, message = "Age must be a positive number")
    private Integer age;
    
    @Size(max = 100, message = "Pet species must not exceed 100 characters")
    private String petSpecies;
    
    @Pattern(
        regexp = "^(Low|Medium|High|LOW|MEDIUM|HIGH)$",
        message = "Energy level must be one of: 'Low', 'Medium', 'High'"
    )
    private String energyLevel;
    
    @Pattern(
        regexp = "^(Beginner|Intermediate|Well-trained|Reactive|BEGINNER|INTERMEDIATE|WELL_TRAINED|REACTIVE)$",
        message = "Walking experience must be one of: 'Beginner', 'Intermediate', 'Well-trained', 'Reactive'"
    )
    private String walkingExperience;
    
    @Pattern(
        regexp = "^(Solo|Group|Either|SOLO|GROUP|EITHER)$",
        message = "Preferred walk type must be one of: 'Solo', 'Group', 'Either'"
    )
    private String preferredWalkType;
    
    @Pattern(
        regexp = "^(15|30|60|Custom)$",
        message = "Preferred walk duration must be one of: '15', '30', '60', 'Custom'"
    )
    private String preferredWalkDuration;
    
    @Min(value = 1, message = "Custom walk duration must be at least 1 minute")
    private Integer customWalkDuration;
    
    @Pattern(
        regexp = "^(Daily|Weekly|As needed|Other)$",
        message = "Frequency must be one of: 'Daily', 'Weekly', 'As needed', 'Other'"
    )
    private String frequency;
    
    @Size(max = 200, message = "Frequency other must not exceed 200 characters")
    private String frequencyOther;
    
    @Pattern(
        regexp = "^(Morning|Midday|Evening|Flexible)$",
        message = "Preferred time of day must be one of: 'Morning', 'Midday', 'Evening', 'Flexible'"
    )
    private String preferredTimeOfDay;
    
    @FutureOrPresent(message = "Preferred start date cannot be in the past")
    private LocalDate preferredStartDate;

    // ==================== Behavior & Handling ====================
    
    private List<String> leashBehavior;
    
    @Size(max = 300, message = "Leash behavior other must not exceed 300 characters")
    private String leashBehaviorOther;
    
    @Size(max = 1000, message = "Known triggers must not exceed 1000 characters")
    private String knownTriggers;
    
    @Pattern(
        regexp = "^(Friendly|Solo only|Unsure)$",
        message = "Social compatibility must be one of: 'Friendly', 'Solo only', 'Unsure'"
    )
    private String socialCompatibility;
    
    private List<String> handlingNotes;
    
    @Size(max = 300, message = "Handling notes other must not exceed 300 characters")
    private String handlingNotesOther;
    
    @Size(max = 1000, message = "Comforting methods must not exceed 1000 characters")
    private String comfortingMethods;

    // ==================== Health & Safety ====================
    
    private Boolean medicalConditions;
    
    @Size(max = 1000, message = "Medical conditions details must not exceed 1000 characters")
    private String medicalConditionsDetails;
    
    private Boolean medications;
    
    @Size(max = 1000, message = "Medications details must not exceed 1000 characters")
    private String medicationsDetails;
    
    @Size(max = 500, message = "Emergency vet info must not exceed 500 characters")
    private String emergencyVetInfo;

    // ==================== Access & Logistics ====================
    
    @Pattern(
        regexp = "^(Home|Apartment|Workplace|Other)$",
        message = "Starting location must be one of: 'Home', 'Apartment', 'Workplace', 'Other'"
    )
    private String startingLocation;
    
    @Size(max = 500, message = "Address/meeting point must not exceed 500 characters")
    private String addressMeetingPoint;
    
    @Size(max = 1000, message = "Access instructions must not exceed 1000 characters")
    private String accessInstructions;
    
    @Size(max = 200, message = "Backup contact must not exceed 200 characters")
    private String backupContact;
    
    private List<String> postWalkPreferences;

    // ==================== Services & Add-ons ====================
    
    private List<String> additionalServices;
    
    @Size(max = 500, message = "Additional services other must not exceed 500 characters")
    private String additionalServicesOther;

    // ==================== Consent & Signature ====================
    
    @NotNull(message = "Consent is required")
    private Boolean consent;
    
    @NotBlank(message = "Signature is required")
    @Size(max = 200, message = "Signature must not exceed 200 characters")
    private String signature;
    
    @NotNull(message = "Signature date is required")
    @PastOrPresent(message = "Signature date cannot be in the future")
    private LocalDate signatureDate;

    // ==================== Getters & Setters ====================

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPetUid() {
        return petUid;
    }

    public void setPetUid(String petUid) {
        this.petUid = petUid;
    }

    public String getPetNames() {
        return petNames;
    }

    public void setPetNames(String petNames) {
        this.petNames = petNames;
    }

    public String getBreedType() {
        return breedType;
    }

    public void setBreedType(String breedType) {
        this.breedType = breedType;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPetSpecies() {
        return petSpecies;
    }

    public void setPetSpecies(String petSpecies) {
        this.petSpecies = petSpecies;
    }

    public String getEnergyLevel() {
        return energyLevel;
    }

    public void setEnergyLevel(String energyLevel) {
        this.energyLevel = energyLevel;
    }

    public String getWalkingExperience() {
        return walkingExperience;
    }

    public void setWalkingExperience(String walkingExperience) {
        this.walkingExperience = walkingExperience;
    }

    public String getPreferredWalkType() {
        return preferredWalkType;
    }

    public void setPreferredWalkType(String preferredWalkType) {
        this.preferredWalkType = preferredWalkType;
    }

    public String getPreferredWalkDuration() {
        return preferredWalkDuration;
    }

    public void setPreferredWalkDuration(String preferredWalkDuration) {
        this.preferredWalkDuration = preferredWalkDuration;
    }

    public Integer getCustomWalkDuration() {
        return customWalkDuration;
    }

    public void setCustomWalkDuration(Integer customWalkDuration) {
        this.customWalkDuration = customWalkDuration;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getFrequencyOther() {
        return frequencyOther;
    }

    public void setFrequencyOther(String frequencyOther) {
        this.frequencyOther = frequencyOther;
    }

    public String getPreferredTimeOfDay() {
        return preferredTimeOfDay;
    }

    public void setPreferredTimeOfDay(String preferredTimeOfDay) {
        this.preferredTimeOfDay = preferredTimeOfDay;
    }

    public LocalDate getPreferredStartDate() {
        return preferredStartDate;
    }

    public void setPreferredStartDate(LocalDate preferredStartDate) {
        this.preferredStartDate = preferredStartDate;
    }

    public List<String> getLeashBehavior() {
        return leashBehavior;
    }

    public void setLeashBehavior(List<String> leashBehavior) {
        this.leashBehavior = leashBehavior;
    }

    public String getLeashBehaviorOther() {
        return leashBehaviorOther;
    }

    public void setLeashBehaviorOther(String leashBehaviorOther) {
        this.leashBehaviorOther = leashBehaviorOther;
    }

    public String getKnownTriggers() {
        return knownTriggers;
    }

    public void setKnownTriggers(String knownTriggers) {
        this.knownTriggers = knownTriggers;
    }

    public String getSocialCompatibility() {
        return socialCompatibility;
    }

    public void setSocialCompatibility(String socialCompatibility) {
        this.socialCompatibility = socialCompatibility;
    }

    public List<String> getHandlingNotes() {
        return handlingNotes;
    }

    public void setHandlingNotes(List<String> handlingNotes) {
        this.handlingNotes = handlingNotes;
    }

    public String getHandlingNotesOther() {
        return handlingNotesOther;
    }

    public void setHandlingNotesOther(String handlingNotesOther) {
        this.handlingNotesOther = handlingNotesOther;
    }

    public String getComfortingMethods() {
        return comfortingMethods;
    }

    public void setComfortingMethods(String comfortingMethods) {
        this.comfortingMethods = comfortingMethods;
    }

    public Boolean getMedicalConditions() {
        return medicalConditions;
    }

    public void setMedicalConditions(Boolean medicalConditions) {
        this.medicalConditions = medicalConditions;
    }

    public String getMedicalConditionsDetails() {
        return medicalConditionsDetails;
    }

    public void setMedicalConditionsDetails(String medicalConditionsDetails) {
        this.medicalConditionsDetails = medicalConditionsDetails;
    }

    public Boolean getMedications() {
        return medications;
    }

    public void setMedications(Boolean medications) {
        this.medications = medications;
    }

    public String getMedicationsDetails() {
        return medicationsDetails;
    }

    public void setMedicationsDetails(String medicationsDetails) {
        this.medicationsDetails = medicationsDetails;
    }

    public String getEmergencyVetInfo() {
        return emergencyVetInfo;
    }

    public void setEmergencyVetInfo(String emergencyVetInfo) {
        this.emergencyVetInfo = emergencyVetInfo;
    }

    public String getStartingLocation() {
        return startingLocation;
    }

    public void setStartingLocation(String startingLocation) {
        this.startingLocation = startingLocation;
    }

    public String getAddressMeetingPoint() {
        return addressMeetingPoint;
    }

    public void setAddressMeetingPoint(String addressMeetingPoint) {
        this.addressMeetingPoint = addressMeetingPoint;
    }

    public String getAccessInstructions() {
        return accessInstructions;
    }

    public void setAccessInstructions(String accessInstructions) {
        this.accessInstructions = accessInstructions;
    }

    public String getBackupContact() {
        return backupContact;
    }

    public void setBackupContact(String backupContact) {
        this.backupContact = backupContact;
    }

    public List<String> getPostWalkPreferences() {
        return postWalkPreferences;
    }

    public void setPostWalkPreferences(List<String> postWalkPreferences) {
        this.postWalkPreferences = postWalkPreferences;
    }

    public List<String> getAdditionalServices() {
        return additionalServices;
    }

    public void setAdditionalServices(List<String> additionalServices) {
        this.additionalServices = additionalServices;
    }

    public String getAdditionalServicesOther() {
        return additionalServicesOther;
    }

    public void setAdditionalServicesOther(String additionalServicesOther) {
        this.additionalServicesOther = additionalServicesOther;
    }

    public Boolean getConsent() {
        return consent;
    }

    public void setConsent(Boolean consent) {
        this.consent = consent;
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
}