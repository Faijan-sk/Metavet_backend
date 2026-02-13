package com.example.demo.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.Entities.GroomerAvailableDay;

public interface GroomerAvailableDayRepo extends JpaRepository<GroomerAvailableDay, Long> {

    List<GroomerAvailableDay> findByServiceProvider_UidAndIsActiveTrue(UUID serviceProviderUid); // ✅ Changed to UUID

    Optional<GroomerAvailableDay> findByServiceProvider_UidAndDayOfWeekAndIsActiveTrue(
            UUID serviceProviderUid, Integer dayOfWeek); // ✅ Changed to UUID

    @Query("SELECT DISTINCT gad.serviceProvider.uid FROM GroomerAvailableDay gad " +
           "WHERE gad.dayOfWeek = :dayOfWeek AND gad.isActive = true")
    List<UUID> findServiceProviderUidsByDayOfWeek(@Param("dayOfWeek") Integer dayOfWeek); // ✅ Changed return type to UUID

	List<GroomerAvailableDay> findByServiceProviderUid(UUID fromString);
}