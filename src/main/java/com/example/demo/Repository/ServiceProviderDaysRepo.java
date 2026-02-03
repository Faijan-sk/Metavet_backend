package com.example.demo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Entities.ServiceProvider;
import com.example.demo.Entities.ServiceProviderDays;
import com.example.demo.Enum.DayOfWeek;

@Repository
public interface ServiceProviderDaysRepo extends JpaRepository<ServiceProviderDays, Long> {

	// Find all days by ServiceProvider entity
    @Query("SELECT d FROM ServiceProviderDays d WHERE d.serviceProvider = :serviceProvider")
    List<ServiceProviderDays> findByServiceProvider(@Param("serviceProvider") ServiceProvider serviceProvider);

    // Find all days by ServiceProvider UID
    List<ServiceProviderDays> findByServiceProvider_Uid(UUID serviceProviderUid);

    // Delete all days for a ServiceProvider UID
    void deleteByServiceProvider_Uid(UUID serviceProviderUid);

    // Find all days for a specific DayOfWeek
    List<ServiceProviderDays> findByDayOfWeek(DayOfWeek day);

    // Get distinct ServiceProviders available on a specific day
    @Query("SELECT DISTINCT d.serviceProvider FROM ServiceProviderDays d WHERE d.dayOfWeek = :day")
    List<ServiceProvider> findServiceProvidersByDay(@Param("day") DayOfWeek day);

    // Find first day record for ServiceProvider UID and DayOfWeek
    Optional<ServiceProviderDays> findFirstByServiceProvider_UidAndDayOfWeek(UUID serviceProviderUid, DayOfWeek dayOfWeek);

    // Find all days for ServiceProvider UID and DayOfWeek
    List<ServiceProviderDays> findByServiceProvider_UidAndDayOfWeek(UUID serviceProviderUid, DayOfWeek dayOfWeek);

    boolean existsByUid(UUID uid);
    
}
