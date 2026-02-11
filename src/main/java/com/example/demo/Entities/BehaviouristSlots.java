package com.example.demo.Entities;

import java.time.LocalTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "behaviourist_slots")
public class BehaviouristSlots extends BaseEntity {

    @Column(name = "service_provider_uid", nullable = false)
    private UUID serviceProviderUid; // Stores ServiceProvider's UID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "behaviourist_day_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "slots", "serviceProvider"})
    private BehaviouristAvailableDay serviceProviderDay;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    // Custom getters for JSON response
    @JsonProperty("slotId")
    public Long getSlotIdForJson() {
        return this.getId();
    }

    @JsonProperty("behaviouristDayId")
    public Long getBehaviouristDayIdForJson() {
        return serviceProviderDay != null ? serviceProviderDay.getId() : null;
    }

    // Constructors
    public BehaviouristSlots() {}

    public BehaviouristSlots(BehaviouristAvailableDay serviceProviderDay, LocalTime startTime, LocalTime endTime) {
        this.serviceProviderDay = serviceProviderDay;
        this.serviceProviderUid = serviceProviderDay != null && serviceProviderDay.getServiceProvider() != null 
            ? serviceProviderDay.getServiceProvider().getUid() 
            : null;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and Setters
    public UUID getServiceProviderUid() {
        return serviceProviderUid;
    }

    public void setServiceProviderUid(UUID serviceProviderUid) {
        this.serviceProviderUid = serviceProviderUid;
    }

    public BehaviouristAvailableDay getServiceProviderDay() {
        return serviceProviderDay;
    }

    public void setServiceProviderDay(BehaviouristAvailableDay serviceProviderDay) {
        this.serviceProviderDay = serviceProviderDay;
        // Keep serviceProviderUid in sync
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
}