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

import com.example.demo.Entities.ServiceProviderDays;
import com.example.demo.Entities.ServiceProviderSlots;
import com.example.demo.Enum.DayOfWeek;

@Repository
public interface ServiceProviderSlotRepo extends JpaRepository<ServiceProviderSlots, Long> {

    // Find all slots by ServiceProvider UID
    List<ServiceProviderSlots> findByServiceProviderUid(UUID serviceProviderUid);

    // Find slots by ServiceProvider UID ordered by start time
    List<ServiceProviderSlots> findByServiceProviderUidOrderByStartTimeAsc(UUID serviceProviderUid);

    // Count slots by ServiceProvider UID
    Long countByServiceProviderUid(UUID serviceProviderUid);

    // Find slots by ServiceProviderDays entity
    List<ServiceProviderSlots> findByServiceProviderDay(ServiceProviderDays serviceProviderDay);

    // Find slots by ServiceProviderDay UID
    @Query("SELECT s FROM ServiceProviderSlots s WHERE s.serviceProviderDay.uid = :serviceProviderDayUid")
    List<ServiceProviderSlots> findByServiceProviderDay_Uid(@Param("serviceProviderDayUid") UUID serviceProviderDayUid);

    // Get slots for a specific ServiceProvider on a specific day
    @Query("SELECT s FROM ServiceProviderSlots s WHERE s.serviceProviderUid = :serviceProviderUid " +
           "AND s.serviceProviderDay.dayOfWeek = :day")
    List<ServiceProviderSlots> findByServiceProviderUidAndDay(@Param("serviceProviderUid") UUID serviceProviderUid,
                                                               @Param("day") DayOfWeek day);

    // Find slots by time range
    @Query("SELECT s FROM ServiceProviderSlots s WHERE s.startTime >= :startTime AND s.endTime <= :endTime")
    List<ServiceProviderSlots> findByTimeRange(@Param("startTime") LocalTime startTime,
                                                @Param("endTime") LocalTime endTime);

    // Find slots for ServiceProvider on specific day with time range
    @Query("SELECT s FROM ServiceProviderSlots s WHERE s.serviceProviderUid = :serviceProviderUid " +
           "AND s.serviceProviderDay.dayOfWeek = :day " +
           "AND s.startTime >= :startTime AND s.endTime <= :endTime")
    List<ServiceProviderSlots> findServiceProviderSlotsByDayAndTimeRange(
            @Param("serviceProviderUid") UUID serviceProviderUid,
            @Param("day") DayOfWeek day,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);

