package com.example.demo.Controller;

import com.example.demo.Config.SpringSecurityAuditorAware;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.example.demo.Entities.AppointmentPayment;
import com.example.demo.Entities.AppointmentPayment.PaymentStatus;
import com.example.demo.Dto.AppointmentRequest;
import com.example.demo.Dto.DoctorDaysResponseDto;
import com.example.demo.Dto.SlotResponseDto;
import com.example.demo.Entities.Appointment;
import com.example.demo.Entities.DoctorDays;
import com.example.demo.Entities.DoctorsEntity;
import com.example.demo.Entities.UsersEntity;
import com.example.demo.Entities.DoctorSlots;
import com.example.demo.Enum.AppointmentStatus;
import com.example.demo.Enum.DayOfWeek;
import com.example.demo.Repository.AppointmentPaymentRepo;
import com.example.demo.Repository.DoctorRepo;
import com.example.demo.Service.AppointmentService;
import com.example.demo.Service.DoctorService;
import com.google.api.client.util.Value;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
	
    @Autowired
    private DoctorService doctorService;

    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);

    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private DoctorRepo doctorRepository;

    
    @Autowired
    private SpringSecurityAuditorAware auditorAware;
    
    @Autowired
    private AppointmentPaymentRepo appointmentPaymentRepo;

    
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;
    
    
    /**
     * ✅ API 1: Get all doctors available on a specific day
     * GET /api/appointments/doctors/by-day/{day}
     */
    @GetMapping("/doctors/by-day/{day}")
    public ResponseEntity<?> getDoctorsByDay(@PathVariable DayOfWeek day) {
        try {
        	
            List<DoctorsEntity> doctors = appointmentService.getDoctorsByDay(day);
            
            return ResponseEntity.ok(doctors);
            
        } catch (RuntimeException ex) {
        	
            logger.warn("getDoctorsByDay error: {}", ex.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
            
        }
    }

    /**
     * ✅ API 2: Get available slots for a doctor on a specific date
     * GET /api/appointments/available-slots?doctorId=1&doctorDayId=5&date=2025-11-10
     */
    @GetMapping("/available-slots")
    public ResponseEntity<?> getAvailableSlots(
            @RequestParam Long doctorId,
            @RequestParam Long doctorDayId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<DoctorSlots> availableSlots = appointmentService.getAvailableSlots(doctorId, doctorDayId, date);
            return ResponseEntity.ok(availableSlots);
        } catch (RuntimeException ex) {
            logger.warn("getAvailableSlots error: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }
    
    
    

    /**
     * ✅ API 3: Book an appointment
     * POST /api/appointments/book
     *
     * Frontend should NOT send userId. Backend will extract logged-in user from token/auditor.
     *
     * Request Body example:
     * {
     *   "petId": 1,              // Required for userType=1 (client), Optional for userType=2 (doctor)
     *   "doctorId": 5,
     *   "doctorDayId": 5,
     *   "slotId": 24,
     *   "appointmentDate": "2025-11-05"
     * }
     */
   @PostMapping("/book")
public ResponseEntity<?> bookAppointment(@RequestBody Map<String, Object> request) {
	   
    try {
        // 1) Get current logged-in user from auditorAware
        Optional<UsersEntity> currentUserOpt = auditorAware.getCurrentAuditor();
        if (currentUserOpt.isEmpty()) {
            logger.info("Unauthenticated attempt to book appointment");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
        }
        UsersEntity currentUser = currentUserOpt.get();

        // Use DB primary key (Long) for appointment.userId
        Long userId = currentUser.getId();
        if (userId == null) {
            logger.warn("Authenticated user has null database id (getId()). Cannot book.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Authenticated user has invalid id"));
        }

        // 2) Check required fields (except petId - it depends on userType)
        if (request.get("doctorId") == null
                || request.get("doctorDayId") == null 
                || request.get("slotId") == null
                || request.get("appointmentDate") == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Missing required fields. Required: doctorId, doctorDayId, slotId, appointmentDate"));
        }

        // 3) Extract petId (can be null)
        Long petId = (request.get("petId") != null) 
                ? Long.parseLong(request.get("petId").toString()) 
                : null;

        // 4) ✅ FIXED LOGIC: Check petId requirement based on userType
        int userType = getUserTypeAsInt(currentUser);
        
        if (userType == 1) {
            // userType = 1 (Client) -> petId is MANDATORY
            if (petId == null) {
                logger.warn("Client (userType=1) attempted to book without petId");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Pet selection is required for clients. Please select a pet."));
            }
        } else if (userType == 2) {
            // userType = 2 (Doctor) -> petId is OPTIONAL
            logger.debug("Doctor (userType=2) booking appointment. PetId: {}", petId);
        } else {
            // Unknown userType
            logger.warn("User with unknown userType {} attempted to book appointment", userType);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Invalid user type for booking appointments"));
        }

        // 5) Extract other required fields
        Long doctorId = Long.parseLong(request.get("doctorId").toString());
        Long doctorDayId = Long.parseLong(request.get("doctorDayId").toString());
        Long slotId = Long.parseLong(request.get("slotId").toString());
        LocalDate appointmentDate = LocalDate.parse(request.get("appointmentDate").toString());

        // 6) Get doctor details and consultation fee
        Optional<DoctorsEntity> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            logger.warn("Doctor not found with id: {}", doctorId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Doctor not found"));
        }
        
        DoctorsEntity doctor = doctorOpt.get();
        Double consultationFee = doctor.getConsultationFee();
        
        if (consultationFee == null || consultationFee <= 0) {
            logger.error("Invalid consultation fee for doctor {}", doctorId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid consultation fee"));
        }

        // 7) ✅ CREATE STRIPE CHECKOUT SESSION
        long amountInCents = (long) (consultationFee * 100);

        Stripe.apiKey = stripeSecretKey ;
        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:5173/payment-sucess?session_id={CHECKOUT_SESSION_ID}")
                
                .setCancelUrl("http://localhost:5173/payment-failed?session_id={CHECKOUT_SESSION_ID}")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Appointment with Dr. " + doctor.getUser().getFirstName()+ " " + doctor.getUser().getLastName())
                                                                .setDescription("Date: " + appointmentDate.toString() + 
                                                                              " | Consultation Fee: $" + consultationFee)
                                                                .build()
                                                )
                                                .build()
                                )
                                .setQuantity(1L)
                                .build()
                )
                .build();

        Session session = Session.create(params);
        
        logger.info("Stripe session created - SessionId: {}, Amount: ${}, User: {}", 
                session.getId(), consultationFee, userId);

        // 8) ✅ SAVE PAYMENT RECORD IN DATABASE (PENDING STATUS)
        AppointmentPayment payment = new AppointmentPayment();
        payment.setSessionId(session.getId());
        payment.setUserId(userId);
        payment.setDoctorId(doctorId);
        payment.setSlotId(slotId);
        payment.setAmount(consultationFee);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCheckoutUrl(session.getUrl());
        payment.setPetId(petId);
        payment.setDoctorDayId(doctorDayId);
        payment.setAppointmentDate(appointmentDate.toString());
        
        appointmentPaymentRepo.save(payment);
        
        logger.info("Payment record saved - SessionId: {}, User: {}, Doctor: {}", 
                session.getId(), userId, doctorId);

        // 9) ✅ RETURN CHECKOUT URL TO FRONTEND
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "message", "Payment session created successfully",
                "sessionId", session.getId(),
                "checkoutUrl", session.getUrl(),
                "amount", consultationFee,
                "doctorName", doctor.getUser().getFirstName()+doctor.getUser().getLastName(),
                "appointmentDate", appointmentDate.toString()
        ));
        
    } catch (StripeException ex) {
        logger.error("Stripe API error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Payment initialization failed: " + ex.getMessage()));
                
    } catch (RuntimeException ex) {
        logger.warn("bookAppointment runtime error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
                
    } catch (Exception ex) {
        logger.error("bookAppointment unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Unexpected error: " + ex.getMessage()));
    }
}
    
    

   
   
   
   
   /**
    * ✅ VERIFY PAYMENT AND BOOK APPOINTMENT
    * GET /api/appointments/verify-payment/{sessionId}
    * 
    * Called after user completes payment on Stripe
    * Automatically books appointment if payment is successful
    */
   @GetMapping("/verify-payment/{sessionId}")
   public ResponseEntity<?> verifyPaymentAndBook(@PathVariable String sessionId) {
       try {
           // 1) Retrieve payment record from database
           Optional<AppointmentPayment> paymentOpt = appointmentPaymentRepo.findBySessionId(sessionId);
           if (paymentOpt.isEmpty()) {
               logger.warn("Payment record not found for sessionId: {}", sessionId);
               return ResponseEntity.status(HttpStatus.NOT_FOUND)
                       .body(Map.of("error", "Payment record not found"));
           }
           
           AppointmentPayment payment = paymentOpt.get();
           
           // 2) Check if already processed
           if (payment.getStatus() == PaymentStatus.PAID && payment.getAppointment() != null) {
               logger.info("Payment already processed - SessionId: {}", sessionId);
               return ResponseEntity.ok(Map.of(
                       "status", "ALREADY_PROCESSED",
                       "message", "Appointment already booked",
                       "appointmentId", payment.getAppointment().getId()
               ));
           }
           
           // 3) Verify payment status with Stripe

           Stripe.apiKey = stripeSecretKey;
           Session session = Session.retrieve(sessionId);
           
           String paymentStatus = session.getPaymentStatus();
           logger.info("Stripe payment status - SessionId: {}, Status: {}", sessionId, paymentStatus);
           
           // 4) Update payment status and book appointment if paid
           if ("paid".equals(paymentStatus)) {
               payment.setStatus(PaymentStatus.PAID);
               
               // Book appointment automatically
               Appointment appointment = appointmentService.bookAppointment(
                       payment.getUserId(),	
                       payment.getPetId(),
                       payment.getDoctorId(),
                       payment.getDoctorDayId(),
                       payment.getSlotId(),
                       LocalDate.parse(payment.getAppointmentDate())
               );
               
               payment.setAppointment(appointment);
               
               appointmentPaymentRepo.save(payment);
               
               logger.info("Appointment booked successfully - SessionId: {}, AppointmentId: {}", 
                       sessionId, appointment.getId());
               
               return ResponseEntity.ok(Map.of(
                       "status", "SUCCESS",
                       "message", "Payment successful! Appointment booked.",
                       "paymentStatus", PaymentStatus.PAID,
                       "appointmentId", appointment.getId(),
                       "appointmentDate", appointment.getAppointmentDate().toString(),
                       "amount", payment.getAmount()
               ));
               
           } else if ("unpaid".equals(paymentStatus)) {
               payment.setStatus(PaymentStatus.FAILED);
               appointmentPaymentRepo.save(payment);
               
               logger.warn("Payment failed - SessionId: {}", sessionId);
               
               return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                       .body(Map.of(
                               "status", "FAILED",
                               "message", "Payment was not completed",
                               "paymentStatus", PaymentStatus.FAILED
                       ));
           } else {
               payment.setStatus(PaymentStatus.PENDING);
               appointmentPaymentRepo.save(payment);
               return ResponseEntity.ok(Map.of(
                       "status", "PENDING",
                       "message", "Payment is still processing",
                       "paymentStatus", PaymentStatus.PENDING
               ));
           }
           
       } catch (StripeException ex) {
           logger.error("Stripe verification error: {}", ex.getMessage(), ex);
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body(Map.of("error", "Payment verification failed: " + ex.getMessage()));
                   
       } catch (RuntimeException ex) {
           logger.error("Runtime error during payment verification: {}", ex.getMessage(), ex);
           return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                   .body(Map.of("error", ex.getMessage()));
                   
       } catch (Exception ex) {
           logger.error("Unexpected error during payment verification", ex);
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body(Map.of("error", "Unexpected error: " + ex.getMessage()));
       }
   }
   
    
    
    
    
   

     

    /**
     * ✅ OFFLINE APPOINTMENT BOOKING
     * POST /api/appointments/book-offline
     * 
     * Required fields:
     * - userId (must have userType = 2)
     * - doctorId
     * - doctorDayId
     * - slotId
     * - appointmentDate
     * 
     * Optional field:
     * - petId (can be null for walk-in customers)
     */
   @PostMapping("/book-offline-simple")
