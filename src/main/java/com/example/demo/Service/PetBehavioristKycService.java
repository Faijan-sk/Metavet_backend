package com.example.demo.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Dto.PetBehavioristKycRequestDto;
import com.example.demo.Entities.PetsEntity;
import com.example.demo.Entities.PetBehavioristKycEntity;
import com.example.demo.Entities.PetBehavioristKycEntity.BehavioralChallenges;
import com.example.demo.Entities.PetBehavioristKycEntity.AggressiveBehaviors;
import com.example.demo.Entities.PetBehavioristKycEntity.CurrentTrainingTools;
import com.example.demo.Entities.PetBehavioristKycEntity.KycStatus;
import com.example.demo.Repository.PetRepo;
import com.example.demo.Repository.PetBehavioristKycRepo;

import jakarta.validation.ValidationException;

@Service
public class PetBehavioristKycService {

    @Autowired
    private PetBehavioristKycRepo behavioristKycRepo;

    @Autowired
    private PetRepo petsRepo;

    // =====================================================
    // 01. CREATE KYC
    // =====================================================
    @Transactional
    public PetBehavioristKycEntity createBehavioristKyc(PetBehavioristKycRequestDto dto) throws ValidationException {

        // ---------- Basic sanity validations (business rules) ----------

        // Consent must be true
        if (dto.getConsentAccuracy() == null || !Boolean.TRUE.equals(dto.getConsentAccuracy())) {
            throw new ValidationException("CONSENT_REQUIRED: You must confirm that the information is accurate.");
        }

        // Pet UID parse + pet existence check
        UUID petUid;
        try {
            petUid = UUID.fromString(dto.getPetUid());
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("INVALID_PET_UID: Pet UID must be a valid UUID.");
        }

        PetsEntity pet = petsRepo.findByUid(petUid)
                .orElseThrow(() -> new ValidationException("PET_NOT_FOUND: No pet found for the given petUid."));

        // Behavioral challenges must not be empty (DTO already has @NotNull/@NotEmpty, but double check)
        if (dto.getBehavioralChallenges() == null || dto.getBehavioralChallenges().isEmpty()) {
            throw new ValidationException("BEHAVIORAL_CHALLENGES_REQUIRED: At least one behavioral challenge must be selected.");
        }

        // ---------- Convert list<String> -> Enums (with validation) ----------
        List<BehavioralChallenges> challengeEnums = convertBehavioralChallenges(dto.getBehavioralChallenges());
        List<AggressiveBehaviors> aggressiveEnums = convertAggressiveBehaviors(dto.getAggressiveBehaviors());
        List<CurrentTrainingTools> trainingToolEnums = convertCurrentTrainingTools(dto.getCurrentTrainingTools());

        // ---------- Conditional validations based on selections ----------

        // If AGGRESSION selected, aggressionBiteDescription must be provided
        if (challengeEnums.contains(BehavioralChallenges.AGGRESSION)) {
            if (dto.getAggressionBiteDescription() == null || dto.getAggressionBiteDescription().trim().isEmpty()) {
                throw new ValidationException(
                        "AGGRESSION_DESCRIPTION_REQUIRED: When 'Aggression' is selected, please describe any biting incidents or context.");
            }
        }

        // If OTHER selected in behavioralChallenges, otherBehaviorDescription must be provided
        if (challengeEnums.contains(BehavioralChallenges.OTHER)) {
            if (dto.getOtherBehaviorDescription() == null || dto.getOtherBehaviorDescription().trim().isEmpty()) {
                throw new ValidationException(
                        "OTHER_BEHAVIOR_DESCRIPTION_REQUIRED: When 'Other' is selected, 'otherBehaviorDescription' is required.");
            }
        }

        // If behaviorFrequency = "Only in specific situations", specificSituationsDescription is required
        if (dto.getBehaviorFrequency() != null &&
                dto.getBehaviorFrequency().equalsIgnoreCase("Only in specific situations") ) {

            if (dto.getSpecificSituationsDescription() == null ||
                    dto.getSpecificSituationsDescription().trim().isEmpty()) {

                throw new ValidationException(
                        "SPECIFIC_SITUATIONS_REQUIRED: Please describe the specific situations where the behavior occurs.");
            }
        }

        // If workedWithTrainer = true -> trainerApproaches required
        if (Boolean.TRUE.equals(dto.getWorkedWithTrainer())) {
            if (dto.getTrainerApproaches() == null || dto.getTrainerApproaches().trim().isEmpty()) {
                throw new ValidationException(
                        "TRAINER_APPROACHES_REQUIRED: Since you have worked with a trainer, please describe what approaches were used.");
            }
        }

        // If training tools include OTHER, otherTrainingTool is required
        if (trainingToolEnums.contains(CurrentTrainingTools.OTHER)) {
            if (dto.getOtherTrainingTool() == null || dto.getOtherTrainingTool().trim().isEmpty()) {
                throw new ValidationException(
                        "OTHER_TRAINING_TOOL_REQUIRED: When 'Other' training tool is selected, 'otherTrainingTool' description is required.");
            }
        }

        // If petMotivation = Yes -> favoriteRewards required
        if (dto.getPetMotivation() != null && dto.getPetMotivation().equalsIgnoreCase("Yes")) {
            if (dto.getFavoriteRewards() == null || dto.getFavoriteRewards().trim().isEmpty()) {
                throw new ValidationException(
                        "FAVORITE_REWARDS_REQUIRED: Please specify your pet's favorite rewards when they are food/toy motivated.");
            }
        }

        // If otherPets = true -> otherPetsDetails required
        if (Boolean.TRUE.equals(dto.getOtherPets())) {
            if (dto.getOtherPetsDetails() == null || dto.getOtherPetsDetails().trim().isEmpty()) {
                throw new ValidationException(
                        "OTHER_PETS_DETAILS_REQUIRED: When 'otherPets' is true, 'otherPetsDetails' is required (types, ages, how they get along).");
            }
        }

        // If childrenInHome = true -> childrenAges & petResponseWithChildren required
        if (Boolean.TRUE.equals(dto.getChildrenInHome())) {
            if (dto.getChildrenAges() == null || dto.getChildrenAges().trim().isEmpty()) {
                throw new ValidationException(
                        "CHILDREN_AGES_REQUIRED: When 'childrenInHome' is true, 'childrenAges' is required.");
            }
            if (dto.getPetResponseWithChildren() == null || dto.getPetResponseWithChildren().trim().isEmpty()) {
                throw new ValidationException(
                        "PET_RESPONSE_WITH_CHILDREN_REQUIRED: Please describe how your pet responds around children.");
            }
        }

        // If homeEnvironment = Other -> homeEnvironmentOther required
        if (dto.getHomeEnvironment() != null && dto.getHomeEnvironment().equalsIgnoreCase("Other")) {
            if (dto.getHomeEnvironmentOther() == null || dto.getHomeEnvironmentOther().trim().isEmpty()) {
                throw new ValidationException(
                        "HOME_ENVIRONMENT_OTHER_REQUIRED: When 'Other' is selected for home environment, please describe it.");
            }
        }

        // ---------- Map DTO -> Entity ----------

        PetBehavioristKycEntity entity = new PetBehavioristKycEntity();

        entity.setPet(pet);
        entity.setPetUid(dto.getPetUid());

        // Step 1
        entity.setBehavioralChallengesEnums(challengeEnums); // will set comma-separated string internally
        entity.setAggressionBiteDescription(dto.getAggressionBiteDescription());
        entity.setOtherBehaviorDescription(dto.getOtherBehaviorDescription());
        entity.setBehaviorStartTime(dto.getBehaviorStartTime());
        entity.setBehaviorFrequency(dto.getBehaviorFrequency());
        entity.setSpecificSituationsDescription(dto.getSpecificSituationsDescription());

        // Step 2
        entity.setKnownTriggers(dto.getKnownTriggers());
        entity.setBehaviorProgress(dto.getBehaviorProgress());
        entity.setBehaviorProgressContext(dto.getBehaviorProgressContext());
        entity.setAggressiveBehaviorsEnums(aggressiveEnums);
        entity.setSeriousIncidents(dto.getSeriousIncidents());

        // Step 3
        entity.setWorkedWithTrainer(dto.getWorkedWithTrainer());
        entity.setTrainerApproaches(dto.getTrainerApproaches());
        entity.setCurrentTrainingToolsEnums(trainingToolEnums);
        entity.setOtherTrainingTool(dto.getOtherTrainingTool());
        entity.setPetMotivation(dto.getPetMotivation());
        entity.setFavoriteRewards(dto.getFavoriteRewards());

        // Step 4
        entity.setWalksPerDay(dto.getWalksPerDay());
        entity.setOffLeashTime(dto.getOffLeashTime());
        entity.setTimeAlone(dto.getTimeAlone());
        entity.setExerciseStimulation(dto.getExerciseStimulation());
        entity.setOtherPets(dto.getOtherPets());
        entity.setOtherPetsDetails(dto.getOtherPetsDetails());
        entity.setChildrenInHome(dto.getChildrenInHome());
        entity.setChildrenAges(dto.getChildrenAges());
        entity.setPetResponseWithChildren(dto.getPetResponseWithChildren());
        entity.setHomeEnvironment(dto.getHomeEnvironment());
        entity.setHomeEnvironmentOther(dto.getHomeEnvironmentOther());

        // Step 5
        entity.setSuccessOutcome(dto.getSuccessOutcome());
        entity.setOpenToAdjustments(dto.getOpenToAdjustments());
        entity.setPreferredSessionType(dto.getPreferredSessionType());
        entity.setAdditionalNotes(dto.getAdditionalNotes());

        // Consent & initial status
        entity.setConsentAccuracy(dto.getConsentAccuracy());
        entity.setKycStatus(KycStatus.PENDING);

        // ---------- Persist ----------
        return behavioristKycRepo.save(entity);
    }

