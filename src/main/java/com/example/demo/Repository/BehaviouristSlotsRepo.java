package com.example.demo.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.Entities.BehaviouristAvailableDay;
import com.example.demo.Entities.BehaviouristSlots;
import com.example.demo.Enum.DayOfWeek;

@Repository
public interface BehaviouristSlotsRepo extends JpaRepository<BehaviouristSlots, Long> {

    // Find all slots by serviceProviderUid
    List<BehaviouristSlots> findByServiceProviderUid(UUID serviceProviderUid);

    // Find slots by serviceProviderUid and order by start time
    List<BehaviouristSlots> findByServiceProviderUidOrderByStartTimeAsc(UUID serviceProviderUid);

    // Count slots by serviceProviderUid
    Long countByServiceProviderUid(UUID serviceProviderUid);

    // Find slots by behaviourist day
    List<BehaviouristSlots> findByServiceProviderDay(BehaviouristAvailableDay serviceProviderDay);

    @Query("SELECT s FROM BehaviouristSlots s WHERE s.serviceProviderDay.id = :behaviouristDayId")
    List<BehaviouristSlots> findByServiceProviderDay_Id(@Param("behaviouristDayId") Long behaviouristDayId);

    // Get slots for a specific service provider on a specific day using serviceProviderUid field
    @Query("SELECT s FROM BehaviouristSlots s WHERE s.serviceProviderUid = :serviceProviderUid " +
           "AND s.serviceProviderDay.dayOfWeek = :day")
    List<BehaviouristSlots> findByServiceProviderUidAndDay(@Param("serviceProviderUid") UUID serviceProviderUid,
                                                            @Param("day") DayOfWeek day);

    // Get all slots for a specific service provider (using join - backup method)
    @Query("SELECT s FROM BehaviouristSlots s WHERE s.serviceProviderDay.serviceProvider.uid = :serviceProviderUid")
    List<BehaviouristSlots> findByServiceProviderUidViaJoin(@Param("serviceProviderUid") UUID serviceProviderUid);

    // Find slots by time range
    @Query("SELECT s FROM BehaviouristSlots s WHERE s.startTime >= :startTime AND s.endTime <= :endTime")
    List<BehaviouristSlots> findByTimeRange(@Param("startTime") LocalTime startTime,
                                            @Param("endTime") LocalTime endTime);

    // Find slots for service provider on specific day with time range
    @Query("SELECT s FROM BehaviouristSlots s WHERE s.serviceProviderUid = :serviceProviderUid " +
           "AND s.serviceProviderDay.dayOfWeek = :day " +
           "AND s.startTime >= :startTime AND s.endTime <= :endTime")
    List<BehaviouristSlots> findServiceProviderSlotsByDayAndTimeRange(@Param("serviceProviderUid") UUID serviceProviderUid,
                                                                       @Param("day") DayOfWeek day,
                                                                       @Param("startTime") LocalTime startTime,
                                                                       @Param("endTime") LocalTime endTime);

