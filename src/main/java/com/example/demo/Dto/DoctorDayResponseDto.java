package com.example.demo.Dto;


import java.util.UUID;

public class DoctorDayResponseDto {

    private Long doctorDayId;
    private UUID doctorDayUid;
    private String dayOfWeek;

    public DoctorDayResponseDto(Long doctorDayId, UUID doctorDayUid, String dayOfWeek) {
        this.doctorDayId = doctorDayId;
        this.doctorDayUid = doctorDayUid;
        this.dayOfWeek = dayOfWeek;
    }

    public Long getDoctorDayId() {
        return doctorDayId;
    }

    public UUID getDoctorDayUid() {
        return doctorDayUid;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }
    
    
    
    
}

