package com.example.demo.Dto;

import java.util.UUID;

public class ServiceProviderDayResponseDto {

    private Long serviceProviderDayId;
    private UUID serviceProviderDayUid;
    private String dayOfWeek;

    public ServiceProviderDayResponseDto() {}

    public ServiceProviderDayResponseDto(Long serviceProviderDayId, UUID serviceProviderDayUid, String dayOfWeek) {
        this.serviceProviderDayId = serviceProviderDayId;
        this.serviceProviderDayUid = serviceProviderDayUid;
        this.dayOfWeek = dayOfWeek;
    }

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

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
}