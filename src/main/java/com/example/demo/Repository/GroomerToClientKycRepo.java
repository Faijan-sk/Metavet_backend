package com.example.demo.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.Entities.GroomerToClientKycEntity;
import com.example.demo.Entities.GroomerToClientKycEntity.KycStatus;
import com.example.demo.Entities.GroomerToClientKycEntity.HealthCondition;
import com.example.demo.Entities.GroomerToClientKycEntity.BehaviorIssue;
import com.example.demo.Entities.GroomerToClientKycEntity.Service;
import com.example.demo.Entities.GroomerToClientKycEntity.AddOn;
import com.example.demo.Entities.PetsEntity;

@Repository
public interface GroomerToClientKycRepo extends JpaRepository<GroomerToClientKycEntity, Long> {

    // ---------- BaseEntity Related ----------
    Optional<GroomerToClientKycEntity> findByUid(UUID uid);

    List<GroomerToClientKycEntity> findAllByOrderByCreatedAtDesc();

    List<GroomerToClientKycEntity> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // ---------- Entity Specific ----------

    // Find by pet
    Optional<GroomerToClientKycEntity> findByPet(PetsEntity pet);

    List<GroomerToClientKycEntity> findByPetId(Long petId);

    // Find by petUid string
    Optional<GroomerToClientKycEntity> findByPetUid(String petUid);

    // Find by userUid (extracted from token)
    List<GroomerToClientKycEntity> findByUserUid(String userUid);

    // Check if KYC exists for a pet
    boolean existsByPet(PetsEntity pet);

    boolean existsByPetId(Long petId);

    boolean existsByPetUid(String petUid);

    // Find by grooming frequency
    @Query("SELECT g FROM GroomerToClientKycEntity g WHERE LOWER(g.groomingFrequency) = LOWER(:frequency)")
    List<GroomerToClientKycEntity> findByGroomingFrequency(@Param("frequency") String frequency);

    // Find by grooming location preference
    @Query("SELECT g FROM GroomerToClientKycEntity g WHERE LOWER(g.groomingLocation) = LOWER(:location)")
    List<GroomerToClientKycEntity> findByGroomingLocation(@Param("location") String location);

    // Find by appointment date
    List<GroomerToClientKycEntity> findByAppointmentDate(LocalDate appointmentDate);

    // Find appointments within date range
    @Query("SELECT g FROM GroomerToClientKycEntity g WHERE g.appointmentDate BETWEEN :startDate AND :endDate")
    List<GroomerToClientKycEntity> findAppointmentsBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Find KYCs with medication flag
    @Query("SELECT g FROM GroomerToClientKycEntity g WHERE g.onMedication = true")
    List<GroomerToClientKycEntity> findKycsWithMedication();

    // Find KYCs with injury/surgery history
    @Query("SELECT g FROM GroomerToClientKycEntity g WHERE g.hadInjuriesSurgery = true")
    List<GroomerToClientKycEntity> findKycsWithInjurySurgery();

    // ---------- UPDATED: enum based searches for List fields ----------

    // Search by health condition (now searches in List)
    @Query("SELECT g FROM GroomerToClientKycEntity g WHERE :condition MEMBER OF g.healthConditions")
    List<GroomerToClientKycEntity> searchByHealthCondition(@Param("condition") HealthCondition condition);

    // Search by behavior issue (now searches in List)
    @Query("SELECT g FROM GroomerToClientKycEntity g WHERE :behavior MEMBER OF g.behaviorIssues")
    List<GroomerToClientKycEntity> searchByBehaviorIssue(@Param("behavior") BehaviorIssue behavior);

    // Search by service (now searches in List)
    @Query("SELECT g FROM GroomerToClientKycEntity g WHERE :service MEMBER OF g.services")
    List<GroomerToClientKycEntity> searchByService(@Param("service") Service service);

    // Search by add-on (searches in List)
    @Query("SELECT g FROM GroomerToClientKycEntity g WHERE :addon MEMBER OF g.addOns")
    List<GroomerToClientKycEntity> searchByAddOn(@Param("addon") AddOn addon);

    // Find recent submissions
    @Query("SELECT g FROM GroomerToClientKycEntity g ORDER BY g.createdAt DESC")
    List<GroomerToClientKycEntity> findRecentSubmissions();

    // Find upcoming appointments
    @Query("SELECT g FROM GroomerToClientKycEntity g WHERE g.appointmentDate >= :today ORDER BY g.appointmentDate ASC, g.appointmentTime ASC")
    List<GroomerToClientKycEntity> findUpcomingAppointments(@Param("today") LocalDate today);

    // ---------- Status Related Queries ----------
    
    // Find by status
    List<GroomerToClientKycEntity> findByStatus(KycStatus status);
    
    // Find by user UID and status
    List<GroomerToClientKycEntity> findByUserUidAndStatus(String userUid, KycStatus status);
    
    // Count by status
    @Query("SELECT COUNT(g) FROM GroomerToClientKycEntity g WHERE g.status = :status")
    long countByStatus(@Param("status") KycStatus status);
}