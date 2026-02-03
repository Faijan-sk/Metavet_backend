package com.example.demo.Entities;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import com.example.demo.Enum.DayOfWeek;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

@Entity
@Table(name = "service_provider_days_availability")
public class ServiceProviderDays extends BaseEntity {

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
    private List<ServiceProviderSlots> slots;

    // Custom getter for JSON response
    @JsonProperty("serviceProviderDayId")
    public Long getServiceProviderDayIdForJson() {
        return this.getId();
    }

    @JsonProperty("serviceProviderUid")
    public UUID getServiceProviderUidForJson() {
        return serviceProvider != null ? serviceProvider.getUid() : null;
    }

    // Constructors
    public ServiceProviderDays() {}

    public ServiceProviderDays(DayOfWeek dayOfWeek, ServiceProvider serviceProvider, 
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

    public List<ServiceProviderSlots> getSlots() {
        return slots;
    }

    public void setSlots(List<ServiceProviderSlots> slots) {
        this.slots = slots;
    }
}