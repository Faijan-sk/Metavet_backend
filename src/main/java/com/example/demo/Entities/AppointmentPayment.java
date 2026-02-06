package com.example.demo.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "appointment_payments")
public class AppointmentPayment extends BaseEntity {

    @Column(name = "session_id", nullable = false, unique = true)
    private String sessionId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    @Column(name = "slot_id", nullable = false)
    private Long slotId;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    // ✅ FIX: Increase column length for checkout_url
    @Column(name = "checkout_url", length = 1000)  // ✅ Changed from default 255 to 1000
    private String checkoutUrl;

    @Column(name = "pet_id")
    private Long petId;

    @Column(name = "doctor_day_id")
    private Long doctorDayId;

    @Column(name = "appointment_date")
    private String appointmentDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    // Getters and Setters
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public String getCheckoutUrl() { return checkoutUrl; }
    public void setCheckoutUrl(String checkoutUrl) { this.checkoutUrl = checkoutUrl; }

    public Long getPetId() { return petId; }
    public void setPetId(Long petId) { this.petId = petId; }

    public Long getDoctorDayId() { return doctorDayId; }
    public void setDoctorDayId(Long doctorDayId) { this.doctorDayId = doctorDayId; }

    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }

    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }

    public enum PaymentStatus {
        PENDING,
        PAID,
        FAILED,
        CANCELLED
    }
}