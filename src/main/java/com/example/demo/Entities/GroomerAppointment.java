package com.example.demo.Entities;


import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "groomer_appointments")
public class GroomerAppointment extends BaseEntity {
	
	@ManyToOne
	@JoinColumn(name = "service_provider_id", nullable = false)
	private ServiceProvider serviceProvider;

	@ManyToOne
	@JoinColumn(name = "client_id", nullable = false)
	private UsersEntity client;

	@ManyToOne
	@JoinColumn(name = "service_id", nullable = false)
	private GroomerServices service;

	    @NotNull(message = "Appointment date is required")
	    @Column(name = "appointment_date", nullable = false)
	    private LocalDate appointmentDate;

	    @NotNull(message = "Start time is required")
	    @Column(name = "start_time", nullable = false)
	    private LocalTime startTime;

	    @NotNull(message = "End time is required")
	    @Column(name = "end_time", nullable = false)
	    private LocalTime endTime;

	    @Enumerated(EnumType.STRING)
	    @Column(nullable = false)
	    private AppointmentStatus status = AppointmentStatus.PENDING;

	    @Column(length = 1000)
	    private String notes;

	    public enum AppointmentStatus {
	        PENDING,
	        CONFIRMED,
	        COMPLETED,
	        CANCELLED,
	        NO_SHOW
	    }

		public ServiceProvider getServiceProvider() {
			return serviceProvider;
		}

		public void setServiceProvider(ServiceProvider serviceProvider) {
			this.serviceProvider = serviceProvider;
		}

		public UsersEntity getClient() {
			return client;
		}

		public void setClient(UsersEntity client) {
			this.client = client;
		}

		public GroomerServices getService() {
			return service;
		}

		public void setService(GroomerServices service) {
			this.service = service;
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

		public LocalTime getEndTime() {
			return endTime;
		}

		public void setEndTime(LocalTime endTime) {
			this.endTime = endTime;
		}

		public AppointmentStatus getStatus() {
			return status;
		}

		public void setStatus(AppointmentStatus status) {
			this.status = status;
		}

		public String getNotes() {
			return notes;
		}

		public void setNotes(String notes) {
			this.notes = notes;
		}

	    
	    
	    
}
