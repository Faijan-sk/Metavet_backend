package com.example.demo.Dto;

import java.time.LocalTime;
import java.util.List;
import com.example.demo.Entities.GroomerServices;

public class GroomerAvailableSlotResponseDto {

    private LocalTime __slotStartTime__;
    private LocalTime __slotEndTime__;
    private Integer __availableDurationMinutes__;
    private List<GroomerServices> __compatibleServices__;

    // ADD GETTERS AND SETTERS
    public LocalTime get__slotStartTime__() {
        return __slotStartTime__;
    }

    public void set__slotStartTime__(LocalTime __slotStartTime__) {
        this.__slotStartTime__ = __slotStartTime__;
    }

    public LocalTime get__slotEndTime__() {
        return __slotEndTime__;
    }

    public void set__slotEndTime__(LocalTime __slotEndTime__) {
        this.__slotEndTime__ = __slotEndTime__;
    }

    public Integer get__availableDurationMinutes__() {
        return __availableDurationMinutes__;
    }

    public void set__availableDurationMinutes__(Integer __availableDurationMinutes__) {
        this.__availableDurationMinutes__ = __availableDurationMinutes__;
    }

    public List<GroomerServices> get__compatibleServices__() {
        return __compatibleServices__;
    }

    public void set__compatibleServices__(List<GroomerServices> __compatibleServices__) {
        this.__compatibleServices__ = __compatibleServices__;
    }
}