    // =====================================================
    // 02. DELETE BY UID
    // =====================================================
    @Transactional
    public void deleteByUid(String uid) throws ValidationException {
        UUID uuid;
        try {
            uuid = UUID.fromString(uid);
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("INVALID_UID: KYC UID must be a valid UUID.");
        }

        Optional<PetBehavioristKycEntity> existing = behavioristKycRepo.findByUid(uuid);
        if (existing.isEmpty()) {
            throw new ValidationException("KYC_NOT_FOUND: No behaviorist KYC found for the given UID.");
        }

        behavioristKycRepo.deleteByUid(uuid);
    }

    // =====================================================
    // 03. GET ALL
    // =====================================================

    @Transactional(readOnly = true)
    public List<PetBehavioristKycEntity> getAllKycs() {
        return behavioristKycRepo.findAllByOrderByCreatedAtDesc();
    }
    // =====================================================
    // 04. GET BY UID
    // =====================================================
    @Transactional(readOnly = true)
    public PetBehavioristKycEntity getByUid(String uid) throws ValidationException {
        UUID uuid;
        try {
            uuid = UUID.fromString(uid);
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("INVALID_UID: KYC UID must be a valid UUID.");
        }

        return behavioristKycRepo.findByUid(uuid)
                .orElseThrow(() -> new ValidationException("KYC_NOT_FOUND: No behaviorist KYC found for the given UID."));
    }