    // Check if slot exists for behaviourist day with specific time
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM BehaviouristSlots s " +
           "WHERE s.serviceProviderDay.id = :behaviouristDayId AND s.startTime = :startTime")
    boolean existsByServiceProviderDayAndStartTime(@Param("behaviouristDayId") Long behaviouristDayId,
                                                   @Param("startTime") LocalTime startTime);

    // Find overlapping slots for a behaviourist day
    @Query("SELECT s FROM BehaviouristSlots s WHERE s.serviceProviderDay.id = :behaviouristDayId " +
           "AND ((s.startTime < :endTime AND s.endTime > :startTime))")
    List<BehaviouristSlots> findOverlappingSlots(@Param("behaviouristDayId") Long behaviouristDayId,
                                                  @Param("startTime") LocalTime startTime,
                                                  @Param("endTime") LocalTime endTime);

    // Count slots by behaviourist day
    @Query("SELECT COUNT(s) FROM BehaviouristSlots s WHERE s.serviceProviderDay.id = :behaviouristDayId")
    Long countSlotsByBehaviouristDay(@Param("behaviouristDayId") Long behaviouristDayId);

    // Get slots ordered by start time
    @Query("SELECT s FROM BehaviouristSlots s WHERE s.serviceProviderDay.id = :behaviouristDayId " +
           "ORDER BY s.startTime ASC")
    List<BehaviouristSlots> findByServiceProviderDayIdOrderByStartTime(@Param("behaviouristDayId") Long behaviouristDayId);

    // Get slots for service provider ordered by day and time
    @Query("SELECT s FROM BehaviouristSlots s WHERE s.serviceProviderUid = :serviceProviderUid " +
           "ORDER BY s.serviceProviderDay.dayOfWeek, s.startTime ASC")
    List<BehaviouristSlots> findByServiceProviderUidOrderByDayAndTime(@Param("serviceProviderUid") UUID serviceProviderUid);

    // Find specific slot by behaviourist day and time
    @Query("SELECT s FROM BehaviouristSlots s WHERE s.serviceProviderDay.id = :behaviouristDayId " +
           "AND s.startTime = :startTime AND s.endTime = :endTime")
    Optional<BehaviouristSlots> findByServiceProviderDayAndTime(@Param("behaviouristDayId") Long behaviouristDayId,
                                                                @Param("startTime") LocalTime startTime,
                                                                @Param("endTime") LocalTime endTime);

    // Find with details
    @Query("SELECT s FROM BehaviouristSlots s " +
           "LEFT JOIN FETCH s.serviceProviderDay sd " +
           "LEFT JOIN FETCH sd.serviceProvider " +
           "WHERE s.id = :slotId")
    Optional<BehaviouristSlots> findByIdWithDetails(@Param("slotId") Long slotId);

    // Get all slots for service provider with details
    @Query("SELECT DISTINCT s FROM BehaviouristSlots s " +
           "LEFT JOIN FETCH s.serviceProviderDay sd " +
           "LEFT JOIN FETCH sd.serviceProvider " +
           "WHERE s.serviceProviderUid = :serviceProviderUid " +
           "ORDER BY sd.dayOfWeek, s.startTime")
    List<BehaviouristSlots> findByServiceProviderUidWithDetails(@Param("serviceProviderUid") UUID serviceProviderUid);

    // Delete all slots for a behaviourist day
    void deleteByServiceProviderDay_Id(Long behaviouristDayId);

    // Delete slots by IDs
    @Modifying
    @Query("DELETE FROM BehaviouristSlots s WHERE s.id IN :ids")
    void deleteByIdIn(@Param("ids") List<Long> ids);

    // Find multiple slots by IDs
    List<BehaviouristSlots> findByIdIn(List<Long> ids);

    // Find morning slots (before 12 PM)
    @Query("SELECT s FROM BehaviouristSlots s WHERE s.serviceProviderUid = :serviceProviderUid " +
           "AND s.startTime < :noonTime")
    List<BehaviouristSlots> findMorningSlots(@Param("serviceProviderUid") UUID serviceProviderUid,
                                             @Param("noonTime") LocalTime noonTime);

    // Find afternoon slots (12 PM to 5 PM)
    @Query("SELECT s FROM BehaviouristSlots s WHERE s.serviceProviderUid = :serviceProviderUid " +
           "AND s.startTime >= :afternoonStart AND s.startTime < :eveningStart")
    List<BehaviouristSlots> findAfternoonSlots(@Param("serviceProviderUid") UUID serviceProviderUid,
                                               @Param("afternoonStart") LocalTime afternoonStart,
                                               @Param("eveningStart") LocalTime eveningStart);

    // Find evening slots (after 5 PM)
    @Query("SELECT s FROM BehaviouristSlots s WHERE s.serviceProviderUid = :serviceProviderUid " +
           "AND s.startTime >= :eveningStart")
    List<BehaviouristSlots> findEveningSlots(@Param("serviceProviderUid") UUID serviceProviderUid,
                                             @Param("eveningStart") LocalTime eveningStart);

    // Count total slots by service provider
    @Query("SELECT COUNT(s) FROM BehaviouristSlots s WHERE s.serviceProviderUid = :serviceProviderUid")
    Long countSlotsByServiceProvider(@Param("serviceProviderUid") UUID serviceProviderUid);
    
    
}