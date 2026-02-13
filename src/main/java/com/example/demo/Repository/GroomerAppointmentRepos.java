package com.example.demo.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.Entities.GroomerAppointment;

@Repository
public interface GroomerAppointmentRepos extends JpaRepository<GroomerAppointment, Long> {

    @Query("SELECT a FROM GroomerAppointment a WHERE a.serviceProvider.uid = :providerUid " +
           "AND a.appointmentDate = :date " +
           "AND a.status NOT IN ('CANCELLED') " +
           "ORDER BY a.startTime ASC")
    List<GroomerAppointment> findBookedAppointments(
            @Param("providerUid") UUID providerUid, // ✅ Changed to UUID
            @Param("date") LocalDate date);

    @Query("SELECT COUNT(a) > 0 FROM GroomerAppointment a WHERE a.serviceProvider.uid = :providerUid " +
           "AND a.appointmentDate = :date " +
           "AND a.status NOT IN ('CANCELLED') " +
           "AND ((a.startTime < :endTime AND a.endTime > :startTime))")
    boolean hasConflict(
            @Param("providerUid") UUID providerUid, // ✅ Changed to UUID
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);
}