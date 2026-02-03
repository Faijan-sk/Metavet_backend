package com.example.demo.Dto;

import java.time.LocalTime;
import java.util.UUID;

public class SlotResponseDtoForServiceProvider {

    private Long id;
    private UUID uid;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long slotId;
    private Long serviceProviderDayId;

    public SlotResponseDtoForServiceProvider() {}

    public SlotResponseDtoForServiceProvider(Long id, UUID uid, LocalTime startTime, 
                                             LocalTime endTime, Long slotId, Long serviceProviderDayId) {
        this.id = id;
        this.uid = uid;
        this.startTime = startTime;
        this.endTime = endTime;
        this.slotId = slotId;
        this.serviceProviderDayId = serviceProviderDayId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUid() {
        return uid;
    }

    public void setUid(UUID uid) {
        this.uid = uid;
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

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public Long getServiceProviderDayId() {
        return serviceProviderDayId;
    }

    public void setServiceProviderDayId(Long serviceProviderDayId) {
        this.serviceProviderDayId = serviceProviderDayId;
    }
}