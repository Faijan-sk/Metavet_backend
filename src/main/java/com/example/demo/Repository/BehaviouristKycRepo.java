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

import com.example.demo.Entities.BehaviouristKyc;
import com.example.demo.Entities.BehaviouristKyc.ApprovalStatus;

@Repository
public interface BehaviouristKycRepo extends JpaRepository<BehaviouristKyc, Long> {
    
    // ---------- BaseEntity Related ----------
    Optional<BehaviouristKyc> findByUid(UUID uid);
    List<BehaviouristKyc> findAllByOrderByCreatedAtDesc();
    List<BehaviouristKyc> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // ---------- Entity Specific ----------
    Optional<BehaviouristKyc> findByEmail(String email);
    boolean existsByEmail(String email);
    List<BehaviouristKyc> findByStatus(ApprovalStatus status);
    
    // Search by partial name or business name
    @Query("SELECT b FROM BehaviouristKyc b WHERE LOWER(b.fullLegalName) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(b.businessName) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<BehaviouristKyc> searchByNameOrBusiness(@Param("q") String query);
    
    // Recently added applications (latest first)
    @Query("SELECT b FROM BehaviouristKyc b ORDER BY b.createdAt DESC")
    List<BehaviouristKyc> findRecentApplications();
    
    // Applications by status (ordered by creation date)
    @Query("SELECT b FROM BehaviouristKyc b WHERE b.status = :status ORDER BY b.createdAt DESC")
    List<BehaviouristKyc> findByStatusOrderByCreatedAtDesc(@Param("status") ApprovalStatus status);
    
    // Find by service area
    @Query("SELECT b FROM BehaviouristKyc b WHERE LOWER(b.serviceArea) LIKE LOWER(CONCAT('%', :area, '%'))")
    List<BehaviouristKyc> findByServiceArea(@Param("area") String serviceArea);
    
    // ⭐ NEW: Distance-based query with filters
    @Query(value = """
        SELECT bk.* FROM behaviourist_kyc bk
        JOIN service_provider sp ON bk.service_provider_uid = sp.uid
        JOIN users_entity u ON bk.user_uid = u.uid
        WHERE bk.status = 'APPROVED'
        AND u.is_profile_deleted = false
        AND (6371 * acos(
            cos(radians(:userLat)) * cos(radians(CAST(bk.latitude AS DOUBLE PRECISION))) *
            cos(radians(CAST(bk.longitude AS DOUBLE PRECISION)) - radians(:userLon)) +
            sin(radians(:userLat)) * sin(radians(CAST(bk.latitude AS DOUBLE PRECISION)))
        )) <= :maxDistance
        AND (:searchTerm IS NULL OR LOWER(u.first_name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            OR LOWER(u.last_name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            OR LOWER(bk.business_name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
        AND (:serviceType IS NULL OR CAST(bk.services_offered AS TEXT) LIKE CONCAT('%', :serviceType, '%'))
        ORDER BY (6371 * acos(
            cos(radians(:userLat)) * cos(radians(CAST(bk.latitude AS DOUBLE PRECISION))) *
            cos(radians(CAST(bk.longitude AS DOUBLE PRECISION)) - radians(:userLon)) +
            sin(radians(:userLat)) * sin(radians(CAST(bk.latitude AS DOUBLE PRECISION)))
        )) ASC
        """, nativeQuery = true)
    Page<BehaviouristKyc> findBehaviouristsWithinDistance(
        @Param("userLat") Double userLat,
        @Param("userLon") Double userLon,
        @Param("maxDistance") Double maxDistance,
        @Param("searchTerm") String searchTerm,
        @Param("serviceType") String serviceType,
        Pageable pageable
    );
	Optional<BehaviouristKyc> findByServiceProviderUid(UUID uid);
}