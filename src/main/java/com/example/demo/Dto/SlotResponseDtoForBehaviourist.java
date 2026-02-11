package com.example.demo.Dto;

import java.time.LocalTime;
import java.util.UUID;

public class SlotResponseDtoForBehaviourist {
    
    private Long slotId;
    private UUID slotUid;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long behaviouristDayId;

    // Constructors
    public SlotResponseDtoForBehaviourist() {}

    public SlotResponseDtoForBehaviourist(Long slotId, UUID slotUid, LocalTime startTime, 
                                          LocalTime endTime, Long behaviouristDayId) {
        this.slotId = slotId;
        this.slotUid = slotUid;
        this.startTime = startTime;
        this.endTime = endTime;
        this.behaviouristDayId = behaviouristDayId;
    }

    // Getters and Setters
    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public UUID getSlotUid() {
        return slotUid;
    }

    public void setSlotUid(UUID slotUid) {
        this.slotUid = slotUid;
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

    public Long getBehaviouristDayId() {
        return behaviouristDayId;
    }

    public void setBehaviouristDayId(Long behaviouristDayId) {
        this.behaviouristDayId = behaviouristDayId;
    }
}