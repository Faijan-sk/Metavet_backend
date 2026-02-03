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
import com.example.demo.Dto.ServiceProviderDayRequest;
import com.example.demo.Dto.ServiceProviderDaysResponseDto;
import com.example.demo.Dto.SlotResponseDtoForServiceProvider;
import com.example.demo.Entities.ServiceProvider;
import com.example.demo.Entities.ServiceProviderDays;
import com.example.demo.Entities.ServiceProviderSlots;
import com.example.demo.Entities.UsersEntity;
import com.example.demo.Enum.DayOfWeek;
import com.example.demo.Repository.ServiceProviderRepo;
import com.example.demo.Repository.UserRepo;
import com.example.demo.Service.ServiceProviderDaysService;
import com.example.demo.Service.ServiceProviderService;

@RestController
@RequestMapping("/api/service-provider-days")
public class ServiceProviderDaysController {

    @Autowired
    private ServiceProviderDaysService serviceProviderDaysService;

    @Autowired
    private ServiceProviderService serviceProviderService;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private SpringSecurityAuditorAware auditorAware;
    
    @Autowired
    private ServiceProviderRepo serviceProviderRepo ;

    /**
     * POST /api/service-provider-days/days
     * Create days and slots for current logged-in service provider
     */
    @PostMapping("/days")
    public ResponseEntity<?> addServiceProviderDays(@RequestBody List<ServiceProviderDayRequest> dayRequests) {
        try {
            Optional<UsersEntity> currentUserOpt = auditorAware.getCurrentAuditor();

            if (currentUserOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Please Login"));
            }

            UUID userUid = currentUserOpt.get().getUid();

            // Get ServiceProvider UID from user UID
            UUID serviceProviderUid = serviceProviderService.getServiceProviderUidByUserUid(userUid);

            if (serviceProviderUid == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Service Provider profile not found for user"));
            }

            List<ServiceProviderDays> createdDays = serviceProviderDaysService
                    .createDaysForServiceProvider(serviceProviderUid, dayRequests);

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
     * GET /api/service-provider-days/service-provider/{serviceProviderUid}
     * Get all days for a specific service provider
     */
    @GetMapping("/service-provider/{serviceProviderUid}")
    public ResponseEntity<?> getServiceProviderDays(@PathVariable UUID serviceProviderUid) {
        try {
            List<ServiceProviderDays> days = serviceProviderDaysService
                    .getServiceProviderDaysFromServiceProvider(serviceProviderUid);

            List<ServiceProviderDaysResponseDto> response = days.stream()
                    .map(this::mapToDto)
                    .toList();

            return ResponseEntity.ok(response);

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        }
    }
    
    
  
    
    
    
    

    private ServiceProviderDaysResponseDto mapToDto(ServiceProviderDays day) {
        ServiceProviderDaysResponseDto dto = new ServiceProviderDaysResponseDto();

        dto.setServiceProviderDayId(day.getServiceProviderDayIdForJson());
        dto.setServiceProviderDayUid(day.getUid());
        dto.setDayOfWeek(day.getDayOfWeek());
        dto.setStartTime(day.getStartTime());
        dto.setEndTime(day.getEndTime());
        dto.setSlotDurationMinutes(day.getSlotDurationMinutes());

        return dto;
    }

    

   

    /**
     * GET /api/service-provider-days/getSelfDays
     * Get available days for current logged-in service provider
     */
    @GetMapping("/getSelfDays")
    public ResponseEntity<?> getServiceProviderDaysSelf() {
        try {
            Optional<UsersEntity> currentUserOpt = auditorAware.getCurrentAuditor();

            if (currentUserOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("status", false, "message", "User not authenticated"));
            }

            Map<String, Object> result = serviceProviderDaysService
                    .getServiceProviderAvailableDays(currentUserOpt.get().getUid());

            List<?> days = (List<?>) result.get("days");

            if (days.isEmpty()) {
                return ResponseEntity.ok(
                        Map.of(
                                "status", false,
                                "message", "Service Provider has not configured availability days yet",
                                "serviceProviderUid", result.get("serviceProviderUid"),
                                "days", List.of()
                        )
                );
            }

            return ResponseEntity.ok(
                    Map.of(
                            "status", true,
                            "message", "Service Provider available days fetched successfully",
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
     * GET /api/service-provider-days/getSelfAvailableSlots
     * Get available slots for current logged-in service provider for a specific date
     */
    @GetMapping("/getSelfAvailableSlots")
    public ResponseEntity<?> getServiceProviderAvailableSlots(@RequestParam String date) {
        try {
            Optional<UsersEntity> currentUserOpt = auditorAware.getCurrentAuditor();

            if (currentUserOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("status", false, "message", "User not authenticated"));
            }

            List<SlotResponseDtoForServiceProvider> availableSlots = serviceProviderDaysService
                    .getAvailableSlotsForDate(currentUserOpt.get().getUid(), date);

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
                    .body(Map.of("status", false, "message", ex.getMessage()));
        }
    }
}