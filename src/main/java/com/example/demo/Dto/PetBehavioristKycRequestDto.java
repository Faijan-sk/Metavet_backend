package com.example.demo.Dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PetBehavioristKycRequestDto {

    // ==================== Pet Reference ====================

    @NotBlank(message = "Pet UID is required")
    @Pattern(
        regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
        message = "Pet UID must be a valid UUID format"
    )
    @Size(max = 100, message = "Pet UID must not exceed 100 characters")
    private String petUid;

    // ==================== Step 1: Behavioral Concern Overview ====================

    @NotNull(message = "Behavioral challenges are required")
    @NotEmpty(message = "At least one behavioral challenge must be selected")
    private List<String> behavioralChallenges;
    // NOTE: Allowed values (case-insensitive):
    // "Separation anxiety", "Aggression", "Excessive barking",
    // "Leash pulling/reactivity", "Destructive behavior",
    // "Fearfulness", "Inappropriate elimination", "Other"

    @Size(max = 2000, message = "Aggression description cannot exceed 2000 characters")
    private String aggressionBiteDescription;

    @Size(max = 1000, message = "Other behavior description cannot exceed 1000 characters")
    private String otherBehaviorDescription;

    @NotBlank(message = "Behavior start time is required")
    @Size(max = 100, message = "Behavior start time must not exceed 100 characters")
    @Pattern(
        regexp = "(?i)^(As a puppy|Within the last year|Recently \\(last 3 months\\))$",
        message = "Behavior start time must be one of: 'As a puppy', 'Within the last year', 'Recently (last 3 months)'"
    )
    private String behaviorStartTime;

    @NotBlank(message = "Behavior frequency is required")
    @Size(max = 100, message = "Behavior frequency must not exceed 100 characters")
    @Pattern(
        regexp = "(?i)^(Daily|Weekly|Occasionally|Only in specific situations)$",
        message = "Behavior frequency must be one of: 'Daily', 'Weekly', 'Occasionally', 'Only in specific situations'"
    )
    private String behaviorFrequency;

    @Size(max = 1000, message = "Specific situations description cannot exceed 1000 characters")
    private String specificSituationsDescription;

    // ==================== Step 2: Triggers & Context ====================

    @Size(max = 2000, message = "Known triggers cannot exceed 2000 characters")
    private String knownTriggers;

    @Size(max = 100, message = "Behavior progress must not exceed 100 characters")
    @Pattern(
        regexp = "(?i)^(Improved|Worsened|Stayed the same)?$",
        message = "Behavior progress (if provided) must be one of: 'Improved', 'Worsened', 'Stayed the same'"
    )
    private String behaviorProgress;

    @Size(max = 1000, message = "Behavior progress context cannot exceed 1000 characters")
    private String behaviorProgressContext;

    // Optional list
    private List<String> aggressiveBehaviors;
    // Allowed values (case-insensitive):
    // "Growling", "Snapping", "Lunging",
    // "Biting (human or animal)", "No aggression observed"

    @Size(max = 2000, message = "Serious incidents description cannot exceed 2000 characters")
    private String seriousIncidents;

    // ==================== Step 3: Training & Tools History ====================

    @NotNull(message = "Worked with trainer status is required (true/false)")
    private Boolean workedWithTrainer;

    @Size(max = 2000, message = "Trainer approaches cannot exceed 2000 characters")
    private String trainerApproaches;

    // Optional list
    private List<String> currentTrainingTools;
    // Allowed values (case-insensitive):
    // "Clicker", "Muzzle", "Harness", "Prong collar",
    // "E-collar", "Crate training", "Other"

    @Size(max = 500, message = "Other training tool cannot exceed 500 characters")
    private String otherTrainingTool;

    @Size(max = 50, message = "Pet motivation must not exceed 50 characters")
    @Pattern(
        regexp = "(?i)^(Yes|No|Unsure)?$",
        message = "Pet motivation (if provided) must be one of: 'Yes', 'No', 'Unsure'"
    )
    private String petMotivation;

    @Size(max = 500, message = "Favorite rewards cannot exceed 500 characters")
    private String favoriteRewards;

    // ==================== Step 4: Routine & Environment ====================

    @Size(max = 50, message = "Walks per day must not exceed 50 characters")
    private String walksPerDay;

    @Size(max = 100, message = "Off leash time must not exceed 100 characters")
    private String offLeashTime;

    @Size(max = 100, message = "Time alone must not exceed 100 characters")
    private String timeAlone;

    @Size(max = 500, message = "Exercise stimulation cannot exceed 500 characters")
    private String exerciseStimulation;

    @NotNull(message = "Other pets status is required (true/false)")
    private Boolean otherPets;

    @Size(max = 2000, message = "Other pets details cannot exceed 2000 characters")
    private String otherPetsDetails;

    @NotNull(message = "Children in home status is required (true/false)")
    private Boolean childrenInHome;

    @Size(max = 200, message = "Children ages cannot exceed 200 characters")
    private String childrenAges;

    @Size(max = 2000, message = "Pet response with children cannot exceed 2000 characters")
    private String petResponseWithChildren;

    @NotBlank(message = "Home environment is required")
    @Size(max = 100, message = "Home environment must not exceed 100 characters")
    @Pattern(
        regexp = "(?i)^(Apartment|House with yard|Shared/communal|Other)$",
        message = "Home environment must be one of: 'Apartment', 'House with yard', 'Shared/communal', 'Other'"
    )
    private String homeEnvironment;

    @Size(max = 500, message = "Home environment other cannot exceed 500 characters")
    private String homeEnvironmentOther;

    // ==================== Step 5: Goals & Expectations ====================

    @Size(max = 3000, message = "Success outcome cannot exceed 3000 characters")
    private String successOutcome;

    @Size(max = 50, message = "Open to adjustments must not exceed 50 characters")
    @Pattern(
        regexp = "(?i)^(Yes|No|Not sure)?$",
        message = "Open to adjustments (if provided) must be one of: 'Yes', 'No', 'Not sure'"
    )
    private String openToAdjustments;

    @Size(max = 50, message = "Preferred session type must not exceed 50 characters")
    @Pattern(
        regexp = "(?i)^(In-person|Virtual|Either is fine)?$",
        message = "Preferred session type (if provided) must be one of: 'In-person', 'Virtual', 'Either is fine'"
    )
    private String preferredSessionType;

    @Size(max = 3000, message = "Additional notes cannot exceed 3000 characters")
    private String additionalNotes;

    // ==================== Consent ====================

    @NotNull(message = "Consent is required to proceed")
    private Boolean consentAccuracy;

    // ==================== Getters & Setters ====================

    public String getPetUid() {
        return petUid;
    }

    public void setPetUid(String petUid) {
        this.petUid = petUid;
    }

    public List<String> getBehavioralChallenges() {
        return behavioralChallenges;
    }

    public void setBehavioralChallenges(List<String> behavioralChallenges) {
        this.behavioralChallenges = behavioralChallenges;
    }

    public String getAggressionBiteDescription() {
        return aggressionBiteDescription;
    }

    public void setAggressionBiteDescription(String aggressionBiteDescription) {
        this.aggressionBiteDescription = aggressionBiteDescription;
    }

    public String getOtherBehaviorDescription() {
        return otherBehaviorDescription;
    }

    public void setOtherBehaviorDescription(String otherBehaviorDescription) {
        this.otherBehaviorDescription = otherBehaviorDescription;
    }

    public String getBehaviorStartTime() {
        return behaviorStartTime;
    }

    public void setBehaviorStartTime(String behaviorStartTime) {
        this.behaviorStartTime = behaviorStartTime;
    }

    public String getBehaviorFrequency() {
        return behaviorFrequency;
    }

    public void setBehaviorFrequency(String behaviorFrequency) {
        this.behaviorFrequency = behaviorFrequency;
    }

    public String getSpecificSituationsDescription() {
        return specificSituationsDescription;
    }

    public void setSpecificSituationsDescription(String specificSituationsDescription) {
        this.specificSituationsDescription = specificSituationsDescription;
    }

    public String getKnownTriggers() {
        return knownTriggers;
    }

    public void setKnownTriggers(String knownTriggers) {
        this.knownTriggers = knownTriggers;
    }

    public String getBehaviorProgress() {
        return behaviorProgress;
    }

    public void setBehaviorProgress(String behaviorProgress) {
        this.behaviorProgress = behaviorProgress;
    }

    public String getBehaviorProgressContext() {
        return behaviorProgressContext;
    }

    public void setBehaviorProgressContext(String behaviorProgressContext) {
        this.behaviorProgressContext = behaviorProgressContext;
    }

    public List<String> getAggressiveBehaviors() {
        return aggressiveBehaviors;
    }

    public void setAggressiveBehaviors(List<String> aggressiveBehaviors) {
        this.aggressiveBehaviors = aggressiveBehaviors;
    }

    public String getSeriousIncidents() {
        return seriousIncidents;
    }

    public void setSeriousIncidents(String seriousIncidents) {
        this.seriousIncidents = seriousIncidents;
    }

    public Boolean getWorkedWithTrainer() {
        return workedWithTrainer;
    }

    public void setWorkedWithTrainer(Boolean workedWithTrainer) {
        this.workedWithTrainer = workedWithTrainer;
    }

    public String getTrainerApproaches() {
        return trainerApproaches;
    }

    public void setTrainerApproaches(String trainerApproaches) {
        this.trainerApproaches = trainerApproaches;
    }

    public List<String> getCurrentTrainingTools() {
        return currentTrainingTools;
    }

    public void setCurrentTrainingTools(List<String> currentTrainingTools) {
        this.currentTrainingTools = currentTrainingTools;
    }

    public String getOtherTrainingTool() {
        return otherTrainingTool;
    }

    public void setOtherTrainingTool(String otherTrainingTool) {
        this.otherTrainingTool = otherTrainingTool;
    }

    public String getPetMotivation() {
        return petMotivation;
    }

    public void setPetMotivation(String petMotivation) {
        this.petMotivation = petMotivation;
    }

    public String getFavoriteRewards() {
        return favoriteRewards;
    }

    public void setFavoriteRewards(String favoriteRewards) {
        this.favoriteRewards = favoriteRewards;
    }

    public String getWalksPerDay() {
        return walksPerDay;
    }

    public void setWalksPerDay(String walksPerDay) {
        this.walksPerDay = walksPerDay;
    }

    public String getOffLeashTime() {
        return offLeashTime;
    }

    public void setOffLeashTime(String offLeashTime) {
        this.offLeashTime = offLeashTime;
    }

    public String getTimeAlone() {
        return timeAlone;
    }

    public void setTimeAlone(String timeAlone) {
        this.timeAlone = timeAlone;
    }

    public String getExerciseStimulation() {
        return exerciseStimulation;
    }

    public void setExerciseStimulation(String exerciseStimulation) {
        this.exerciseStimulation = exerciseStimulation;
    }

    public Boolean getOtherPets() {
        return otherPets;
    }

    public void setOtherPets(Boolean otherPets) {
        this.otherPets = otherPets;
    }

    public String getOtherPetsDetails() {
        return otherPetsDetails;
    }

    public void setOtherPetsDetails(String otherPetsDetails) {
        this.otherPetsDetails = otherPetsDetails;
    }

    public Boolean getChildrenInHome() {
        return childrenInHome;
    }

    public void setChildrenInHome(Boolean childrenInHome) {
        this.childrenInHome = childrenInHome;
    }

    public String getChildrenAges() {
        return childrenAges;
    }

    public void setChildrenAges(String childrenAges) {
        this.childrenAges = childrenAges;
    }

    public String getPetResponseWithChildren() {
        return petResponseWithChildren;
    }

    public void setPetResponseWithChildren(String petResponseWithChildren) {
        this.petResponseWithChildren = petResponseWithChildren;
    }

    public String getHomeEnvironment() {
        return homeEnvironment;
    }

    public void setHomeEnvironment(String homeEnvironment) {
        this.homeEnvironment = homeEnvironment;
    }

    public String getHomeEnvironmentOther() {
        return homeEnvironmentOther;
    }

    public void setHomeEnvironmentOther(String homeEnvironmentOther) {
        this.homeEnvironmentOther = homeEnvironmentOther;
    }

    public String getSuccessOutcome() {
        return successOutcome;
    }

    public void setSuccessOutcome(String successOutcome) {
        this.successOutcome = successOutcome;
    }

    public String getOpenToAdjustments() {
        return openToAdjustments;
    }

    public void setOpenToAdjustments(String openToAdjustments) {
        this.openToAdjustments = openToAdjustments;
    }

    public String getPreferredSessionType() {
        return preferredSessionType;
    }

    public void setPreferredSessionType(String preferredSessionType) {
        this.preferredSessionType = preferredSessionType;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }

    public Boolean getConsentAccuracy() {
        return consentAccuracy;
    }

    public void setConsentAccuracy(Boolean consentAccuracy) {
        this.consentAccuracy = consentAccuracy;
    }
}
