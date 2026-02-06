package com.example.demo.Controller;

import com.example.demo.Config.SpringSecurityAuditorAware;
import com.example.demo.Entities.AppointmentPayment;
import com.example.demo.Entities.UsersEntity;
import com.example.demo.Service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private SpringSecurityAuditorAware auditorAware;

    /**
     * ✅ Create Stripe Checkout Session
     * POST /api/payments/create-checkout
     */
    @PostMapping("/create-checkout")
    public ResponseEntity<?> createCheckoutSession(@RequestBody Map<String, Object> request) {
        try {
            // Get logged-in user
            Optional<UsersEntity> currentUserOpt = auditorAware.getCurrentAuditor();
            if (currentUserOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            Long userId = currentUserOpt.get().getId();

            // Extract request parameters
            Long petId = request.get("petId") != null 
                    ? Long.parseLong(request.get("petId").toString()) 
                    : null;
            Long doctorId = Long.parseLong(request.get("doctorId").toString());
            Long doctorDayId = Long.parseLong(request.get("doctorDayId").toString());
            Long slotId = Long.parseLong(request.get("slotId").toString());
            String appointmentDate = request.get("appointmentDate").toString();

            // Create Stripe session
            Session session = paymentService.createCheckoutSession(
                    userId, petId, doctorId, doctorDayId, slotId, appointmentDate
            );

            return ResponseEntity.ok(Map.of(
                    "sessionId", session.getId(),
                    "checkoutUrl", session.getUrl()
            ));

        } catch (StripeException e) {
            logger.error("Stripe error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Payment initialization failed: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating checkout session", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * ✅ Verify Payment and Book Appointment
     * GET /api/payments/verify/{sessionId}
     */
    @GetMapping("/verify/{sessionId}")
    public ResponseEntity<?> verifyPayment(@PathVariable String sessionId) {
        try {
            AppointmentPayment payment = paymentService.verifyPaymentAndBookAppointment(sessionId);

            return ResponseEntity.ok(Map.of(
                    "status", payment.getStatus(),
                    "message", payment.getStatus() == com.example.demo.Entities.AppointmentPayment.PaymentStatus.PAID
                            ? "Payment successful! Appointment booked." 
                            : "Payment failed",
                    "appointmentId", payment.getAppointment() != null 
                            ? payment.getAppointment().getId() 
                            : null
            ));

        } catch (StripeException e) {
            logger.error("Stripe verification error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Payment verification failed"));
        } catch (Exception e) {
            logger.error("Error verifying payment", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}