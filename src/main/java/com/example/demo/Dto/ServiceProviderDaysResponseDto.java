package com.example.demo.Dto;

import java.time.LocalTime;
import java.util.UUID;

import com.example.demo.Enum.DayOfWeek;

public class ServiceProviderDaysResponseDto {

    private Long serviceProviderDayId;
    private UUID serviceProviderDayUid;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer slotDurationMinutes;

    // Constructors
    public ServiceProviderDaysResponseDto() {}

    // Getters and Setters
    public Long getServiceProviderDayId() {
        return serviceProviderDayId;
    }

    public void setServiceProviderDayId(Long serviceProviderDayId) {
        this.serviceProviderDayId = serviceProviderDayId;
    }

    public UUID getServiceProviderDayUid() {
        return serviceProviderDayUid;
    }

    public void setServiceProviderDayUid(UUID serviceProviderDayUid) {
        this.serviceProviderDayUid = serviceProviderDayUid;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

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

    public Integer getSlotDurationMinutes() {
        return slotDurationMinutes;
    }

    public void setSlotDurationMinutes(Integer slotDurationMinutes) {
        this.slotDurationMinutes = slotDurationMinutes;
    }
}