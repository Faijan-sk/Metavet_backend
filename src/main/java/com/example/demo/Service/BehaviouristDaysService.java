package com.example.demo.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Dto.BehaviouristDayRequest;
import com.example.demo.Dto.BehaviouristDayResponseDto;
import com.example.demo.Dto.SlotResponseDtoForBehaviourist;
import com.example.demo.Entities.BehaviouristAvailableDay;
import com.example.demo.Entities.BehaviouristSlots;
import com.example.demo.Entities.ServiceProvider;
import com.example.demo.Enum.DayOfWeek;
import com.example.demo.Repository.BehaviouristDaysRepo;
import com.example.demo.Repository.BehaviouristSlotsRepo;
import com.example.demo.Repository.ServiceProviderRepo;

@Service
public class BehaviouristDaysService {

    @Autowired
    private BehaviouristDaysRepo behaviouristDaysRepository;

    @Autowired
    private ServiceProviderRepo serviceProviderRepository;

    @Autowired
    private BehaviouristSlotsRepo behaviouristSlotsRepository;

    @Transactional
    public List<BehaviouristAvailableDay> createDaysForBehaviourist(UUID serviceProviderUid, 
                                                                     List<BehaviouristDayRequest> dayRequests) {
        ServiceProvider serviceProvider = serviceProviderRepository.findByUid(serviceProviderUid)
                .orElseThrow(() -> new RuntimeException("Service Provider not found with uid: " + serviceProviderUid));

        if (dayRequests == null || dayRequests.isEmpty()) {
            throw new RuntimeException("Day requests cannot be empty");
        }

        List<BehaviouristAvailableDay> existingDays = behaviouristDaysRepository.findByServiceProvider_Uid(serviceProviderUid);
        List<BehaviouristAvailableDay> createdDays = new ArrayList<>();

        for (BehaviouristDayRequest request : dayRequests) {
            validateDayRequest(request);

            boolean dayExists = existingDays.stream()
                    .anyMatch(day -> day.getDayOfWeek() == request.getDayOfWeek());

            if (dayExists) {
                throw new RuntimeException("Day " + request.getDayOfWeek() + 
                        " is already assigned to service provider with uid: " + serviceProviderUid);
            }

            validateTimeRange(request.getStartTime(), request.getEndTime(), request.getDayOfWeek());

            BehaviouristAvailableDay behaviouristDay = new BehaviouristAvailableDay(
                    request.getDayOfWeek(),
                    serviceProvider,
                    request.getStartTime(),
                    request.getEndTime(),
                    request.getSlotDurationMinutes());

            BehaviouristAvailableDay savedDay = behaviouristDaysRepository.save(behaviouristDay);

            List<BehaviouristSlots> slots = createTimeSlots(savedDay);
            behaviouristSlotsRepository.saveAll(slots);

            createdDays.add(savedDay);
        }

        return createdDays;
    }

    private List<BehaviouristSlots> createTimeSlots(BehaviouristAvailableDay behaviouristDay) {
        List<BehaviouristSlots> slots = new ArrayList<>();

        LocalTime currentTime = behaviouristDay.getStartTime();
        LocalTime endTime = behaviouristDay.getEndTime();
        int durationMinutes = behaviouristDay.getSlotDurationMinutes();

        while (currentTime.isBefore(endTime)) {
            LocalTime slotEnd = currentTime.plusMinutes(durationMinutes);

            if (slotEnd.isAfter(endTime)) {
                break;
            }

            BehaviouristSlots slot = new BehaviouristSlots(behaviouristDay, currentTime, slotEnd);
            slots.add(slot);

            currentTime = slotEnd;
        }

        return slots;
    }

    public List<BehaviouristAvailableDay> getBehaviouristDaysFromServiceProvider(UUID serviceProviderUid) {
        if (!serviceProviderRepository.existsByUid(serviceProviderUid)) {
            throw new RuntimeException("Service Provider not found with uid: " + serviceProviderUid);
        }
        return behaviouristDaysRepository.findByServiceProvider_Uid(serviceProviderUid);
    }

    public List<BehaviouristAvailableDay> getBehaviouristDaysByDay(DayOfWeek day) {
        if (day == null) {
            throw new RuntimeException("Day cannot be null");
        }
        return behaviouristDaysRepository.findByDayOfWeek(day);
    }

    public List<ServiceProvider> getServiceProvidersByDay(DayOfWeek day) {
        if (day == null) {
            throw new RuntimeException("Day cannot be null");
        }
        return behaviouristDaysRepository.findServiceProvidersByDay(day);
    }

    public List<BehaviouristSlots> getBehaviouristSlots(UUID serviceProviderUid) {
        if (!serviceProviderRepository.existsByUid(serviceProviderUid)) {
            throw new RuntimeException("Service Provider not found with uid: " + serviceProviderUid);
        }
        return behaviouristSlotsRepository.findByServiceProviderUid(serviceProviderUid);
    }

    public List<BehaviouristSlots> getSlotsForBehaviouristDay(Long behaviouristDayId) {
        if (!behaviouristDaysRepository.existsById(behaviouristDayId)) {
            throw new RuntimeException("Behaviourist day not found with id: " + behaviouristDayId);
        }
        return behaviouristSlotsRepository.findByServiceProviderDay_Id(behaviouristDayId);
    }

