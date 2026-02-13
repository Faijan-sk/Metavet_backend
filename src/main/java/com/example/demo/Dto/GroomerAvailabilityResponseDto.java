package com.example.demo.Dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class GroomerAvailabilityResponseDto {
    private String groomerUid;
    private String groomerName;
    private LocalDate date;
    private String dayName;
    private WorkingHoursDTO workingHours;
    private List<AvailableSlotDto> availableSlots;

    public static class WorkingHoursDTO {
        private LocalTime startTime;
        private LocalTime endTime;

        public LocalTime getStartTime() {
            return startTime;
        }

        public void setStartTime(LocalTime startTime) {
            this.startTime = startTime;
        }

        public LocalTime getEndTime() {
            return endTime;
        }

        public void setEndTime(LocalTime endTime) {
            this.endTime = endTime;
        }
    }

    // Getters and Setters
    public String getGroomerUid() {
        return groomerUid;
    }

    public void setGroomerUid(String groomerUid) {
        this.groomerUid = groomerUid;
    }

    public String getGroomerName() {
        return groomerName;
    }

    public void setGroomerName(String groomerName) {
        this.groomerName = groomerName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public WorkingHoursDTO getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(WorkingHoursDTO workingHours) {
        this.workingHours = workingHours;
    }

    public List<AvailableSlotDto> getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(List<AvailableSlotDto> availableSlots) {
        this.availableSlots = availableSlots;
    }
}