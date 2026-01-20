package com.example.demo.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Dto.GroomerToClientKycRequestDto;
import com.example.demo.Entities.GroomerToClientKycEntity;
import com.example.demo.Entities.GroomerToClientKycEntity.KycStatus;
import com.example.demo.Entities.GroomerToClientKycEntity.HealthCondition;
import com.example.demo.Entities.GroomerToClientKycEntity.BehaviorIssue;
import com.example.demo.Entities.GroomerToClientKycEntity.AddOn;
import com.example.demo.Entities.PetsEntity;
import com.example.demo.Entities.UsersEntity;
import com.example.demo.Repository.GroomerToClientKycRepo;
import com.example.demo.Repository.PetRepo;
import com.example.demo.Repository.UserRepo;

import jakarta.validation.ValidationException;

@Service
public class GroomerToClientKycService {

    @Autowired
    private GroomerToClientKycRepo groomerKycRepo;

    @Autowired
    private PetRepo petsRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JwtService jwtService;

    // ==================== CREATE ====================
    @Transactional
    public GroomerToClientKycEntity createGroomerKyc(GroomerToClientKycRequestDto dto, String accessToken) throws ValidationException {

        GroomerToClientKycEntity kyc = new GroomerToClientKycEntity();

        // ==================== Extract User UID from Access Token ====================
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new ValidationException("ACCESS_TOKEN_REQUIRED: Authorization token is mandatory for this operation.");
        }

        try {
            String token = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
            UUID userUuid = jwtService.extractUserIdAsUUID(token, true);

            if (userUuid == null) {
                throw new ValidationException("INVALID_TOKEN: User ID could not be extracted from the provided token.");
            }

            Optional<UsersEntity> userOpt = userRepo.findByUid(userUuid);
            if (!userOpt.isPresent()) {
                throw new ValidationException("USER_NOT_FOUND: No user exists with the ID from the provided token.");
            }

            kyc.setUserUid(userUuid.toString());

        } catch (ValidationException ve) {
            throw ve;
        } catch (Exception e) {
            throw new ValidationException("TOKEN_EXTRACTION_FAILED: Unable to process authentication token - " + e.getMessage());
        }

        // ==================== Pet Mapping via UUID ====================
        if (dto.getPetUid() == null || dto.getPetUid().trim().isEmpty()) {
            throw new ValidationException("PET_UID_REQUIRED: Pet UID is mandatory. Please provide a valid UUID in 'petUid' field.");
        }

        try {
            UUID petUuid = UUID.fromString(dto.getPetUid());
            Optional<PetsEntity> petOpt = petsRepo.findByUid(petUuid);

            if (!petOpt.isPresent()) {
                throw new ValidationException("PET_NOT_FOUND: No pet exists with UID '" + dto.getPetUid() + "'. Please verify the pet UID.");
            }

            kyc.setPet(petOpt.get());
            kyc.setPetUid(dto.getPetUid());

        } catch (IllegalArgumentException e) {
            throw new ValidationException("INVALID_PET_UID_FORMAT: Pet UID must be in valid UUID format (e.g., '550e8400-e29b-41d4-a716-446655440000'). Received: '" + dto.getPetUid() + "'");
        } catch (ValidationException ve) {
            throw ve;
        }

        // Set default status as PENDING
        kyc.setStatus(GroomerToClientKycEntity.KycStatus.PENDING);

        // ==================== Step 1: Grooming Preferences ====================
        if (dto.getGroomingFrequency() == null || dto.getGroomingFrequency().trim().isEmpty()) {
            throw new ValidationException("GROOMING_FREQUENCY_REQUIRED: Grooming frequency is mandatory.");
        }
        if (dto.getGroomingFrequency().length() > 100) {
            throw new ValidationException("GROOMING_FREQUENCY_TOO_LONG: Grooming frequency cannot exceed 100 characters. Current length: " + dto.getGroomingFrequency().length());
        }
        kyc.setGroomingFrequency(dto.getGroomingFrequency());

        kyc.setLastGroomingDate(dto.getLastGroomingDate());

