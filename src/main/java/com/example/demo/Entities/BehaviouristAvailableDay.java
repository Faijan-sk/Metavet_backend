package com.example.demo.Entities;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import com.example.demo.Enum.DayOfWeek;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "behaviourist_available_days")
public class BehaviouristAvailableDay extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 20)
    private DayOfWeek dayOfWeek;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_provider_uid", referencedColumnName = "uid", nullable = false)
    @JsonIgnoreProperties({"availableDays", "hibernateLazyInitializer", "handler"})
    private ServiceProvider serviceProvider;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "slot_duration_minutes", nullable = false)
    private Integer slotDurationMinutes;

    @OneToMany(mappedBy = "serviceProviderDay", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<BehaviouristSlots> slots;

    // Custom getters for JSON response
    @JsonProperty("behaviouristDayId")
    public Long getBehaviouristDayIdForJson() {
        return this.getId();
    }

    @JsonProperty("serviceProviderUid")
    public UUID getServiceProviderUidForJson() {
        return serviceProvider != null ? serviceProvider.getUid() : null;
    }

    // Constructors
    public BehaviouristAvailableDay() {}

    public BehaviouristAvailableDay(DayOfWeek dayOfWeek, ServiceProvider serviceProvider, 
                                     LocalTime startTime, LocalTime endTime, Integer slotDurationMinutes) {
        this.dayOfWeek = dayOfWeek;
        this.serviceProvider = serviceProvider;
        this.startTime = startTime;
        this.endTime = endTime;
        this.slotDurationMinutes = slotDurationMinutes;
    }

    // Getters and Setters
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
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

    public Integer getSlotDurationMinutes() {
        return slotDurationMinutes;
    }

    public void setSlotDurationMinutes(Integer slotDurationMinutes) {
        this.slotDurationMinutes = slotDurationMinutes;
    }

    public List<BehaviouristSlots> getSlots() {
        return slots;
    }

    public void setSlots(List<BehaviouristSlots> slots) {
        this.slots = slots;
    }
}