    public Map<String, Long> getBehaviouristDayIdByServiceProviderAndDay(UUID serviceProviderUid, DayOfWeek day) {
        if (day == null) {
            throw new RuntimeException("Day cannot be null");
        }
        if (!serviceProviderRepository.existsByUid(serviceProviderUid)) {
            throw new RuntimeException("Service Provider not found with uid: " + serviceProviderUid);
        }

        BehaviouristAvailableDay behaviouristDay = behaviouristDaysRepository
                .findFirstByServiceProvider_UidAndDayOfWeek(serviceProviderUid, day)
                .orElseThrow(() -> new RuntimeException(
                        "No schedule found for service provider UID: " + serviceProviderUid + " on " + day));

        return Map.of("behaviouristDayId", behaviouristDay.getId());
    }

    private void validateDayRequest(BehaviouristDayRequest request) {
        if (request == null) {
            throw new RuntimeException("Day request cannot be null");
        }
        if (request.getDayOfWeek() == null) {
            throw new RuntimeException("Day of week cannot be null");
        }
        if (request.getStartTime() == null) {
            throw new RuntimeException("Start time cannot be null");
        }
        if (request.getEndTime() == null) {
            throw new RuntimeException("End time cannot be null");
        }
        if (request.getSlotDurationMinutes() == null || request.getSlotDurationMinutes() <= 0) {
            throw new RuntimeException("Slot duration must be greater than 0");
        }
    }

    private void validateTimeRange(LocalTime startTime, LocalTime endTime, DayOfWeek day) {
        if (endTime.isBefore(startTime)) {
            throw new RuntimeException("End time must be after start time for day: " + day);
        }
        if (endTime.equals(startTime)) {
            throw new RuntimeException("End time cannot be equal to start time for day: " + day);
        }
    }

    public Map<String, Object> getBehaviouristAvailableDays(UUID userUid) {

        // Assuming ServiceProvider has userUid field
        ServiceProvider serviceProvider = serviceProviderRepository.findByOwner_Uid(userUid)
                .orElseThrow(() -> new RuntimeException("Service Provider not found"));

        List<BehaviouristAvailableDay> days = behaviouristDaysRepository.findByServiceProvider_Uid(serviceProvider.getUid());

        List<BehaviouristDayResponseDto> responseDays = days.stream()
                .map(d -> new BehaviouristDayResponseDto(
                        d.getId(),
                        d.getUid(),
                        d.getDayOfWeek().name()
                ))
                .distinct()
                .toList();

        return Map.of(
                "serviceProviderUid", serviceProvider.getUid(),
                "days", responseDays
        );
    }

//    public List<SlotResponseDtoForBehaviourist> getAvailableSlotsForDate(UUID serviceProviderUserUid, String dateString) {
//        // Parse the date string
//        LocalDate requestedDate;
//        try {
//            requestedDate = LocalDate.parse(dateString);
//        } catch (Exception e) {
//            throw new RuntimeException("Invalid date format. Expected format: YYYY-MM-DD");
//        }
//
//        // Get day of week from date
//        DayOfWeek dayOfWeek = convertJavaDayToDayOfWeek(requestedDate.getDayOfWeek());
//
//        // Get ServiceProvider from user UID
//        ServiceProvider serviceProvider = serviceProviderRepository.findByOwner_Uid(serviceProviderUserUid)
//                .orElseThrow(() -> new RuntimeException("Service Provider not found"));
//
//        UUID serviceProviderUid = serviceProvider.getUid();
//
//        if (!serviceProviderRepository.existsByUid(serviceProviderUid)) {
//            throw new RuntimeException("Service Provider not found");
//        }
//
//        // Check if service provider has availability on this day
//        List<BehaviouristAvailableDay> behaviouristDays = 
//                behaviouristDaysRepository.findByServiceProvider_UidAndDayOfWeek(serviceProviderUid, dayOfWeek);
//
//        if (behaviouristDays.isEmpty()) {
//            throw new RuntimeException("Service Provider is not available on " + dayOfWeek);
//        }
//
//        // Get available slots (not booked)
//        List<BehaviouristSlots> availableSlots = 
//                behaviouristSlotsRepository.findAvailableSlotsByServiceProviderUidAndDay(serviceProviderUid, dayOfWeek);
//
//        // Convert to DTO
//        return availableSlots.stream()
//                .map(slot -> new SlotResponseDtoForBehaviourist(
//                        slot.getId(),
//                        slot.getUid(),
//                        slot.getStartTime(),
//                        slot.getEndTime(),
//                        slot.getBehaviouristDayIdForJson()
//                ))
//                .collect(Collectors.toList());
//    }

    // Helper method to convert java.time.DayOfWeek to custom DayOfWeek enum
    private DayOfWeek convertJavaDayToDayOfWeek(java.time.DayOfWeek javaDayOfWeek) {
        return switch (javaDayOfWeek) {
            case MONDAY -> DayOfWeek.MONDAY;
            case TUESDAY -> DayOfWeek.TUESDAY;
            case WEDNESDAY -> DayOfWeek.WEDNESDAY;
            case THURSDAY -> DayOfWeek.THURSDAY;
            case FRIDAY -> DayOfWeek.FRIDAY;
            case SATURDAY -> DayOfWeek.SATURDAY;
            case SUNDAY -> DayOfWeek.SUNDAY;
        };
    }
}