        if (dto.getPreferredStyle() != null && dto.getPreferredStyle().length() > 500) {
            throw new ValidationException("PREFERRED_STYLE_TOO_LONG: Preferred style cannot exceed 500 characters. Current length: " + dto.getPreferredStyle().length());
        }
        kyc.setPreferredStyle(dto.getPreferredStyle());

        if (dto.getAvoidFocusAreas() != null && dto.getAvoidFocusAreas().length() > 1000) {
            throw new ValidationException("AVOID_FOCUS_AREAS_TOO_LONG: Avoid focus areas cannot exceed 1000 characters. Current length: " + dto.getAvoidFocusAreas().length());
        }
        kyc.setAvoidFocusAreas(dto.getAvoidFocusAreas());

        // ==================== Step 2: Health & Safety ====================
        if (dto.getHealthConditions() == null || dto.getHealthConditions().isEmpty()) {
            throw new ValidationException("HEALTH_CONDITION_REQUIRED: At least one health condition is mandatory.");
        }
        kyc.setHealthConditions(dto.getHealthConditions());

        if (dto.getHealthConditions().contains(HealthCondition.OTHER)) {
            if (dto.getOtherHealthCondition() == null || dto.getOtherHealthCondition().trim().isEmpty()) {
                throw new ValidationException("OTHER_HEALTH_CONDITION_REQUIRED: Please specify the health condition in 'otherHealthCondition' field when selecting 'OTHER'.");
            }
            if (dto.getOtherHealthCondition().length() > 300) {
                throw new ValidationException("OTHER_HEALTH_CONDITION_TOO_LONG: Other health condition cannot exceed 300 characters. Current length: " + dto.getOtherHealthCondition().length());
            }
            kyc.setOtherHealthCondition(dto.getOtherHealthCondition());
        } else {
            kyc.setOtherHealthCondition(dto.getOtherHealthCondition());
        }

        if (dto.getOnMedication() == null) {
            throw new ValidationException("MEDICATION_STATUS_REQUIRED: Medication status is mandatory. Please provide true or false in 'onMedication' field.");
        }
        kyc.setOnMedication(dto.getOnMedication());

        if (dto.getOnMedication()) {
            if (dto.getMedicationDetails() == null || dto.getMedicationDetails().trim().isEmpty()) {
                throw new ValidationException("MEDICATION_DETAILS_REQUIRED: Medication details are required in 'medicationDetails' field when 'onMedication' is true.");
            }
            if (dto.getMedicationDetails().length() > 1000) {
                throw new ValidationException("MEDICATION_DETAILS_TOO_LONG: Medication details cannot exceed 1000 characters. Current length: " + dto.getMedicationDetails().length());
            }
            kyc.setMedicationDetails(dto.getMedicationDetails());
        } else {
            kyc.setMedicationDetails(dto.getMedicationDetails());
        }

        if (dto.getHadInjuriesSurgery() == null) {
            throw new ValidationException("INJURY_SURGERY_STATUS_REQUIRED: Injury/surgery history is mandatory. Please provide true or false in 'hadInjuriesSurgery' field.");
        }
        kyc.setHadInjuriesSurgery(dto.getHadInjuriesSurgery());

        if (dto.getHadInjuriesSurgery()) {
            if (dto.getInjurySurgeryDetails() == null || dto.getInjurySurgeryDetails().trim().isEmpty()) {
                throw new ValidationException("INJURY_SURGERY_DETAILS_REQUIRED: Injury or surgery details are required in 'injurySurgeryDetails' field when 'hadInjuriesSurgery' is true.");
            }
            if (dto.getInjurySurgeryDetails().length() > 1000) {
                throw new ValidationException("INJURY_SURGERY_DETAILS_TOO_LONG: Injury/surgery details cannot exceed 1000 characters. Current length: " + dto.getInjurySurgeryDetails().length());
            }
            kyc.setInjurySurgeryDetails(dto.getInjurySurgeryDetails());
        } else {
            kyc.setInjurySurgeryDetails(dto.getInjurySurgeryDetails());
        }

