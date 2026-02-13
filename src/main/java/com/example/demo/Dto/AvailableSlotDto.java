package com.example.demo.Dto;

import java.time.LocalTime;
import java.util.List;

public class AvailableSlotDto {
    private LocalTime slotStartTime;
    private LocalTime slotEndTime;
    private Integer availableDurationMinutes;
    private List<GroomerServiceResDto> compatibleServices;

    // Getters and Setters
    public LocalTime getSlotStartTime() {
        return slotStartTime;
    }

    public void setSlotStartTime(LocalTime slotStartTime) {
        this.slotStartTime = slotStartTime;
    }

    public LocalTime getSlotEndTime() {
        return slotEndTime;
    }

    public void setSlotEndTime(LocalTime slotEndTime) {
        this.slotEndTime = slotEndTime;
    }

    public Integer getAvailableDurationMinutes() {
        return availableDurationMinutes;
    }

    public void setAvailableDurationMinutes(Integer availableDurationMinutes) {
        this.availableDurationMinutes = availableDurationMinutes;
    }

	public List<GroomerServiceResDto> getCompatibleServices() {
		return compatibleServices;
	}

	public void setCompatibleServices(List<GroomerServiceResDto> compatibleServices) {
		this.compatibleServices = compatibleServices;
	}

    
}