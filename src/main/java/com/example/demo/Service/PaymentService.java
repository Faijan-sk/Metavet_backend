package com.example.demo.Service;

import com.example.demo.Entities.Appointment;
import com.example.demo.Entities.AppointmentPayment;
import com.example.demo.Entities.AppointmentPayment.PaymentStatus;
import com.example.demo.Entities.DoctorsEntity;

import com.example.demo.Repository.AppointmentPaymentRepo;
import com.example.demo.Repository.DoctorRepo;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class PaymentService {

	 @Autowired
	 private SecretKeyService secrateKeyService;
	    

    @Value("${stripe.success.url}")
    private String successUrl;

    @Value("${stripe.cancel.url}")
    private String cancelUrl;

    @Autowired
    private AppointmentPaymentRepo paymentRepo;

    @Autowired
    private DoctorRepo doctorRepo;

    @Autowired
    private AppointmentService appointmentService;

    /**
     * Create Stripe Checkout Session for appointment payment
     */
    public Session createCheckoutSession(
            Long userId,
            Long petId,
            Long doctorId,
            Long doctorDayId,
            Long slotId,
            String appointmentDate
    ) throws StripeException {
        
        // Set Stripe API key
        Stripe.apiKey =  secrateKeyService.getValueByKeyName("STRIPE_SECRET_KEY_TEST");

        // Get doctor details and consultation fee
        DoctorsEntity doctor = doctorRepo.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Double consultationFee = doctor.getConsultationFee();
        long amountInCents = (long) (consultationFee * 100);

        // Create Stripe Checkout Session
        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(cancelUrl + "?session_id={CHECKOUT_SESSION_ID}")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Appointment with Dr. " + doctor.getUser().getFirstName() +doctor.getUser().getLastName() )
                                                                .setDescription("Date: " + appointmentDate)
                                                                .build()
                                                )
                                                .build()
                                )
                                .setQuantity(1L)
                                .build()
                )
                .build();

        Session session = Session.create(params);

        // Save payment record in database
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
        payment.setAppointmentDate(appointmentDate);

        paymentRepo.save(payment);

        return session;
    }

    /**
     * Verify payment status and book appointment if paid
     */
    @Transactional
    public AppointmentPayment verifyPaymentAndBookAppointment(String sessionId) throws StripeException {
        
        Stripe.apiKey = secrateKeyService.getValueByKeyName("STRIPE_SECRET_KEY_TEST");

        // Retrieve payment from database
        AppointmentPayment payment = paymentRepo.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Payment record not found"));

        // Get payment status from Stripe
        Session session = Session.retrieve(sessionId);
        String paymentStatus = session.getPaymentStatus();

        // Update payment status
        if ("paid".equals(paymentStatus)) {
            payment.setStatus(com.example.demo.Entities.AppointmentPayment.PaymentStatus.PAID);

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
            
        } else if ("unpaid".equals(paymentStatus)) {
            payment.setStatus(com.example.demo.Entities.AppointmentPayment.PaymentStatus.FAILED);
        }

        return paymentRepo.save(payment);
    }
}