        // ==================== Step 3: Behavior & Handling ====================
        if (dto.getBehaviorIssues() == null || dto.getBehaviorIssues().isEmpty()) {
            throw new ValidationException("BEHAVIOR_ISSUE_REQUIRED: At least one behavior issue is mandatory.");
        }
        kyc.setBehaviorIssues(dto.getBehaviorIssues());

        if (dto.getCalmingMethods() != null && dto.getCalmingMethods().length() > 1000) {
            throw new ValidationException("CALMING_METHODS_TOO_LONG: Calming methods cannot exceed 1000 characters. Current length: " + dto.getCalmingMethods().length());
        }
        kyc.setCalmingMethods(dto.getCalmingMethods());

        if (dto.getTriggers() != null && dto.getTriggers().length() > 1000) {
            throw new ValidationException("TRIGGERS_TOO_LONG: Triggers cannot exceed 1000 characters. Current length: " + dto.getTriggers().length());
        }
        kyc.setTriggers(dto.getTriggers());

        // ==================== Step 4: Services & Scheduling ====================
        if (dto.getServices() == null || dto.getServices().isEmpty()) {
            throw new ValidationException("SERVICE_REQUIRED: At least one service is mandatory.");
        }
        kyc.setServices(dto.getServices());

        if (dto.getServices().contains(GroomerToClientKycEntity.Service.OTHER)) {
            if (dto.getOtherService() == null || dto.getOtherService().trim().isEmpty()) {
                throw new ValidationException("OTHER_SERVICE_REQUIRED: Please specify the service in 'otherService' field when selecting 'OTHER' in services.");
            }
            if (dto.getOtherService().length() > 300) {
                throw new ValidationException("OTHER_SERVICE_TOO_LONG: Other service cannot exceed 300 characters. Current length: " + dto.getOtherService().length());
            }
            kyc.setOtherService(dto.getOtherService());
        } else {
            kyc.setOtherService(dto.getOtherService());
        }

        if (dto.getGroomingLocation() == null || dto.getGroomingLocation().trim().isEmpty()) {
            throw new ValidationException("GROOMING_LOCATION_REQUIRED: Grooming location preference is mandatory.");
        }
        kyc.setGroomingLocation(dto.getGroomingLocation());

        kyc.setAppointmentDate(dto.getAppointmentDate());
        kyc.setAppointmentTime(dto.getAppointmentTime());

        if (dto.getAdditionalNotes() != null && dto.getAdditionalNotes().length() > 2000) {
            throw new ValidationException("ADDITIONAL_NOTES_TOO_LONG: Additional notes cannot exceed 2000 characters. Current length: " + dto.getAdditionalNotes().length());
        }
        kyc.setAdditionalNotes(dto.getAdditionalNotes());

        // ==================== Add-ons Mapping ====================
        if (dto.getAddOns() != null && !dto.getAddOns().isEmpty()) {
            kyc.setAddOns(dto.getAddOns());
        } else {
            kyc.setAddOns(null);
        }

