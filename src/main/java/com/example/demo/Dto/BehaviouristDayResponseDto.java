package com.example.demo.Dto;

import java.util.UUID;

public class BehaviouristDayResponseDto {
    
    private Long behaviouristDayId;
    private UUID behaviouristDayUid;
    private String dayOfWeek;

    // Constructors
    public BehaviouristDayResponseDto() {}

    public BehaviouristDayResponseDto(Long behaviouristDayId, UUID behaviouristDayUid, String dayOfWeek) {
        this.behaviouristDayId = behaviouristDayId;
        this.behaviouristDayUid = behaviouristDayUid;
        this.dayOfWeek = dayOfWeek;
    }

    // Getters and Setters
    public Long getBehaviouristDayId() {
        return behaviouristDayId;
    }

    public void setBehaviouristDayId(Long behaviouristDayId) {
        this.behaviouristDayId = behaviouristDayId;
    }

    public UUID getBehaviouristDayUid() {
        return behaviouristDayUid;
    }

    public void setBehaviouristDayUid(UUID behaviouristDayUid) {
        this.behaviouristDayUid = behaviouristDayUid;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
}