    // =====================================================
    // 05. UPDATE STATUS BY UID
    // =====================================================
    @Transactional
    public PetBehavioristKycEntity updateStatusByUid(String uid, String status) throws ValidationException {
        UUID uuid;
        try {
            uuid = UUID.fromString(uid);
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("INVALID_UID: KYC UID must be a valid UUID.");
        }

        // Validate status string -> enum
        KycStatus kycStatus;
        try {
            kycStatus = KycStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            String allowed = Arrays.stream(KycStatus.values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            throw new ValidationException("INVALID_KYC_STATUS: '" + status + "' is not valid. Allowed values: " + allowed);
        }

        // Make sure record exists
        PetBehavioristKycEntity entity = behavioristKycRepo.findByUid(uuid)
                .orElseThrow(() -> new ValidationException("KYC_NOT_FOUND: No behaviorist KYC found for the given UID."));

        entity.setKycStatus(kycStatus);

        // You can either use repo.updateKycStatusByUid(...) or just save:
        // behavioristKycRepo.updateKycStatusByUid(uuid, kycStatus);
        return behavioristKycRepo.save(entity);
    }

    // =====================================================
    // Helpers: Conversion + Allowed values
    // =====================================================

    private List<BehavioralChallenges> convertBehavioralChallenges(List<String> values) {
        if (values == null || values.isEmpty()) {
            throw new ValidationException("BEHAVIORAL_CHALLENGES_REQUIRED: At least one behavioral challenge must be selected.");
        }

        try {
            return values.stream()
                    .map(v -> {
                        if (v == null || v.trim().isEmpty()) {
                            throw new ValidationException("INVALID_BEHAVIORAL_CHALLENGE: Empty value is not allowed.");
                        }
                        return BehavioralChallenges.fromString(v);
                    })
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException ex) {
            String allowed = Arrays.stream(BehavioralChallenges.values())
                    .map(BehavioralChallenges::getLabel)
                    .collect(Collectors.joining(", "));
            throw new ValidationException("INVALID_BEHAVIORAL_CHALLENGE: " + ex.getMessage()
                    + ". Allowed values are: " + allowed);
        }
    }

    private List<AggressiveBehaviors> convertAggressiveBehaviors(List<String> values) {
        if (values == null || values.isEmpty()) {
            return List.of(); // optional field
        }

        try {
            return values.stream()
                    .map(v -> {
                        if (v == null || v.trim().isEmpty()) {
                            throw new ValidationException("INVALID_AGGRESSIVE_BEHAVIOR: Empty value is not allowed.");
                        }
                        return AggressiveBehaviors.fromString(v);
                    })
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException ex) {
            String allowed = Arrays.stream(AggressiveBehaviors.values())
                    .map(AggressiveBehaviors::getLabel)
                    .collect(Collectors.joining(", "));
            throw new ValidationException("INVALID_AGGRESSIVE_BEHAVIOR: " + ex.getMessage()
                    + ". Allowed values are: " + allowed);
        }
    }

    private List<CurrentTrainingTools> convertCurrentTrainingTools(List<String> values) {
        if (values == null || values.isEmpty()) {
            return List.of(); // optional field
        }

        try {
            return values.stream()
                    .map(v -> {
                        if (v == null || v.trim().isEmpty()) {
                            throw new ValidationException("INVALID_TRAINING_TOOL: Empty value is not allowed.");
                        }
                        return CurrentTrainingTools.fromString(v);
                    })
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException ex) {
            String allowed = Arrays.stream(CurrentTrainingTools.values())
                    .map(CurrentTrainingTools::getLabel)
                    .collect(Collectors.joining(", "));
            throw new ValidationException("INVALID_TRAINING_TOOL: " + ex.getMessage()
                    + ". Allowed values are: " + allowed);
        }
    }
}
