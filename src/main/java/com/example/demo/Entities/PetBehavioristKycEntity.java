package com.example.demo.Entities;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "behaviorist_to_client_kyc")
public class PetBehavioristKycEntity extends BaseEntity {

    // Store petUid for reference (optional, for debugging)
    @Column(length = 100)
    private String petUid;
    
    
    @Column(length = 100)
    @NotBlank(message = "User UID is required")
    @Size(max = 100, message = "User UID must not exceed 100 characters")
    private String userUid;
    
    

    // ==================== Pet Reference ====================

    @ManyToOne
    @JoinColumn(name = "pet_id", referencedColumnName = "id")
    private PetsEntity pet;

    // ==================== Step 1: Behavioral Concern Overview ====================

    // DB me comma-separated string hi rahega (SINGLE TABLE)
    @Column(length = 1000)
    private String behavioralChallenges; // "Separation anxiety,Aggression,Excessive barking"

    // ===== Enum-friendly transient field + getter/setter (code me ENUM use hoga) =====
    @Transient
    private List<BehavioralChallenges> behavioralChallengesEnums;

    @Column(length = 2000)
    private String aggressionBiteDescription;

    @Column(length = 1000)
    private String otherBehaviorDescription;

    @Column(length = 100)
    private String behaviorStartTime; // "As a puppy", "Within the last year", "Recently (last 3 months)"

    @Column(length = 100)
    private String behaviorFrequency; // "Daily", "Weekly", "Occasionally", "Only in specific situations"

    @Column(length = 1000)
    private String specificSituationsDescription;

    // ==================== Step 2: Triggers & Context ====================

    @Column(length = 2000)
    private String knownTriggers;

    @Column(length = 100)
    private String behaviorProgress; // "Improved", "Worsened", "Stayed the same"

    @Column(length = 1000)
    private String behaviorProgressContext;

    // DB me comma-separated string hi rahega
    @Column(length = 500)
    private String aggressiveBehaviors; // "Growling,Snapping,Lunging"

    // ===== Enum-friendly transient field =====
    @Transient
    private List<AggressiveBehaviors> aggressiveBehaviorsEnums;

    @Column(length = 2000)
    private String seriousIncidents;

    // ==================== Step 3: Training & Tools History ====================

    private Boolean workedWithTrainer;

    @Column(length = 2000)
    private String trainerApproaches;

    // DB me comma-separated string hi rahega
    @Column(length = 500)
    private String currentTrainingTools; // "Clicker,Muzzle,Harness"

    // ===== Enum-friendly transient field =====
    @Transient
    private List<CurrentTrainingTools> currentTrainingToolsEnums;

    @Column(length = 500)
    private String otherTrainingTool;

    @Column(length = 50)
    private String petMotivation; // "Yes", "No", "Unsure"

    @Column(length = 500)
    private String favoriteRewards;

    // ==================== Step 4: Routine & Environment ====================

    @Column(length = 50)
    private String walksPerDay;

    @Column(length = 100)
    private String offLeashTime;

    @Column(length = 100)
    private String timeAlone;

    @Column(length = 500)
    private String exerciseStimulation;

    private Boolean otherPets;

    @Column(length = 2000)
    private String otherPetsDetails;

    private Boolean childrenInHome;

    @Column(length = 200)
    private String childrenAges;

    @Column(length = 2000)
    private String petResponseWithChildren;

    @Column(length = 100)
    private String homeEnvironment; // "Apartment", "House with yard", "Shared/communal", "Other"

    @Column(length = 500)
    private String homeEnvironmentOther;

    // ==================== Step 5: Goals & Expectations ====================

    @Column(length = 3000)
    private String successOutcome;

    @Column(length = 50)
    private String openToAdjustments; // "Yes", "No", "Not sure"

    @Column(length = 50)
    private String preferredSessionType; // "In-person", "Virtual", "Either is fine"

    @Column(length = 3000)
    private String additionalNotes;

    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_status", length = 20, nullable = false)
    private KycStatus kycStatus = KycStatus.PENDING; // default = PENDING

    // ==================== Consent ====================

