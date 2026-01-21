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

import com.example.demo.Entities.WalkerKyc;
import com.example.demo.Entities.WalkerKyc.ApplicationStatus;

@Repository
public interface WalkerKycRepo extends JpaRepository<WalkerKyc, Long> {
    
    // ---------- BaseEntity Related ----------
    Optional<WalkerKyc> findByUid(UUID uid);
    List<WalkerKyc> findAllByOrderByCreatedAtDesc();
    List<WalkerKyc> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // ---------- Entity Specific ----------
    Optional<WalkerKyc> findByEmail(String email);
    boolean existsByEmail(String email);
    List<WalkerKyc> findByStatus(ApplicationStatus status);
    
    // Search by partial name or business name
    @Query("SELECT w FROM WalkerKyc w WHERE LOWER(w.fullLegalName) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(w.businessName) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<WalkerKyc> searchByNameOrBusiness(@Param("q") String query);
    
    // Recently added applications (latest first)
    @Query("SELECT w FROM WalkerKyc w ORDER BY w.createdAt DESC")
    List<WalkerKyc> findRecentApplications();
    
    // Applications by status (ordered by creation date)
    @Query("SELECT w FROM WalkerKyc w WHERE w.status = :status ORDER BY w.createdAt DESC")
    List<WalkerKyc> findByStatusOrderByCreatedAtDesc(@Param("status") ApplicationStatus status);
    
    // ⭐ NEW: Distance-based query with filters
    @Query(value = """
        SELECT wk.* FROM metavet_to_walker_kyc wk
        JOIN service_provider sp ON wk.service_provider_uid = sp.uid
        JOIN users_entity u ON wk.user_uid = u.uid
        WHERE wk.status = 'APPROVED'
        AND u.is_profile_deleted = false
        AND (6371 * acos(
            cos(radians(:userLat)) * cos(radians(CAST(wk.latitude AS DOUBLE PRECISION))) *
            cos(radians(CAST(wk.longitude AS DOUBLE PRECISION)) - radians(:userLon)) +
            sin(radians(:userLat)) * sin(radians(CAST(wk.latitude AS DOUBLE PRECISION)))
        )) <= :maxDistance
        AND (:searchTerm IS NULL OR LOWER(u.first_name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            OR LOWER(u.last_name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            OR LOWER(wk.business_name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
        AND (:serviceArea IS NULL OR LOWER(wk.service_area) LIKE LOWER(CONCAT('%', :serviceArea, '%')))
        ORDER BY (6371 * acos(
            cos(radians(:userLat)) * cos(radians(CAST(wk.latitude AS DOUBLE PRECISION))) *
            cos(radians(CAST(wk.longitude AS DOUBLE PRECISION)) - radians(:userLon)) +
            sin(radians(:userLat)) * sin(radians(CAST(wk.latitude AS DOUBLE PRECISION)))
        )) ASC
        """, nativeQuery = true)
    Page<WalkerKyc> findWalkersWithinDistance(
        @Param("userLat") Double userLat,
        @Param("userLon") Double userLon,
        @Param("maxDistance") Double maxDistance,
        @Param("searchTerm") String searchTerm,
        @Param("serviceArea") String serviceArea,
        Pageable pageable
    );
}