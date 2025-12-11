package com.example.demo.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.Entities.PetBehavioristKycEntity;
import com.example.demo.Entities.PetBehavioristKycEntity.KycStatus;

@Repository
public interface PetBehavioristKycRepo extends JpaRepository<PetBehavioristKycEntity, Long> {

   
    void deleteByUid(UUID uid);

   
    Optional<PetBehavioristKycEntity> findByUid(UUID uid);

    List<PetBehavioristKycEntity> findAllByOrderByCreatedAtDesc();
   
    @Modifying
    @Query("UPDATE PetBehavioristKycEntity p SET p.kycStatus = :status WHERE p.uid = :uid")
    int updateKycStatusByUid(@Param("uid") UUID uid, @Param("status") KycStatus status);
}