    @Column(nullable = false)
    private Boolean consentAccuracy = false;

    // ==================== ENUMS ====================

    public enum KycStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

    public enum BehavioralChallenges {

        SEPARATION_ANXIETY("Separation anxiety"),
        AGGRESSION("Aggression"),
        EXCESSIVE_BARKING("Excessive barking"),
        LEASH_PULLING_REACTIVITY("Leash pulling/reactivity"),
        DESTRUCTIVE_BEHAVIOR("Destructive behavior"),
        FEARFULNESS("Fearfulness"),
        INAPPROPRIATE_ELIMINATION("Inappropriate elimination"),
        OTHER("Other");

        private final String label;

        BehavioralChallenges(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        // ⭐ Accepts ANY format: "aggression", "Aggression", "aggre-ssion", etc.
        public static BehavioralChallenges fromString(String value) {
            if (value == null || value.trim().isEmpty()) {
                return null;
            }

            String normalized = value.trim()
                    .toUpperCase()
                    .replace(" ", "_")
                    .replace("-", "_")
                    .replace("/", "_");

            switch (normalized) {
                case "SEPARATION_ANXIETY":
                    return SEPARATION_ANXIETY;
                case "AGGRESSION":
                    return AGGRESSION;
                case "EXCESSIVE_BARKING":
                    return EXCESSIVE_BARKING;
                case "LEASH_PULLING_REACTIVITY":
                    return LEASH_PULLING_REACTIVITY;
                case "DESTRUCTIVE_BEHAVIOR":
                    return DESTRUCTIVE_BEHAVIOR;
                case "FEARFULNESS":
                    return FEARFULNESS;
                case "INAPPROPRIATE_ELIMINATION":
                    return INAPPROPRIATE_ELIMINATION;
                case "OTHER":
                    return OTHER;
                default:
                    throw new IllegalArgumentException("Invalid behavioral challenge: " + value);
            }
        }
    }

    public enum AggressiveBehaviors {

        GROWLING("Growling"),
        SNAPPING("Snapping"),
        LUNGING("Lunging"),
        BITING_HUMAN_OR_ANIMAL("Biting (human or animal)"),
        NO_AGGRESSION_OBSERVED("No aggression observed");

        private final String label;

        AggressiveBehaviors(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        // ⭐ Accept ANY input format
        public static AggressiveBehaviors fromString(String value) {
            if (value == null || value.trim().isEmpty()) return null;

            String normalized = value.trim()
                    .toUpperCase()
                    .replace(" ", "_")
                    .replace("-", "_")
                    .replace("/", "_")
                    .replace("(", "")
                    .replace(")", "");

            switch (normalized) {
                case "GROWLING":
                    return GROWLING;
                case "SNAPPING":
                    return SNAPPING;
                case "LUNGING":
                    return LUNGING;
                case "BITING_HUMAN_OR_ANIMAL":
                    return BITING_HUMAN_OR_ANIMAL;
                case "NO_AGGRESSION_OBSERVED":
                    return NO_AGGRESSION_OBSERVED;
                default:
                    throw new IllegalArgumentException("Invalid aggressive behavior: " + value);
            }
        }
    }

    public enum CurrentTrainingTools {

        CLICKER("Clicker"),
        MUZZLE("Muzzle"),
        HARNESS("Harness"),
        PRONG_COLLAR("Prong collar"),
        E_COLLAR("E-collar"),
        CRATE_TRAINING("Crate training"),
        OTHER("Other");

        private final String label;

        CurrentTrainingTools(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        // ⭐ Accepts ANY input format (small case, with dash, spaces, e-collar, etc.)
        public static CurrentTrainingTools fromString(String value) {
            if (value == null || value.trim().isEmpty()) return null;

            String normalized = value.trim()
                    .toUpperCase()
                    .replace(" ", "_")
                    .replace("-", "_")
                    .replace("/", "_");

            switch (normalized) {
                case "CLICKER":
                    return CLICKER;
                case "MUZZLE":
                    return MUZZLE;
                case "HARNESS":
                    return HARNESS;
                case "PRONG_COLLAR":
                    return PRONG_COLLAR;
                case "E_COLLAR":
                    return E_COLLAR;
                case "CRATE_TRAINING":
                    return CRATE_TRAINING;
                case "OTHER":
                    return OTHER;
                default:
                    throw new IllegalArgumentException("Invalid training tool: " + value);
            }
        }
    }