        return groomerKycRepo.save(kyc);
    }

    // ==================== UPDATE ====================
    @Transactional
    public GroomerToClientKycEntity updateGroomerKyc(Long kycId, GroomerToClientKycRequestDto dto, String accessToken) throws ValidationException {

        Optional<GroomerToClientKycEntity> existingKycOpt = groomerKycRepo.findById(kycId);

        if (!existingKycOpt.isPresent()) {
            throw new ValidationException("KYC_NOT_FOUND: Groomer KYC with ID " + kycId + " does not exist in the database.");
        }

        GroomerToClientKycEntity kyc = existingKycOpt.get();

        // ==================== Verify User UID from Access Token ====================
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new ValidationException("ACCESS_TOKEN_REQUIRED: Authorization token is mandatory for this operation.");
        }

        try {
            String token = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
            UUID userUuid = jwtService.extractUserIdAsUUID(token, true);

            if (userUuid == null) {
                throw new ValidationException("INVALID_TOKEN: User ID could not be extracted from the provided token.");
            }

            if (!kyc.getUserUid().equals(userUuid.toString())) {
                throw new ValidationException("UNAUTHORIZED_ACCESS: You are not authorized to update this KYC record. This record belongs to a different user.");
            }

        } catch (ValidationException ve) {
            throw ve;
        } catch (Exception e) {
            throw new ValidationException("TOKEN_VERIFICATION_FAILED: Unable to verify authentication token - " + e.getMessage());
        }

        // ==================== Pet Mapping via UUID ====================
        if (dto.getPetUid() != null && !dto.getPetUid().trim().isEmpty()) {
            try {
                UUID petUuid = UUID.fromString(dto.getPetUid());
                Optional<PetsEntity> petOpt = petsRepo.findByUid(petUuid);

                if (!petOpt.isPresent()) {
                    throw new ValidationException("PET_NOT_FOUND: No pet exists with UID '" + dto.getPetUid() + "'. Please verify the pet UID.");
                }

                kyc.setPet(petOpt.get());
                kyc.setPetUid(dto.getPetUid());

            } catch (IllegalArgumentException e) {
                throw new ValidationException("INVALID_PET_UID_FORMAT: Pet UID must be in valid UUID format. Received: '" + dto.getPetUid() + "'");
            } catch (ValidationException ve) {
                throw ve;
            }
        }

        // ==================== Step 1: Grooming Preferences ====================
        if (dto.getGroomingFrequency() != null && !dto.getGroomingFrequency().trim().isEmpty()) {
            if (dto.getGroomingFrequency().length() > 100) {
                throw new ValidationException("GROOMING_FREQUENCY_TOO_LONG: Grooming frequency cannot exceed 100 characters. Current length: " + dto.getGroomingFrequency().length());
            }
            kyc.setGroomingFrequency(dto.getGroomingFrequency());
        }

        if (dto.getLastGroomingDate() != null) {
            kyc.setLastGroomingDate(dto.getLastGroomingDate());
        }

        if (dto.getPreferredStyle() != null && dto.getPreferredStyle().length() > 500) {
            throw new ValidationException("PREFERRED_STYLE_TOO_LONG: Preferred style cannot exceed 500 characters. Current length: " + dto.getPreferredStyle().length());
        }
        if (dto.getPreferredStyle() != null) {
            kyc.setPreferredStyle(dto.getPreferredStyle());
        }

        if (dto.getAvoidFocusAreas() != null && dto.getAvoidFocusAreas().length() > 1000) {
            throw new ValidationException("AVOID_FOCUS_AREAS_TOO_LONG: Avoid focus areas cannot exceed 1000 characters. Current length: " + dto.getAvoidFocusAreas().length());
        }
        if (dto.getAvoidFocusAreas() != null) {
            kyc.setAvoidFocusAreas(dto.getAvoidFocusAreas());
        }

        // ==================== Step 2: Health & Safety ====================
        if (dto.getHealthConditions() != null && !dto.getHealthConditions().isEmpty()) {
            kyc.setHealthConditions(dto.getHealthConditions());

            if (dto.getHealthConditions().contains(HealthCondition.OTHER)) {
                if (dto.getOtherHealthCondition() == null || dto.getOtherHealthCondition().trim().isEmpty()) {
                    throw new ValidationException("OTHER_HEALTH_CONDITION_REQUIRED: Please specify the health condition in 'otherHealthCondition' field when selecting 'OTHER'.");
                }
                if (dto.getOtherHealthCondition().length() > 300) {
                    throw new ValidationException("OTHER_HEALTH_CONDITION_TOO_LONG: Other health condition cannot exceed 300 characters. Current length: " + dto.getOtherHealthCondition().length());
                }
                kyc.setOtherHealthCondition(dto.getOtherHealthCondition());
            } else if (dto.getOtherHealthCondition() != null) {
                if (dto.getOtherHealthCondition().length() > 300) {
                    throw new ValidationException("OTHER_HEALTH_CONDITION_TOO_LONG: Other health condition cannot exceed 300 characters. Current length: " + dto.getOtherHealthCondition().length());
                }
                kyc.setOtherHealthCondition(dto.getOtherHealthCondition());
            }
        }

        if (dto.getOnMedication() != null) {
            kyc.setOnMedication(dto.getOnMedication());

            if (dto.getOnMedication()) {
                if (dto.getMedicationDetails() == null || dto.getMedicationDetails().trim().isEmpty()) {
                    throw new ValidationException("MEDICATION_DETAILS_REQUIRED: Medication details are required when 'onMedication' is true.");
                }
                if (dto.getMedicationDetails().length() > 1000) {
                    throw new ValidationException("MEDICATION_DETAILS_TOO_LONG: Medication details cannot exceed 1000 characters. Current length: " + dto.getMedicationDetails().length());
                }
                kyc.setMedicationDetails(dto.getMedicationDetails());
            } else if (dto.getMedicationDetails() != null) {
                if (dto.getMedicationDetails().length() > 1000) {
                    throw new ValidationException("MEDICATION_DETAILS_TOO_LONG: Medication details cannot exceed 1000 characters. Current length: " + dto.getMedicationDetails().length());
                }
                kyc.setMedicationDetails(dto.getMedicationDetails());
            }
        }

        if (dto.getHadInjuriesSurgery() != null) {
            kyc.setHadInjuriesSurgery(dto.getHadInjuriesSurgery());

            if (dto.getHadInjuriesSurgery()) {
                if (dto.getInjurySurgeryDetails() == null || dto.getInjurySurgeryDetails().trim().isEmpty()) {
                    throw new ValidationException("INJURY_SURGERY_DETAILS_REQUIRED: Injury or surgery details are required when 'hadInjuriesSurgery' is true.");
                }
                if (dto.getInjurySurgeryDetails().length() > 1000) {
                    throw new ValidationException("INJURY_SURGERY_DETAILS_TOO_LONG: Injury/surgery details cannot exceed 1000 characters. Current length: " + dto.getInjurySurgeryDetails().length());
                }
                kyc.setInjurySurgeryDetails(dto.getInjurySurgeryDetails());
            } else if (dto.getInjurySurgeryDetails() != null) {
                if (dto.getInjurySurgeryDetails().length() > 1000) {
                    throw new ValidationException("INJURY_SURGERY_DETAILS_TOO_LONG: Injury/surgery details cannot exceed 1000 characters. Current length: " + dto.getInjurySurgeryDetails().length());
                }
                kyc.setInjurySurgeryDetails(dto.getInjurySurgeryDetails());
            }
        }

        // ==================== Step 3: Behavior & Handling ====================
        if (dto.getBehaviorIssues() != null && !dto.getBehaviorIssues().isEmpty()) {
            kyc.setBehaviorIssues(dto.getBehaviorIssues());
        }

        if (dto.getCalmingMethods() != null && dto.getCalmingMethods().length() > 1000) {
            throw new ValidationException("CALMING_METHODS_TOO_LONG: Calming methods cannot exceed 1000 characters. Current length: " + dto.getCalmingMethods().length());
        }
        if (dto.getCalmingMethods() != null) {
            kyc.setCalmingMethods(dto.getCalmingMethods());
        }

        if (dto.getTriggers() != null && dto.getTriggers().length() > 1000) {
            throw new ValidationException("TRIGGERS_TOO_LONG: Triggers cannot exceed 1000 characters. Current length: " + dto.getTriggers().length());
        }
        if (dto.getTriggers() != null) {
            kyc.setTriggers(dto.getTriggers());
        }

        // ==================== Step 4: Services & Scheduling ====================
        if (dto.getServices() != null && !dto.getServices().isEmpty()) {
            kyc.setServices(dto.getServices());

            if (dto.getServices().contains(GroomerToClientKycEntity.Service.OTHER)) {
                if (dto.getOtherService() == null || dto.getOtherService().trim().isEmpty()) {
                    throw new ValidationException("OTHER_SERVICE_REQUIRED: Please specify the service in 'otherService' field when selecting 'OTHER'.");
                }
                if (dto.getOtherService().length() > 300) {
                    throw new ValidationException("OTHER_SERVICE_TOO_LONG: Other service cannot exceed 300 characters. Current length: " + dto.getOtherService().length());
                }
                kyc.setOtherService(dto.getOtherService());
            } else if (dto.getOtherService() != null) {
                if (dto.getOtherService().length() > 300) {
                    throw new ValidationException("OTHER_SERVICE_TOO_LONG: Other service cannot exceed 300 characters. Current length: " + dto.getOtherService().length());
                }
                kyc.setOtherService(dto.getOtherService());
            }
        }

        if (dto.getGroomingLocation() != null && !dto.getGroomingLocation().trim().isEmpty()) {
            if (dto.getGroomingLocation().length() > 100) {
                throw new ValidationException("GROOMING_LOCATION_TOO_LONG: Grooming location cannot exceed 100 characters. Current length: " + dto.getGroomingLocation().length());
            }
            kyc.setGroomingLocation(dto.getGroomingLocation());
        }

        if (dto.getAppointmentDate() != null) {
            kyc.setAppointmentDate(dto.getAppointmentDate());
        }

        if (dto.getAppointmentTime() != null) {
            kyc.setAppointmentTime(dto.getAppointmentTime());
        }

        if (dto.getAdditionalNotes() != null && dto.getAdditionalNotes().length() > 2000) {
            throw new ValidationException("ADDITIONAL_NOTES_TOO_LONG: Additional notes cannot exceed 2000 characters. Current length: " + dto.getAdditionalNotes().length());
        }
        if (dto.getAdditionalNotes() != null) {
            kyc.setAdditionalNotes(dto.getAdditionalNotes());
        }

        // ==================== Add-ons Mapping ====================
        if (dto.getAddOns() != null) {
            kyc.setAddOns(dto.getAddOns());
        }

        return groomerKycRepo.save(kyc);
    }

    // ==================== Get All KYCs ====================
    public List<GroomerToClientKycEntity> getAllGroomerKycs() {
        return groomerKycRepo.findAllByOrderByCreatedAtDesc();
    }

    // ==================== Get KYC by UID ====================
    public Optional<GroomerToClientKycEntity> getGroomerKycByUid(String uid) throws ValidationException {
        try {
            UUID uuid = UUID.fromString(uid);
            return groomerKycRepo.findByUid(uuid);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("INVALID_UID_FORMAT: UID must be in valid UUID format. Received: '" + uid + "'");
        }
    }

    // ==================== Get Status by UID ====================
    public KycStatus getKycStatusByUid(String uid) throws ValidationException {
        Optional<GroomerToClientKycEntity> kycOpt = getGroomerKycByUid(uid);

        if (!kycOpt.isPresent()) {
            throw new ValidationException("KYC_NOT_FOUND: KYC with UID '" + uid + "' does not exist in the database.");
        }

        return kycOpt.get().getStatus();
    }

    // ==================== Update Status by UID ====================
