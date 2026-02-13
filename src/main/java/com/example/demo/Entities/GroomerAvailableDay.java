package com.example.demo.Entities;

import jakarta.persistence.ManyToOne;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;


@Entity
@Table(name = "groomer_available_day",
        uniqueConstraints = @UniqueConstraint(columnNames = {"service_provider_uid", "day_of_week"}))
public class GroomerAvailableDay extends BaseEntity{
	
	@ManyToOne
    @JoinColumn(name = "service_provider_uid", referencedColumnName = "uid", nullable = false)
    private ServiceProvider serviceProvider;

    @NotNull(message = "Day of week is required")
    @Min(value = 1, message = "Day must be between 1 (Monday) and 7 (Sunday)")
    @Max(value = 7, message = "Day must be between 1 (Monday) and 7 (Sunday)")
    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek; 

    @NotNull(message = "Start time is required")
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private Boolean isActive = true;

	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	public Integer getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(Integer dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
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

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
    
    
    
    

}
