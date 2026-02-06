package com.example.demo.Repository;

import com.example.demo.Entities.AppointmentPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AppointmentPaymentRepo extends JpaRepository<AppointmentPayment, Long> {
    Optional<AppointmentPayment> findBySessionId(String sessionId);
}