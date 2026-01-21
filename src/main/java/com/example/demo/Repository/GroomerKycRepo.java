package com.example.demo.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.Entities.GroomerKyc;
import com.example.demo.Entities.GroomerKyc.ApplicationStatus;
import com.example.demo.Entities.GroomerKyc.ServiceLocationType;

@Repository
public interface GroomerKycRepo extends JpaRepository<GroomerKyc, Long> {

    // ---------- BaseEntity Related ----------
    Optional<GroomerKyc> findByUid(UUID uid);

    List<GroomerKyc> findAllByOrderByCreatedAtDesc();

    List<GroomerKyc> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // ---------- Entity Specific ----------
    Optional<GroomerKyc> findByEmail(String email);

    boolean existsByEmail(String email);

    List<GroomerKyc> findByStatus(ApplicationStatus status);

    List<GroomerKyc> findByServiceLocationType(ServiceLocationType serviceLocationType);

    // Search by partial name or business name
    @Query("SELECT g FROM GroomerKyc g WHERE LOWER(g.fullLegalName) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(g.businessName) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<GroomerKyc> searchByNameOrBusiness(@Param("q") String query);

    // Recently added applications (latest first)
    @Query("SELECT g FROM GroomerKyc g ORDER BY g.createdAt DESC")
    List<GroomerKyc> findRecentApplications();

    // Applications by status (ordered by creation date)
    @Query("SELECT g FROM GroomerKyc g WHERE g.status = :status ORDER BY g.createdAt DESC")
    List<GroomerKyc> findByStatusOrderByCreatedAtDesc(@Param("status") ApplicationStatus status);

    GroomerKyc findByUserUid(UUID uid);

    // Distance-based query with filters (FIXED RETURN TYPE)
    @Query(value = """
        SELECT gk.* FROM groomer_kyc gk
        JOIN service_provider sp ON gk.service_provider_uid = sp.uid
        JOIN users_entity u ON gk.user_uid = u.uid
        WHERE gk.status = 'APPROVED'
        AND u.is_profile_deleted = false
        AND (6371 * acos(
            cos(radians(:userLat)) * cos(radians(CAST(gk.latitude AS DOUBLE PRECISION))) *
            cos(radians(CAST(gk.longitude AS DOUBLE PRECISION)) - radians(:userLon)) +
            sin(radians(:userLat)) * sin(radians(CAST(gk.latitude AS DOUBLE PRECISION)))
        )) <= :maxDistance
        AND (:searchTerm IS NULL OR LOWER(u.first_name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            OR LOWER(u.last_name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            OR LOWER(gk.business_name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
        AND (:serviceType IS NULL OR CAST(gk.services_offered AS TEXT) LIKE CONCAT('%', :serviceType, '%'))
        ORDER BY (6371 * acos(
            cos(radians(:userLat)) * cos(radians(CAST(gk.latitude AS DOUBLE PRECISION))) *
            cos(radians(CAST(gk.longitude AS DOUBLE PRECISION)) - radians(:userLon)) +
            sin(radians(:userLat)) * sin(radians(CAST(gk.latitude AS DOUBLE PRECISION)))
        )) ASC
        """, nativeQuery = true)
    Page<GroomerKyc> findGroomersWithinDistance(
        @Param("userLat") Double userLat,
        @Param("userLon") Double userLon,
        @Param("maxDistance") Double maxDistance,
        @Param("searchTerm") String searchTerm,
        @Param("serviceType") String serviceType,
        Pageable pageable
    );
}