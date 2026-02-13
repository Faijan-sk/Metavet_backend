package com.example.demo.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Entities.GroomerServices;

@Repository
public interface GroomerServiceRepo extends JpaRepository<GroomerServices, Long> {

    List<GroomerServices> findByServiceProvider_UidAndIsActiveTrue(UUID serviceProviderUid); // ✅ Changed to UUID
    Optional<GroomerServices> findByUidAndIsActiveTrue(UUID uid); // ✅ Changed to UUID
    Optional<GroomerServices> findByUid(UUID uid); // ✅ Changed to UUID
}