package com.example.demo.Controller;


import com.example.demo.Dto.*;
import com.example.demo.Service.GroomerAppointmentService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;





@RestController
@RequestMapping("/groomer/appointment")
public class GroomerAppointmentController {

	
	

	    @Autowired
	    private GroomerAppointmentService  groomerScheduleService;

	    // ==================== GROOMER SETUP ENDPOINTS ====================

	    /**
	     * Groomer sets up weekly schedule
	     * POST /api/groomer/setup/schedule/{groomerUid}
	     */
	    @PostMapping("/days")
	    public ResponseEntity<Map<String, Object>> setupSchedule(
	            
	            @Valid @RequestBody List<GroomerAvailableDayRequestDto> schedules) {

	    	
	    	
	    	
	        Map<String, Object> response = groomerScheduleService.setupGroomerSchedule(schedules);
	        return ResponseEntity.ok(response);
	    }

	    
	   
	    
	    
	    
	    /**
	     * Groomer sets up services
	     * POST /api/groomer/setup/services/{groomerUid}
	     */
	    @PostMapping("/services")
	    public ResponseEntity<Map<String, Object>> setupServices(
	          
	            @Valid @RequestBody List<GroomerServiceRequestDto> services) {

	        Map<String, Object> response = groomerScheduleService.setupGroomerServices( services);
	        return ResponseEntity.ok(response);
	    }

	    /**
	     * Get groomer's available days (Scenario 3 - Step 1)
	     * GET /api/groomer/{groomerUid}/available-days
	     */
	    @GetMapping("/{groomerUid}/available-days")
	    public ResponseEntity<Map<String, Object>> getAvailableDays(@PathVariable String groomerUid) {
	        Map<String, Object> response = groomerScheduleService.getGroomerAvailableDays(groomerUid);
	        return ResponseEntity.ok(response);
	    }

	    // ==================== CLIENT BOOKING ENDPOINTS ====================

	    /**
	     * SCENARIO 1 & 2: Get available groomers by date
	     * GET /api/groomer/available-by-date?date=2026-03-03
	     */
	    @GetMapping("/available-by-date")
	    public ResponseEntity<List<GroomerInfoDTO>> getAvailableGroomersByDate(
	            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

	        List<GroomerInfoDTO> groomers = groomerScheduleService.getAvailableGroomersByDate(date);
	        return ResponseEntity.ok(groomers);
	    }

	    /**
	     * Get groomer's services
	     * GET /api/groomer/{groomerUid}/services
	     */
	    @GetMapping("/{groomerUid}/services")
	    public ResponseEntity<List<GroomerServiceResponseDto>> getGroomerServices(@PathVariable String groomerUid) {
	        List<GroomerServiceResponseDto> services = groomerScheduleService.getGroomerServices(groomerUid);
	        return ResponseEntity.ok(services);
	    }

	    /**
	     * SCENARIO 2 & 3: Get available slots with compatible services
	     * GET /api/groomer/{groomerUid}/availability?date=2026-03-03
	     */
	    @GetMapping("/{groomerUid}/availability")
	    public ResponseEntity<GroomerAvailabilityResponseDto> getAvailability(
	            @PathVariable String groomerUid,
	            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

	        GroomerAvailabilityResponseDto availability = groomerScheduleService.getAvailableSlotsWithServices(groomerUid, date);
	        return ResponseEntity.ok(availability);
	    }

	    /**
	     * Book an appointment
	     * POST /api/groomer/book/{clientUid}
	     */
	    @PostMapping("/book")
	    public ResponseEntity<Map<String, Object>> bookAppointment(
	            
	            @Valid @RequestBody GroomerAppointmentBookingRequestDto request) {

	        Map<String, Object> response = groomerScheduleService.bookAppointment( request);
	        return ResponseEntity.ok(response);
	    }
}
