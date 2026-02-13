package com.example.demo.Dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class GroomerResponseDto {

    private String __groomerUid__;
    private String __groomerName__;
    private LocalDate __date__;
    private String __dayName__;
    private WorkingHoursDTO __workingHours__;
    private List<GroomerAvailableSlotResponseDto> __availableSlots__;

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

    // ADD GETTERS AND SETTERS
    public String get__groomerUid__() {
        return __groomerUid__;
    }

    public void set__groomerUid__(String __groomerUid__) {
        this.__groomerUid__ = __groomerUid__;
    }

    public String get__groomerName__() {
        return __groomerName__;
    }

    public void set__groomerName__(String __groomerName__) {
        this.__groomerName__ = __groomerName__;
    }

    public LocalDate get__date__() {
        return __date__;
    }

    public void set__date__(LocalDate __date__) {
        this.__date__ = __date__;
    }

    public String get__dayName__() {
        return __dayName__;
    }

    public void set__dayName__(String __dayName__) {
        this.__dayName__ = __dayName__;
    }

    public WorkingHoursDTO get__workingHours__() {
        return __workingHours__;
    }

    public void set__workingHours__(WorkingHoursDTO __workingHours__) {
        this.__workingHours__ = __workingHours__;
    }

    public List<GroomerAvailableSlotResponseDto> get__availableSlots__() {
        return __availableSlots__;
    }

    public void set__availableSlots__(List<GroomerAvailableSlotResponseDto> __availableSlots__) {
        this.__availableSlots__ = __availableSlots__;
    }
}