    // ==================== ENUM-BASED GETTERS/SETTERS (NO EXTRA TABLE) ====================
    // FRONTEND se List<Enum> bhejo, DB me comma-separated String hi stored rahega

    // ---------- BehavioralChallenges ----------
    public List<BehavioralChallenges> getBehavioralChallengesEnums() {
        if (behavioralChallenges == null || behavioralChallenges.isBlank()) {
            return List.of();
        }
        return Arrays.stream(behavioralChallenges.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(BehavioralChallenges::fromString)
                .collect(Collectors.toList());
    }

    public void setBehavioralChallengesEnums(List<BehavioralChallenges> enums) {
        this.behavioralChallengesEnums = enums;
        if (enums == null || enums.isEmpty()) {
            this.behavioralChallenges = null;
        } else {
            this.behavioralChallenges = enums.stream()
                    .map(BehavioralChallenges::getLabel)
                    .collect(Collectors.joining(","));
        }
    }

    // ---------- AggressiveBehaviors ----------
    public List<AggressiveBehaviors> getAggressiveBehaviorsEnums() {
        if (aggressiveBehaviors == null || aggressiveBehaviors.isBlank()) {
            return List.of();
        }
        return Arrays.stream(aggressiveBehaviors.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(AggressiveBehaviors::fromString)
                .collect(Collectors.toList());
    }

    public void setAggressiveBehaviorsEnums(List<AggressiveBehaviors> enums) {
        this.aggressiveBehaviorsEnums = enums;
        if (enums == null || enums.isEmpty()) {
            this.aggressiveBehaviors = null;
        } else {
            this.aggressiveBehaviors = enums.stream()
                    .map(AggressiveBehaviors::getLabel)
                    .collect(Collectors.joining(","));
        }
    }

    // ---------- CurrentTrainingTools ----------
    public List<CurrentTrainingTools> getCurrentTrainingToolsEnums() {
        if (currentTrainingTools == null || currentTrainingTools.isBlank()) {
            return List.of();
        }
        return Arrays.stream(currentTrainingTools.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(CurrentTrainingTools::fromString)
                .collect(Collectors.toList());
    }

    public void setCurrentTrainingToolsEnums(List<CurrentTrainingTools> enums) {
        this.currentTrainingToolsEnums = enums;
        if (enums == null || enums.isEmpty()) {
            this.currentTrainingTools = null;
        } else {
            this.currentTrainingTools = enums.stream()
                    .map(CurrentTrainingTools::getLabel)
                    .collect(Collectors.joining(","));
        }
    }

	public String getPetUid() {
		return petUid;
	}

	public void setPetUid(String petUid) {
		this.petUid = petUid;
	}

	public PetsEntity getPet() {
		return pet;
	}

	public void setPet(PetsEntity pet) {
		this.pet = pet;
	}

	public String getBehavioralChallenges() {
		return behavioralChallenges;
	}

	public void setBehavioralChallenges(String behavioralChallenges) {
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

	public String getAggressiveBehaviors() {
		return aggressiveBehaviors;
	}

	public void setAggressiveBehaviors(String aggressiveBehaviors) {
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

	public String getCurrentTrainingTools() {
		return currentTrainingTools;
	}

	public void setCurrentTrainingTools(String currentTrainingTools) {
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

	public KycStatus getKycStatus() {
		return kycStatus;
	}

	public void setKycStatus(KycStatus kycStatus) {
		this.kycStatus = kycStatus;
	}

	public Boolean getConsentAccuracy() {
		return consentAccuracy;
	}

	public void setConsentAccuracy(Boolean consentAccuracy) {
		this.consentAccuracy = consentAccuracy;
	}

	public String getUserUid() {
		return userUid;
	}

	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}
    
    
    
    
    
    
    
    
}