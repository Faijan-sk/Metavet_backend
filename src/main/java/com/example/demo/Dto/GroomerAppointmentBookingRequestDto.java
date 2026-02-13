package com.example.demo.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class GroomerAppointmentBookingRequestDto {
	
	@NotBlank(message = "Service provider UID is required")
    private String serviceProviderUid;

    @NotBlank(message = "Service UID is required")
    private String serviceUid;

    @NotNull(message = "Appointment date is required")
    private LocalDate appointmentDate;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    private String notes;

	public String getServiceProviderUid() {
		return serviceProviderUid;
	}

	public void setServiceProviderUid(String serviceProviderUid) {
		this.serviceProviderUid = serviceProviderUid;
	}

	public String getServiceUid() {
		return serviceUid;
	}

	public void setServiceUid(String serviceUid) {
		this.serviceUid = serviceUid;
	}

	public LocalDate getAppointmentDate() {
		return appointmentDate;
	}

	public void setAppointmentDate(LocalDate appointmentDate) {
		this.appointmentDate = appointmentDate;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

    
    

}
