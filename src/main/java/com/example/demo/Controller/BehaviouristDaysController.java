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
import com.example.demo.Dto.BehaviouristDayRequest;
import com.example.demo.Dto.SlotResponseDtoForBehaviourist;
import com.example.demo.Entities.BehaviouristAvailableDay;
import com.example.demo.Entities.BehaviouristSlots;
import com.example.demo.Entities.ServiceProvider;
import com.example.demo.Entities.UsersEntity;
import com.example.demo.Enum.DayOfWeek;
import com.example.demo.Repository.ServiceProviderRepo;
import com.example.demo.Service.BehaviouristDaysService;

@RestController
@RequestMapping("/api/behaviourist-days")
public class BehaviouristDaysController {
    
    @Autowired
    private BehaviouristDaysService behaviouristDaysService;

    @Autowired
    private ServiceProviderRepo serviceProviderRepository;
    
    @Autowired
    private SpringSecurityAuditorAware auditorAware;

    /**
     * Create availability days for logged-in behaviourist
     * POST /api/behaviourist-days/days
     */
    @PostMapping("/days")
    public ResponseEntity<?> addBehaviouristDays(@RequestBody List<BehaviouristDayRequest> dayRequests) {

        Optional<UsersEntity> currentUserOpt = auditorAware.getCurrentAuditor();
        
        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Please Login"));
        }

        try {
            UUID userUid = currentUserOpt.get().getUid();

            // Get ServiceProvider from user UID
            ServiceProvider serviceProvider = serviceProviderRepository.findByOwner_Uid(userUid)
                    .orElseThrow(() -> new RuntimeException("Service Provider profile not found for user"));

            UUID serviceProviderUid = serviceProvider.getUid();

            List<BehaviouristAvailableDay> createdDays = 
                    behaviouristDaysService.createDaysForBehaviourist(serviceProviderUid, dayRequests);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdDays);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error", "details", ex.getMessage()));
        }
    }

    /**
     * Get available days for current logged-in behaviourist
     * GET /api/behaviourist-days/getSelfDays
     */
    @GetMapping("/getSelfDays")
    public ResponseEntity<?> getBehaviouristDaysSelf() {
        try {
            Optional<UsersEntity> currentUserOpt = auditorAware.getCurrentAuditor();

            if (currentUserOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("status", false, "message", "User not authenticated"));
            }

            Map<String, Object> result = 
                    behaviouristDaysService.getBehaviouristAvailableDays(currentUserOpt.get().getUid());

            List<?> days = (List<?>) result.get("days");

            if (days.isEmpty()) {
                return ResponseEntity.ok(
                        Map.of(
                                "status", false,
                                "message", "Behaviourist has not configured availability days yet",
                                "serviceProviderUid", result.get("serviceProviderUid"),
                                "days", List.of()
                        )
                );
            }

            return ResponseEntity.ok(
                    Map.of(
                            "status", true,
                            "message", "Behaviourist available days fetched successfully",
                            "serviceProviderUid", result.get("serviceProviderUid"),
                            "days", result.get("days")
                    )
            );

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", false, "message", ex.getMessage()));
        }
    }

    /**
     * Get available slots for current logged-in behaviourist for a specific date
     * GET /api/behaviourist-days/getSelfAvailableSlots?date=YYYY-MM-DD
     */
//    @GetMapping("/getSelfAvailableSlots")
//    public ResponseEntity<?> getBehaviouristAvailableSlots(@RequestParam String date) {
//        try {
//            Optional<UsersEntity> currentUserOpt = auditorAware.getCurrentAuditor();
//            
//            if (currentUserOpt.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body(Map.of("status", false, "message", "User not authenticated"));
//            }
//            
//            List<SlotResponseDtoForBehaviourist> availableSlots = 
//                    behaviouristDaysService.getAvailableSlotsForDate(currentUserOpt.get().getUid(), date);
//            
//            if (availableSlots.isEmpty()) {
//                return ResponseEntity.ok(Map.of(
//                        "status", true,
//                        "message", "No available slots found for the given date",
//                        "date", date,
//                        "slots", List.of()
//                ));
//            }
//            
//            return ResponseEntity.ok(Map.of(
//                    "status", true,
//                    "message", "Available slots fetched successfully",
//                    "date", date,
//                    "totalSlots", availableSlots.size(),
//                    "slots", availableSlots
//            ));
//            
//        } catch (RuntimeException ex) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(Map.of("status", false, "message", ex.getMessage()));
//        }
//    }

    /**
     * Get behaviourist days by service provider UID
     * GET /api/behaviourist-days/service-provider/{serviceProviderUid}
     */
    @GetMapping("/service-provider/{serviceProviderUid}")
    public ResponseEntity<?> getBehaviouristDays(@PathVariable UUID serviceProviderUid) {
        try {
            List<BehaviouristAvailableDay> days = 
                    behaviouristDaysService.getBehaviouristDaysFromServiceProvider(serviceProviderUid);
            return ResponseEntity.ok(days);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Get all service providers available on a specific day
     * GET /api/behaviourist-days/day/{day}
     */
    @GetMapping("/day/{day}")
    public ResponseEntity<?> getServiceProvidersByDay(@PathVariable DayOfWeek day) {
        try {
            List<ServiceProvider> serviceProviders = behaviouristDaysService.getServiceProvidersByDay(day);
            return ResponseEntity.ok(serviceProviders);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Get all slots for a service provider
     * GET /api/behaviourist-days/service-provider/{serviceProviderUid}/slots
     */
    @GetMapping("/service-provider/{serviceProviderUid}/slots")
    public ResponseEntity<?> getBehaviouristSlots(@PathVariable UUID serviceProviderUid) {
        try {
            List<BehaviouristSlots> slots = behaviouristDaysService.getBehaviouristSlots(serviceProviderUid);
            return ResponseEntity.ok(slots);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Get slots for a specific behaviourist day
     * GET /api/behaviourist-days/behaviourist-day/{behaviouristDayId}/slots
     */
    @GetMapping("/behaviourist-day/{behaviouristDayId}/slots")
    public ResponseEntity<?> getSlotsForBehaviouristDay(@PathVariable Long behaviouristDayId) {
        try {
            List<BehaviouristSlots> slots = behaviouristDaysService.getSlotsForBehaviouristDay(behaviouristDayId);
            return ResponseEntity.ok(slots);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Get behaviourist day ID by service provider and day
     * GET /api/behaviourist-days/getDayId/{serviceProviderUid}/{day}
     */
    @GetMapping("/getDayId/{serviceProviderUid}/{day}")
    public ResponseEntity<?> getDayIdByServiceProviderAndDay(
            @PathVariable("serviceProviderUid") UUID serviceProviderUid,
            @PathVariable("day") DayOfWeek day) {
        try {
            Map<String, Long> result = 
                    behaviouristDaysService.getBehaviouristDayIdByServiceProviderAndDay(serviceProviderUid, day);
            return ResponseEntity.ok(result);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        }
    }
}