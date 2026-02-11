package com.example.demo.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.Entities.BehaviouristAvailableDay;
import com.example.demo.Entities.ServiceProvider;
import com.example.demo.Enum.DayOfWeek;

@Repository
public interface BehaviouristDaysRepo extends JpaRepository<BehaviouristAvailableDay, Long> {
    
    // Find all days by ServiceProvider entity
    @Query("SELECT d FROM BehaviouristAvailableDay d WHERE d.serviceProvider = :serviceProvider")
    List<BehaviouristAvailableDay> findByServiceProvider(@Param("serviceProvider") ServiceProvider serviceProvider);
    
    // Find all days by ServiceProvider UID
    List<BehaviouristAvailableDay> findByServiceProvider_Uid(UUID serviceProviderUid);
    
    // Delete all days for a ServiceProvider UID
    void deleteByServiceProvider_Uid(UUID serviceProviderUid);
    
    // Find all days for a specific DayOfWeek
    List<BehaviouristAvailableDay> findByDayOfWeek(DayOfWeek day);
    
    // Check if exists by UID
    boolean existsByUid(UUID uid);
    
    // Find single day record for a given serviceProviderUid and dayOfWeek
    Optional<BehaviouristAvailableDay> findFirstByServiceProvider_UidAndDayOfWeek(UUID serviceProviderUid, DayOfWeek dayOfWeek);
    
    // Find all days for a given serviceProviderUid and dayOfWeek
    List<BehaviouristAvailableDay> findByServiceProvider_UidAndDayOfWeek(UUID serviceProviderUid, DayOfWeek dayOfWeek);
    
    // Get distinct service providers available on a given day
    @Query("SELECT DISTINCT d.serviceProvider FROM BehaviouristAvailableDay d WHERE d.dayOfWeek = :day")
    List<ServiceProvider> findServiceProvidersByDay(@Param("day") DayOfWeek day);
}