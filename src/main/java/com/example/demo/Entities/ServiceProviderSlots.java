package com.example.demo.Entities;

import java.time.LocalTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

@Entity
@Table(name = "service_provider_slots")
public class ServiceProviderSlots extends BaseEntity {

    @Column(name = "service_provider_uid", nullable = false)
    private UUID serviceProviderUid; // Stores ServiceProvider's UID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_provider_day_uid", referencedColumnName = "uid", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "slots", "serviceProvider"})
    private ServiceProviderDays serviceProviderDay;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    // Custom getters for JSON response
    @JsonProperty("slotId")
    public Long getSlotIdForJson() {
        return this.getId();
    }

    @JsonProperty("serviceProviderDayId")
    public Long getServiceProviderDayIdForJson() {
        return serviceProviderDay != null ? serviceProviderDay.getId() : null;
    }

    // Constructors
    public ServiceProviderSlots() {}

    public ServiceProviderSlots(ServiceProviderDays serviceProviderDay, LocalTime startTime, LocalTime endTime) {
        this.serviceProviderDay = serviceProviderDay;
        this.serviceProviderUid = serviceProviderDay != null && serviceProviderDay.getServiceProvider() != null 
                ? serviceProviderDay.getServiceProvider().getUid() : null;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and Setters
    public ServiceProviderDays getServiceProviderDay() {
        return serviceProviderDay;
    }

    public void setServiceProviderDay(ServiceProviderDays serviceProviderDay) {
        this.serviceProviderDay = serviceProviderDay;
        if (serviceProviderDay != null && serviceProviderDay.getServiceProvider() != null) {
            this.serviceProviderUid = serviceProviderDay.getServiceProvider().getUid();
        }
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

    public UUID getServiceProviderUid() {
        return serviceProviderUid;
    }

    public void setServiceProviderUid(UUID serviceProviderUid) {
        this.serviceProviderUid = serviceProviderUid;
    }
}