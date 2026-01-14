package com.example.demo.Controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Config.SpringSecurityAuditorAware;
import com.example.demo.Dto.DoctorDayRequest;
import com.example.demo.Dto.SlotResponseDtoForDoctor;
import com.example.demo.Entities.DoctorDays;
import com.example.demo.Entities.DoctorSlots;
import com.example.demo.Entities.DoctorsEntity;
import com.example.demo.Entities.UsersEntity;
import com.example.demo.Enum.DayOfWeek;
import com.example.demo.Service.DoctorDaysService;
import com.example.demo.Service.DoctorService;

@RestController
@RequestMapping("/api/doctor-days")
public class DoctorDaysController {
    @Autowired
    private DoctorDaysService doctorDaysService;

    @Autowired
    private DoctorService doctorService;
    
    
    @Autowired
    private SpringSecurityAuditorAware auditorAware;


    /**
     * Endpoint unchanged: POST /api/doctor-days/doctor/{userUid}/days
     * Note: userUid is the User's UID (UUID) from BaseEntity (passed as path value).
     * We accept it as String and parse to UUID internally (no URL change).
     */
    @PostMapping("/days")
    public ResponseEntity<?> addDoctorDays( @RequestBody List<DoctorDayRequest> dayRequests) {

    	
    	
    	Optional<UsersEntity> currentUserOpt = auditorAware.getCurrentAuditor();
    	
    	if(currentUserOpt.get() == null) {
    	
    		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Please Login"));
    		
    		
    	}
    	
    	
        try {
//            if (userUid == null || userUid.trim().isEmpty()) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                        .body(Map.of("error", "User uid is required in path"));
//            }

            // Parse userUid to UUID safely
            UUID parsedUid;
            try {
                parsedUid = currentUserOpt.get().getUid();
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Invalid userUid format. Must be a UUID string."));
            }

            // Fetch doctorId using doctorService (resolves UsersEntity.uid -> DoctorsEntity.id)
            Long doctorId = doctorService.getDoctorIdByUserUid(parsedUid);

            if (doctorId == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Doctor profile not found for user uid: " ));
            }

            List<DoctorDays> createdDays = doctorDaysService.createDaysForDoctor(doctorId, dayRequests);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdDays);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error", "details", ex.getMessage()));
        }
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getDoctorDays(@PathVariable long doctorId) {
        try {
            List<DoctorDays> days = doctorDaysService.getDoctorDaysFromDoctor(doctorId);
            return ResponseEntity.ok(days);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/day/{day}/details")
    public ResponseEntity<?> getDoctorDaysByDay(@PathVariable DayOfWeek day) {
        try {
            List<DoctorDays> doctorDays = doctorDaysService.getDoctorDaysByDay(day);
            return ResponseEntity.ok(doctorDays);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/day/{day}")
    public ResponseEntity<?> getDoctorsByDay(@PathVariable DayOfWeek day) {
        try {
            List<DoctorsEntity> doctors = doctorDaysService.getDoctorsByDay(day);
            return ResponseEntity.ok(doctors);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/day/{day}/specializations")
    public ResponseEntity<?> getSpecializationsByDay(@PathVariable DayOfWeek day) {
        try {
            List<String> specializations = doctorDaysService.getSpecializationsByDay(day);
            return ResponseEntity.ok(specializations);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<?> getDoctorsByDayAndSpecialization(
            @RequestParam DayOfWeek day,
            @RequestParam String specialization) {
        try {
            List<DoctorsEntity> doctors = doctorDaysService.getDoctorsByDayAndSpecialization(day, specialization);
            return ResponseEntity.ok(doctors);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/doctor/{doctorId}/slots")
    public ResponseEntity<?> getDoctorSlots(@PathVariable long doctorId) {
        try {
            List<DoctorSlots> slots = doctorDaysService.getDoctorSlots(doctorId);
            return ResponseEntity.ok(slots);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/doctor-day/{doctorDayId}/slots")
    public ResponseEntity<?> getSlotsForDoctorDay(@PathVariable long doctorDayId) {
        try {
            List<DoctorSlots> slots = doctorDaysService.getSlotsForDoctorDay(doctorDayId);
            return ResponseEntity.ok(slots);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/find-doctors")
    public ResponseEntity<?> findDoctorsByDay(@RequestParam DayOfWeek day) {
        try {
            List<DoctorsEntity> doctors = doctorDaysService.getDoctorsByDay(day);
            return ResponseEntity.ok(doctors);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/getDayId/{doctorId}/{day}")
    public ResponseEntity<?> getDayIdByDoctorAndDay(
            @PathVariable("doctorId") long doctorId,
            @PathVariable("day") DayOfWeek day) {
        try {
            Map<String, Long> result = doctorDaysService.getDoctorDayIdByDoctorAndDay(doctorId, day);
            return ResponseEntity.ok(result);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        }
    }
    
    
    
    /*
     * yaha mai doctor extract kar raha hu access token se or user uske available day return kar raha hu
     * 
     */ 
   @GetMapping("/getSelfDays")
public ResponseEntity<?> getDoctorDaysSelf() {
    try {

        Optional<UsersEntity> currentUserOpt = auditorAware.getCurrentAuditor();

        Map<String, Object> result =
                doctorDaysService.getDoctorAvailableDays(
                        currentUserOpt.get().getUid()
                );

        List<?> days = (List<?>) result.get("days");

        if (days.isEmpty()) {
            return ResponseEntity.ok(
                    Map.of(
                            "status", false,
                            "message", "Doctor has not configured availability days yet",
                            "doctorId", result.get("doctorId"),
                            "days", List.of()
                    )
            );
        }

        return ResponseEntity.ok(
                Map.of(
                        "status", true,
                        "message", "Doctor available days fetched successfully",
                        "doctorId", result.get("doctorId"),
                        "days", result.get("days")
                )
        );

    } catch (RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        Map.of(
                                "status", false,
                                "message", ex.getMessage()
                        )
                );
    }
}


    
    
   @GetMapping("/getSelfAvailableSlots")
public ResponseEntity<?> getDoctorAvailableSlots(@RequestParam String date) {
    try {
        // Get current doctor from access token
        Optional<UsersEntity> currentUserOpt = auditorAware.getCurrentAuditor();
        
        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", false,
                            "message", "User not authenticated"
                    ));
        }
        
        // Get available slots for the date
        List<SlotResponseDtoForDoctor> availableSlots = doctorDaysService.getAvailableSlotsForDate(
                currentUserOpt.get().getUid(),
                date
        );
        
        if (availableSlots.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "status", true,
                    "message", "No available slots found for the given date",
                    "date", date,
                    "slots", List.of()
            ));
        }
        
        return ResponseEntity.ok(Map.of(
                "status", true,
                "message", "Available slots fetched successfully",
                "date", date,
                "totalSlots", availableSlots.size(),
                "slots", availableSlots
        ));
        
    } catch (RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "status", false,
                        "message", ex.getMessage()
                ));
    }
}
    
}