public ResponseEntity<?> bookOfflineAppointmentSimple(
        @RequestBody Map<String, Object> request
) {
    try {
        // 1️⃣ Get logged-in user from Spring Security context
        Optional<UsersEntity> currentUserOpt = auditorAware.getCurrentAuditor();
        if (currentUserOpt.isEmpty()) {
            logger.warn("Unauthenticated attempt to book offline appointment");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
        }
        
        UsersEntity currentUser = currentUserOpt.get();
        Long loggedInUserId = currentUser.getId();
        
        if (loggedInUserId == null) {
            logger.warn("Authenticated user has null database id");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Authenticated user has invalid id"));
        }
        
        // 2️⃣ Verify logged-in user is a doctor (userType = 2)
        int userType = getUserTypeAsInt(currentUser);
        if (userType != 2) {
            logger.warn("Non-doctor user {} attempted to book offline appointment", loggedInUserId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Only doctors can book offline appointments"));
        }
        
        // 3️⃣ Validate required fields
        if (request.get("doctorId") == null
                || request.get("doctorDayId") == null 
                || request.get("slotId") == null
                || request.get("appointmentDate") == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Missing required fields: doctorId, doctorDayId, slotId, appointmentDate"));
        }
        
        // 4️⃣ Extract booking details
        Long doctorId = Long.parseLong(request.get("doctorId").toString());
        Long doctorDayId = Long.parseLong(request.get("doctorDayId").toString());
        Long slotId = Long.parseLong(request.get("slotId").toString());
        LocalDate appointmentDate = LocalDate.parse(request.get("appointmentDate").toString());
        
        // 5️⃣ Book offline appointment (userId = logged-in doctor, petId = null)
        Appointment appointment = appointmentService.bookOfflineAppointmentSimple(
                loggedInUserId, // Logged-in doctor ki ID
                doctorId,
                doctorDayId,
                slotId,
                appointmentDate
        );
        
        logger.info("Offline appointment booked by doctor {} for doctor {} on {}", 
                loggedInUserId, doctorId, appointmentDate);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
        
    } catch (RuntimeException ex) {
        logger.warn("bookOfflineAppointmentSimple error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    } catch (Exception ex) {
        logger.error("bookOfflineAppointmentSimple unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Unexpected error: " + ex.getMessage()));
    }
}
    /**
     * ✅ ALTERNATIVE: Request Body approach (better for complex data)
     */
    @PostMapping("/book-offline-v2")
    public ResponseEntity<?> bookOfflineAppointmentV2(
            @RequestBody OfflineBookingRequest request
    ) {
        try {
            LocalDate date = LocalDate.parse(request.getAppointmentDate());
            
            Appointment appointment = appointmentService.bookOfflineAppointment(
                request.getUserId(),
                request.getDoctorId(),
                request.getDoctorDayId(),
                request.getSlotId(),
                date,
                request.getPetId()  // Can be null
            );
            
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(appointment);
                    
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error: " + e.getMessage());
        }
    }
    
    // DTO Class for Request Body
    public static class OfflineBookingRequest {
        private Long userId;
        private Long doctorId;
        private Long doctorDayId;
        private Long slotId;
        private String appointmentDate;
        private Long petId;  // Optional
        
        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public Long getDoctorId() { return doctorId; }
        
        public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
       
        public Long getDoctorDayId() { return doctorDayId; }
        public void setDoctorDayId(Long doctorDayId) { this.doctorDayId = doctorDayId; }
        
        public Long getSlotId() { return slotId; }
        public void setSlotId(Long slotId) { this.slotId = slotId; }
        
        public String getAppointmentDate() { return appointmentDate; }
        public void setAppointmentDate(String appointmentDate) { 
            this.appointmentDate = appointmentDate; 
        }
        
        public Long getPetId() { return petId; }
        public void setPetId(Long petId) { this.petId = petId; }
    }
    
    

    /**
     * API 4: Get all appointments for a user
     * GET /api/appointments/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserAppointments(@PathVariable Long userId) {
        try {
            List<Appointment> appointments = appointmentService.getUserAppointments(userId);
            return ResponseEntity.ok(appointments);
        } catch (RuntimeException ex) {
            logger.warn("getUserAppointments error: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * API 5: Get all appointments for a doctor
     * GET /api/appointments/doctor/{doctorId}
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getDoctorAppointments(@PathVariable Long doctorId) {
        try {
            List<Appointment> appointments = appointmentService.getDoctorAppointments(doctorId);
            return ResponseEntity.ok(appointments);
        } catch (RuntimeException ex) {
            logger.warn("getDoctorAppointments error: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * NEW API: Get appointments for a doctor with optional date and status filters
     * GET /api/appointments/doctor/{doctorId}/filter?date=2025-11-10&status=BOOKED
     */
    @GetMapping("/doctor/{doctorId}/filter")
    public ResponseEntity<?> getDoctorAppointmentsFiltered(
            @PathVariable Long doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) AppointmentStatus status) {
        try {
            List<Appointment> appointments = appointmentService.getDoctorAppointments(doctorId, date, status);
            return ResponseEntity.ok(Map.of(
                    "doctorId", doctorId,
                    "date", date,
                    "status", status,
                    "totalAppointments", appointments.size(),
                    "appointments", appointments
            ));
        } catch (RuntimeException ex) {
            logger.warn("getDoctorAppointmentsFiltered error: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            logger.error("getDoctorAppointmentsFiltered unexpected error", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected error: " + ex.getMessage()));
        }
    }

    /**
     * ✅ FIXED API: Get appointments for the logged-in doctor (from access token)
     * GET /api/appointments/my-appointments-doctor?date=2025-11-10&status=BOOKED
     *
     * This extracts doctor UID from the access token via SpringSecurityAuditorAware,
     * then converts it to doctorId using DoctorService
     */
    @GetMapping("/my-appointments-doctor")
    public ResponseEntity<?> getMyAppointmentsDoctor(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) AppointmentStatus status) {
        try {
            // 1) Get current logged-in user from token
            Optional<UsersEntity> currentUserOpt = auditorAware.getCurrentAuditor();
            if (currentUserOpt.isEmpty()) {
                logger.warn("Unauthenticated attempt to access doctor appointments");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }
            
            UsersEntity currentUser = currentUserOpt.get();

            // Use UID (UUID) for doctor lookup
            UUID userUid = currentUser.getUid();
            if (userUid == null) {
                logger.warn("Authenticated user has null UID; cannot resolve doctor profile");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Authenticated user UID is invalid"));
            }

            logger.debug("Fetching appointments for user uid: {}", userUid);
            
            // 2) Convert user uid to doctor id using DoctorService
            Long doctorId = doctorService.getDoctorIdByUserUid(userUid);
            
            if (doctorId == null) {
                logger.warn("Doctor profile not found for user uid: {}", userUid);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Doctor profile not found for user uid: " + userUid));
            }
            
            logger.debug("Resolved doctorId {} for userUid {}", doctorId, userUid);
            
            // 3) Fetch appointments using doctorId
            List<Appointment> appointments = appointmentService.getDoctorAppointments(doctorId, date, status);
            
            return ResponseEntity.ok(Map.of(
                    "userUid", userUid,
                    "doctorId", doctorId,
                    "date", date != null ? date.toString() : "all",
                    "status", status != null ? status.toString() : "all",
                    "totalAppointments", appointments.size(),
                    "appointments", appointments
            ));
            
        } catch (RuntimeException ex) {
            logger.error("getMyAppointmentsDoctor runtime error: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            logger.error("getMyAppointmentsDoctor unexpected error", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error: " + ex.getMessage()));
        }
    }

    /**
     * API 7: Get appointments by status for a user
     * GET /api/appointments/user/{userId}/status/{status}
     */
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<?> getAppointmentsByStatus(
            @PathVariable Long userId,
            @PathVariable AppointmentStatus status) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByStatus(userId, status);
            return ResponseEntity.ok(appointments);
        } catch (RuntimeException ex) {
            logger.warn("getAppointmentsByStatus error: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * API 8: Cancel an appointment
     * PUT /api/appointments/{appointmentId}/cancel
     */
    @PutMapping("/{appointmentId}/cancel")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long appointmentId) {
        try {
            Appointment appointment = appointmentService.cancelAppointment(appointmentId);
            return ResponseEntity.ok(appointment);
        } catch (RuntimeException ex) {
            logger.warn("cancelAppointment error: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * API 9: Update appointment status
     * PUT /api/appointments/{appointmentId}/status
     */
    @PutMapping("/{appointmentId}/status")
    public ResponseEntity<?> updateAppointmentStatus(
            @PathVariable Long appointmentId,
            @RequestBody Map<String, String> request) {
        try {
            AppointmentStatus status = AppointmentStatus.valueOf(request.get("status"));
            Appointment appointment = appointmentService.updateAppointmentStatus(appointmentId, status);
            return ResponseEntity.ok(appointment);
        } catch (RuntimeException ex) {
            logger.warn("updateAppointmentStatus error: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * ✅ API 10: Get booked appointments
     * GET /api/appointments/booked?doctorId=1&date=2025-11-10
     */
    @GetMapping("/booked")
    public ResponseEntity<?> getBookedAppointments(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<Appointment> appointments = appointmentService
                    .getBookedAppointmentsByDoctorAndDate(doctorId, date);

            return ResponseEntity.ok(Map.of(
                    "doctorId", doctorId,
                    "date", date,
                    "totalAppointments", appointments.size(),
                    "appointments", appointments
            ));
        } catch (RuntimeException ex) {
            logger.warn("getBookedAppointments error: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * ✅ API 11: Get all appointments for logged-in user
     * GET /api/appointments/my-appointments
     */
    @GetMapping("/my-appointments")
    public ResponseEntity<?> getMyAppointments() {
        try {
            // 1) Get current logged-in user from token
            Optional<UsersEntity> currentUserOpt = auditorAware.getCurrentAuditor();
            if (currentUserOpt.isEmpty()) {
                logger.info("Unauthenticated attempt to fetch appointments");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            UsersEntity currentUser = currentUserOpt.get();

            // Use DB primary key (Long) to fetch user's appointments
            Long userId = currentUser.getId();
            if (userId == null) {
                logger.warn("Authenticated user has null database id (getId()). Cannot fetch appointments.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Authenticated user has invalid id"));
            }

            // 2) Fetch appointments for this user
            List<Appointment> appointments = appointmentService.getUserAppointments(userId);

            // 3) Return all appointments
            return ResponseEntity.ok(Map.of(
                    "userId", userId,
                    "totalAppointments", appointments.size(),
                    "appointments", appointments
            ));

        } catch (RuntimeException ex) {
            logger.warn("getMyAppointments error: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * ✅ API 12: Delete an appointment (Admin/Direct delete)
     * DELETE /api/appointments/{appointmentId}
     *
     * This permanently deletes the appointment from database
     */
    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long appointmentId) {
        try {
            appointmentService.deleteAppointment(appointmentId);
            return ResponseEntity.ok(Map.of(
                    "message", "Appointment deleted successfully",
                    "appointmentId", appointmentId
            ));
        } catch (RuntimeException ex) {
            logger.warn("deleteAppointment error: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * ✅ API 13: Delete own appointment (User delete with ownership verification)
     * DELETE /api/appointments/my-appointments/{appointmentId}
     *
     * This allows users to delete only their own appointments
     * Automatically gets userId from logged-in user
     */
    @DeleteMapping("/my-appointments/{appointmentId}")
    public ResponseEntity<?> deleteMyAppointment(@PathVariable Long appointmentId) {
        try {
            // 1) Get current logged-in user from token
            Optional<UsersEntity> currentUserOpt = auditorAware.getCurrentAuditor();
            if (currentUserOpt.isEmpty()) {
                logger.info("Unauthenticated attempt to delete appointment");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            UsersEntity currentUser = currentUserOpt.get();
            Long userId = currentUser.getId();
            if (userId == null) {
                logger.warn("Authenticated user has null database id (getId()). Cannot delete appointment.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Authenticated user has invalid id"));
            }

            // 2) Delete appointment with ownership verification
            appointmentService.deleteAppointmentByUser(appointmentId, userId);

            return ResponseEntity.ok(Map.of(
                    "message", "Appointment deleted successfully",
                    "appointmentId", appointmentId,
                    "userId", userId
            ));

        } catch (RuntimeException ex) {
            logger.warn("deleteMyAppointment error: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    // ----------------- Helper methods -----------------

    /**
     * ✅ NEW HELPER: Get userType as integer
     * Handles various userType representations (Integer, Long, String, etc.)
     */
    private int getUserTypeAsInt(UsersEntity user) {
        if (user == null || user.getUserType() == null) {
            return -1; // Unknown
        }
        
        Object userType = user.getUserType();
        
        if (userType instanceof Integer) {
            return ((Integer) userType).intValue();
        }
        if (userType instanceof Long) {
            return ((Long) userType).intValue();
        }
        if (userType instanceof String) {
            String s = ((String) userType).trim();
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                // Handle string representations like "CLIENT" or "DOCTOR"
                if (s.equalsIgnoreCase("client") || s.equalsIgnoreCase("user")) {
                    return 1;
                } else if (s.equalsIgnoreCase("doctor")) {
                    return 2;
                }
                return -1;
            }
        }
        
        // Try toString() as last resort
        try {
            return Integer.parseInt(userType.toString());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * ✅ IMPROVED: Null-safe check if the user is a doctor.
     * Assumes doctor can be represented by:
     *  - numeric value 2 (Integer/Long) OR
     *  - String "DOCTOR" (case-insensitive)
     */
    private boolean isDoctor(UsersEntity user) {
        if (user == null) {
            logger.debug("isDoctor check: user is null");
            return false;
        }
        
        Object userType = user.getUserType();
        if (userType == null) {
            logger.debug("isDoctor check: userType is null for user {}", user.getUid());
            return false;
        }

        logger.debug("isDoctor check: user {} has userType {} (class: {})", 
                user.getUid(), userType, userType.getClass().getSimpleName());

        // Integer / Long checks
        if (userType instanceof Integer) {
            boolean result = ((Integer) userType).intValue() == 2;
            logger.debug("isDoctor (Integer): {}", result);
            return result;
        }
        if (userType instanceof Long) {
            boolean result = ((Long) userType).longValue() == 2L;
            logger.debug("isDoctor (Long): {}", result);
            return result;
        }

        // String check (e.g., "DOCTOR", "doctor", or "2")
        if (userType instanceof String) {
            String s = ((String) userType).trim();
            if (s.equalsIgnoreCase("doctor")) {
                logger.debug("isDoctor (String match): true");
                return true;
            }
            try {
                boolean result = Integer.parseInt(s) == 2;
                logger.debug("isDoctor (String numeric): {}", result);
                return result;
            } catch (NumberFormatException ignore) {
                logger.debug("isDoctor (String): not a doctor string");
                return false;
            }
        }

        // Enum or other type: try toString match
        try {
            String s = userType.toString();
            if (s.equalsIgnoreCase("doctor")) {
                logger.debug("isDoctor (toString match): true");
                return true;
            }
            try {
                boolean result = Integer.parseInt(s) == 2;
                logger.debug("isDoctor (toString numeric): {}", result);
                return result;
            } catch (NumberFormatException ignore) {
                logger.debug("isDoctor (toString): not a doctor");
                return false;
            }
        } catch (Exception e) {
            logger.error("isDoctor check exception: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Null-safe check if the user is a client (userType == 1)
     */
    private boolean isClient(UsersEntity user) {
        if (user == null) return false;
        Object userType = user.getUserType();
        if (userType == null) return false;

        if (userType instanceof Integer) {
            return ((Integer) userType).intValue() == 1;
        }
        if (userType instanceof Long) {
            return ((Long) userType).longValue() == 1L;
        }
        if (userType instanceof String) {
            String s = ((String) userType).trim();
            if (s.equalsIgnoreCase("client") || s.equalsIgnoreCase("user")) return true;
            try {
                return Integer.parseInt(s) == 1;
            } catch (NumberFormatException ignore) {
                return false;
            }
        }
        try {
            String s = userType.toString();
            if (s.equalsIgnoreCase("client") || s.equalsIgnoreCase("user")) return true;
            try {
                return Integer.parseInt(s) == 1;
            } catch (NumberFormatException ignore) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    
  
    @GetMapping("/getSlot")
    public ResponseEntity<?> getAvailableSlotsforDashboad(
            @RequestParam Long doctorId,
            @RequestParam Long doctorDayId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<DoctorSlots> availableSlots = appointmentService.getAvailableSlots(doctorId, doctorDayId, date);
            
            List<SlotResponseDto> response = availableSlots.stream().map(this::mapToDto).toList();
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            logger.warn("getAvailableSlots error: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    
    }
    
    private SlotResponseDto mapToDto(DoctorSlots slot) {

        
        SlotResponseDto dto = new SlotResponseDto();
        
        dto.setDay(slot.getDoctorDay().getDayOfWeek().toString());
        dto.setDoctorDayId(slot.getDoctorId());
        dto.setDoctorDayUid(slot.getDoctorDay().getUid());
        dto.setEndTime(slot.getEndTime());
        dto.setSlotId(slot.getSlotIdForJson());
        dto.setSlotUid(slot.getUid());
        dto.setStartTime(slot.getStartTime());
        
        

        return dto;
    }

        
        
    
 
    @PostMapping("/dummy-book")
public ResponseEntity<?> bookOppointmentDummy(@RequestBody Map<String, Object> request) {

    try {
        // Logged-in user nikalo
        Optional<UsersEntity> currentUserOpt = auditorAware.getCurrentAuditor();
        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
        }

        UsersEntity loginedUser = currentUserOpt.get();
        long userId = loginedUser.getId();   // <-- UUID

        // Request parsing
        Long petId = (request.get("petId") != null)
                ? Long.parseLong(request.get("petId").toString())
                : null;

        Long doctorId = Long.parseLong(request.get("doctorId").toString());
        Long doctorDayId = Long.parseLong(request.get("doctorDayId").toString());
        Long slotId = Long.parseLong(request.get("slotId").toString());
        LocalDate appointmentDate = LocalDate.parse(request.get("appointmentDate").toString());

        // Service call (Service method ka signature: bookAppointment(UUID userId, ...))
        Appointment appointment = appointmentService.bookAppointment(
                userId,
                petId,
                doctorId,
                doctorDayId,
                slotId,
                appointmentDate
        );

        return ResponseEntity.ok(Map.of(
                "message", "Appointment booked successfully",
                "appointment", appointment
        ));

    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Something went wrong"));
    }
}

    
    
    
    
    
    
    
    
    
    
    
    
}