    // Check if slot exists for ServiceProviderDay with specific time
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM ServiceProviderSlots s " +
           "WHERE s.serviceProviderDay.uid = :serviceProviderDayUid AND s.startTime = :startTime")
    boolean existsByServiceProviderDayAndStartTime(@Param("serviceProviderDayUid") UUID serviceProviderDayUid,
                                                    @Param("startTime") LocalTime startTime);

    // Find overlapping slots for a ServiceProviderDay
    @Query("SELECT s FROM ServiceProviderSlots s WHERE s.serviceProviderDay.uid = :serviceProviderDayUid " +
           "AND ((s.startTime < :endTime AND s.endTime > :startTime))")
    List<ServiceProviderSlots> findOverlappingSlots(@Param("serviceProviderDayUid") UUID serviceProviderDayUid,
                                                     @Param("startTime") LocalTime startTime,
                                                     @Param("endTime") LocalTime endTime);

    // Count slots by ServiceProviderDay UID
    @Query("SELECT COUNT(s) FROM ServiceProviderSlots s WHERE s.serviceProviderDay.uid = :serviceProviderDayUid")
    Long countSlotsByServiceProviderDay(@Param("serviceProviderDayUid") UUID serviceProviderDayUid);

    // Get slots ordered by start time
    @Query("SELECT s FROM ServiceProviderSlots s WHERE s.serviceProviderDay.uid = :serviceProviderDayUid " +
           "ORDER BY s.startTime ASC")
    List<ServiceProviderSlots> findByServiceProviderDayUidOrderByStartTime(@Param("serviceProviderDayUid") UUID serviceProviderDayUid);

    // Get slots for ServiceProvider ordered by day and time
    @Query("SELECT s FROM ServiceProviderSlots s WHERE s.serviceProviderUid = :serviceProviderUid " +
           "ORDER BY s.serviceProviderDay.dayOfWeek, s.startTime ASC")
    List<ServiceProviderSlots> findByServiceProviderUidOrderByDayAndTime(@Param("serviceProviderUid") UUID serviceProviderUid);

    // Find specific slot by ServiceProviderDay and time
    @Query("SELECT s FROM ServiceProviderSlots s WHERE s.serviceProviderDay.uid = :serviceProviderDayUid " +
           "AND s.startTime = :startTime AND s.endTime = :endTime")
    Optional<ServiceProviderSlots> findByServiceProviderDayAndTime(
            @Param("serviceProviderDayUid") UUID serviceProviderDayUid,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);

    // Find with details (eager fetch)
    @Query("SELECT s FROM ServiceProviderSlots s " +
           "LEFT JOIN FETCH s.serviceProviderDay spd " +
           "LEFT JOIN FETCH spd.serviceProvider " +
           "WHERE s.id = :slotId")
    Optional<ServiceProviderSlots> findByIdWithDetails(@Param("slotId") Long slotId);

    // Get all slots for ServiceProvider with details
    @Query("SELECT DISTINCT s FROM ServiceProviderSlots s " +
           "LEFT JOIN FETCH s.serviceProviderDay spd " +
           "LEFT JOIN FETCH spd.serviceProvider " +
           "WHERE s.serviceProviderUid = :serviceProviderUid " +
           "ORDER BY spd.dayOfWeek, s.startTime")
    List<ServiceProviderSlots> findByServiceProviderUidWithDetails(@Param("serviceProviderUid") UUID serviceProviderUid);

    // Delete all slots for a ServiceProviderDay UID
    void deleteByServiceProviderDay_Uid(UUID serviceProviderDayUid);

    // Delete slots by IDs
    @Modifying
    @Query("DELETE FROM ServiceProviderSlots s WHERE s.id IN :ids")
    void deleteByIdIn(@Param("ids") List<Long> ids);

    // Find multiple slots by IDs
    List<ServiceProviderSlots> findByIdIn(List<Long> ids);

    // Find morning slots (before 12 PM)
    @Query("SELECT s FROM ServiceProviderSlots s WHERE s.serviceProviderUid = :serviceProviderUid " +
           "AND s.startTime < :noonTime")
    List<ServiceProviderSlots> findMorningSlots(@Param("serviceProviderUid") UUID serviceProviderUid,
                                                 @Param("noonTime") LocalTime noonTime);

    // Find afternoon slots (12 PM to 5 PM)
    @Query("SELECT s FROM ServiceProviderSlots s WHERE s.serviceProviderUid = :serviceProviderUid " +
           "AND s.startTime >= :afternoonStart AND s.startTime < :eveningStart")
    List<ServiceProviderSlots> findAfternoonSlots(@Param("serviceProviderUid") UUID serviceProviderUid,
                                                   @Param("afternoonStart") LocalTime afternoonStart,
                                                   @Param("eveningStart") LocalTime eveningStart);

    // Find evening slots (after 5 PM)
    @Query("SELECT s FROM ServiceProviderSlots s WHERE s.serviceProviderUid = :serviceProviderUid " +
           "AND s.startTime >= :eveningStart")
    List<ServiceProviderSlots> findEveningSlots(@Param("serviceProviderUid") UUID serviceProviderUid,
                                                 @Param("eveningStart") LocalTime eveningStart);

    // Count total slots by ServiceProvider
    @Query("SELECT COUNT(s) FROM ServiceProviderSlots s WHERE s.serviceProviderUid = :serviceProviderUid")
    Long countSlotsByServiceProvider(@Param("serviceProviderUid") UUID serviceProviderUid);

    // Find available slots (not booked) - assuming you have Appointment entity
    @Query("SELECT s FROM ServiceProviderSlots s WHERE s.serviceProviderUid = :serviceProviderUid " +
           "AND s.serviceProviderDay.dayOfWeek = :day " +
           "AND NOT EXISTS (SELECT 1 FROM Appointment a WHERE a.slotId = s.id) " +
           "ORDER BY s.startTime ASC")
    List<ServiceProviderSlots> findAvailableSlotsByServiceProviderUidAndDay(
            @Param("serviceProviderUid") UUID serviceProviderUid,
            @Param("day") DayOfWeek day);
}