//    @Transactional
//    public GroomerToClientKycEntity updateKycStatusByUid(String uid, String status) throws ValidationException {
//        Optional<GroomerToClientKycEntity> kycOpt = getGroomerKycByUid(uid);
//
//        if (!kycOpt.isPresent()) {
//            throw new ValidationException("KYC_NOT_FOUND: KYC with UID '" + uid + "' does not exist in the database.");
//        }
//
//        GroomerToClientKycEntity kyc = kycOpt.get();
//
//        if (status == null || status.trim().isEmpty()) {
//            throw new ValidationException("STATUS_REQUIRED: Status field is mandatory. Allowed values: 'PENDING', 'APPROVED', 'REJECTED'");
//        }
//
//        try {
//            KycStatus newStatus = KycStatus.valueOf(status.toUpperCase());
//            KycStatus oldStatus = kyc.getStatus();
//            kyc.setStatus(newStatus);
//
//            GroomerToClientKycEntity updatedKyc = groomerKycRepo.save(kyc);
//
//            // Log status change for audit
//            System.out.println("KYC Status Updated - UID: " + uid + ", From: " + oldStatus + ", To: " + newStatus);
//
//            return updatedKyc;
//
//        } catch (IllegalArgumentException e) {
//            throw new ValidationException("INVALID_STATUS_VALUE: Status must be one of: 'PENDING', 'APPROVED', 'REJECTED'. Received: '" + status + "'");
//        }
//    }
    
    @Transactional
    public GroomerToClientKycEntity updateKycStatusByUid(String uid, String status) throws ValidationException {
        Optional<GroomerToClientKycEntity> kycOpt = getGroomerKycByUid(uid);
        if (!kycOpt.isPresent()) {
            throw new ValidationException("KYC_NOT_FOUND: ...");
        }

        GroomerToClientKycEntity kyc = kycOpt.get();

        if (status == null || status.trim().isEmpty()) {
            throw new ValidationException("STATUS_REQUIRED: ...");
        }

        try {
            KycStatus newStatus = KycStatus.valueOf(status.toUpperCase());
            KycStatus oldStatus = kyc.getStatus();

            // Use repository bulk update to avoid full-entity validation
            UUID uuid = UUID.fromString(uid);
            int updated = groomerKycRepo.updateStatusByUid(uuid, newStatus);
            if (updated == 0) {
                throw new ValidationException("KYC_NOT_FOUND: Unable to update status for UID: " + uid);
            }

            // re-fetch updated entity to return full record
            return groomerKycRepo.findByUid(uuid).orElseThrow(() -> new ValidationException("KYC_NOT_FOUND_AFTER_UPDATE"));
        } catch (IllegalArgumentException e) {
            throw new ValidationException("INVALID_STATUS_VALUE: ...");
        }
    }

    
    

    // ==================== Delete KYC by UID ====================
    @Transactional
    public void deleteGroomerKycByUid(String uid) throws ValidationException {
        Optional<GroomerToClientKycEntity> kycOpt = getGroomerKycByUid(uid);

        if (!kycOpt.isPresent()) {
            throw new ValidationException("KYC_NOT_FOUND: KYC with UID '" + uid + "' does not exist in the database.");
        }

        groomerKycRepo.delete(kycOpt.get());
    }

    // ==================== Existing Methods ====================
    public Optional<GroomerToClientKycEntity> getGroomerKycById(Long id) {
        return groomerKycRepo.findById(id);
    }

    public Optional<GroomerToClientKycEntity> getGroomerKycByPetId(Long petId) {
        return groomerKycRepo.findByPetId(petId).stream().findFirst();
    }

    public Optional<GroomerToClientKycEntity> getGroomerKycByPetUid(String petUid) {
        return groomerKycRepo.findByPetUid(petUid);
    }

    @Transactional
    public void deleteGroomerKyc(Long id, String accessToken) throws ValidationException {

        Optional<GroomerToClientKycEntity> kycOpt = groomerKycRepo.findById(id);

        if (!kycOpt.isPresent()) {
            throw new ValidationException("KYC_NOT_FOUND: Groomer KYC with ID " + id + " does not exist in the database.");
        }

        GroomerToClientKycEntity kyc = kycOpt.get();

        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new ValidationException("ACCESS_TOKEN_REQUIRED: Authorization token is mandatory for this operation.");
        }

        try {
            String token = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
            UUID userUuid = jwtService.extractUserIdAsUUID(token, true);

            if (userUuid == null) {
                throw new ValidationException("INVALID_TOKEN: User ID could not be extracted from the provided token.");
            }

            if (!kyc.getUserUid().equals(userUuid.toString())) {
                throw new ValidationException("UNAUTHORIZED_ACCESS: You are not authorized to delete this KYC record. This record belongs to a different user.");
            }

        } catch (ValidationException ve) {
            throw ve;
        } catch (Exception e) {
            throw new ValidationException("TOKEN_VERIFICATION_FAILED: Unable to verify authentication token - " + e.getMessage());
        }

        groomerKycRepo.deleteById(id);
    }
    
 // ==================== Get KYC by User UID ====================
    public Optional<GroomerToClientKycEntity> getGroomerKycByUserUid(String userUid) {
        return groomerKycRepo.findFirstByUserUid(userUid);
    }
    
    
}