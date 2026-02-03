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

import com.example.demo.Dto.ServiceProviderDayRequest;
import com.example.demo.Dto.ServiceProviderDayResponseDto;
import com.example.demo.Dto.SlotResponseDtoForServiceProvider;
import com.example.demo.Entities.ServiceProvider;
import com.example.demo.Entities.ServiceProviderDays;
import com.example.demo.Entities.ServiceProviderSlots;
import com.example.demo.Enum.DayOfWeek;
import com.example.demo.Repository.ServiceProviderDaysRepo;
import com.example.demo.Repository.ServiceProviderRepo;
import com.example.demo.Repository.ServiceProviderSlotRepo;


@Service
public class ServiceProviderDaysService {

    @Autowired
    private ServiceProviderDaysRepo serviceProviderDaysRepository;

    @Autowired
    private ServiceProviderRepo serviceProviderRepository;

    @Autowired
    private ServiceProviderSlotRepo serviceProviderSlotsRepository;

    @Transactional
    public List<ServiceProviderDays> createDaysForServiceProvider(UUID serviceProviderUid, 
                                                                   List<ServiceProviderDayRequest> dayRequests) {
        ServiceProvider serviceProvider = serviceProviderRepository.findByUid(serviceProviderUid)
                .orElseThrow(() -> new RuntimeException("Service Provider not found with uid: " + serviceProviderUid));

        if (dayRequests == null || dayRequests.isEmpty()) {
            throw new RuntimeException("Day requests cannot be empty");
        }

        List<ServiceProviderDays> existingDays = serviceProviderDaysRepository
                .findByServiceProvider_Uid(serviceProviderUid);
        List<ServiceProviderDays> createdDays = new ArrayList<>();

        for (ServiceProviderDayRequest request : dayRequests) {
            validateDayRequest(request);

            boolean dayExists = existingDays.stream()
                    .anyMatch(day -> day.getDayOfWeek() == request.getDayOfWeek());

            if (dayExists) {
                throw new RuntimeException("Day " + request.getDayOfWeek() + 
                        " is already assigned to service provider with uid: " + serviceProviderUid);
            }

            validateTimeRange(request.getStartTime(), request.getEndTime(), request.getDayOfWeek());

            ServiceProviderDays serviceProviderDay = new ServiceProviderDays(
                    request.getDayOfWeek(),
                    serviceProvider,
                    request.getStartTime(),
                    request.getEndTime(),
                    request.getSlotDurationMinutes());

            ServiceProviderDays savedDay = serviceProviderDaysRepository.save(serviceProviderDay);

            List<ServiceProviderSlots> slots = createTimeSlots(savedDay);
            serviceProviderSlotsRepository.saveAll(slots);

            createdDays.add(savedDay);
        }

        return createdDays;
    }

    private List<ServiceProviderSlots> createTimeSlots(ServiceProviderDays serviceProviderDay) {
        List<ServiceProviderSlots> slots = new ArrayList<>();

        LocalTime currentTime = serviceProviderDay.getStartTime();
        LocalTime endTime = serviceProviderDay.getEndTime();
        int durationMinutes = serviceProviderDay.getSlotDurationMinutes();

        while (currentTime.isBefore(endTime)) {
            LocalTime slotEnd = currentTime.plusMinutes(durationMinutes);

            if (slotEnd.isAfter(endTime)) {
                break;
            }

            ServiceProviderSlots slot = new ServiceProviderSlots(serviceProviderDay, currentTime, slotEnd);
            slots.add(slot);

            currentTime = slotEnd;
        }

        return slots;
    }

    public List<ServiceProviderDays> getServiceProviderDaysFromServiceProvider(UUID serviceProviderUid) {
        if (!serviceProviderRepository.existsByUid(serviceProviderUid)) {
            throw new RuntimeException("Service Provider not found with uid: " + serviceProviderUid);
        }
        return serviceProviderDaysRepository.findByServiceProvider_Uid(serviceProviderUid);
    }

    public List<ServiceProviderDays> getServiceProviderDaysFromUserUUID(UUID userUid) {
        ServiceProvider serviceProvider = serviceProviderRepository.findByOwner_Uid(userUid)
                .orElseThrow(() -> new RuntimeException("Service Provider not found for user uid: " + userUid));

        return serviceProviderDaysRepository.findByServiceProvider_Uid(serviceProvider.getUid());
    }

    public List<ServiceProviderDays> getServiceProviderDaysByDay(DayOfWeek day) {
        if (day == null) {
            throw new RuntimeException("Day cannot be null");
        }
        return serviceProviderDaysRepository.findByDayOfWeek(day);
    }

    public List<ServiceProvider> getServiceProvidersByDay(DayOfWeek day) {
        if (day == null) {
            throw new RuntimeException("Day cannot be null");
        }
        return serviceProviderDaysRepository.findServiceProvidersByDay(day);
    }

    public List<ServiceProvider> getServiceProvidersByDayAndServiceType(DayOfWeek day, 
                                                                         ServiceProvider.ServiceType serviceType) {
        if (day == null) {
            throw new RuntimeException("Day cannot be null");
        }
        if (serviceType == null) {
            throw new RuntimeException("Service type cannot be null");
        }

        List<ServiceProvider> serviceProviders = serviceProviderDaysRepository.findServiceProvidersByDay(day);
        return serviceProviders.stream()
                .filter(sp -> sp.getServiceType() == serviceType)
                .collect(Collectors.toList());
    }

    public List<ServiceProvider.ServiceType> getServiceTypesByDay(DayOfWeek day) {
        if (day == null) {
            throw new RuntimeException("Day cannot be null");
        }

        List<ServiceProvider> serviceProviders = serviceProviderDaysRepository.findServiceProvidersByDay(day);
        return serviceProviders.stream()
                .map(ServiceProvider::getServiceType)
                .filter(type -> type != null)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<ServiceProviderSlots> getServiceProviderSlots(UUID serviceProviderUid) {
        if (!serviceProviderRepository.existsByUid(serviceProviderUid)) {
            throw new RuntimeException("Service Provider not found with uid: " + serviceProviderUid);
        }
        return serviceProviderSlotsRepository.findByServiceProviderUid(serviceProviderUid);
    }

    public List<ServiceProviderSlots> getSlotsForServiceProviderDay(UUID serviceProviderDayUid) {
        if (!serviceProviderDaysRepository.existsByUid(serviceProviderDayUid)) {
            throw new RuntimeException("Service Provider day not found with uid: " + serviceProviderDayUid);
        }
        return serviceProviderSlotsRepository.findByServiceProviderDay_Uid(serviceProviderDayUid);
    }

    public Map<String, UUID> getServiceProviderDayUidByServiceProviderAndDay(UUID serviceProviderUid, DayOfWeek day) {
        if (day == null) {
            throw new RuntimeException("Day cannot be null");
        }
        if (!serviceProviderRepository.existsByUid(serviceProviderUid)) {
            throw new RuntimeException("Service Provider not found with uid: " + serviceProviderUid);
        }

        ServiceProviderDays serviceProviderDay = serviceProviderDaysRepository
                .findFirstByServiceProvider_UidAndDayOfWeek(serviceProviderUid, day)
                .orElseThrow(() -> new RuntimeException(
                        "No schedule found for service provider UID: " + serviceProviderUid + " on " + day));

        return Map.of("serviceProviderDayUid", serviceProviderDay.getUid());
    }

    private void validateDayRequest(ServiceProviderDayRequest request) {
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

    public Map<String, Object> getServiceProviderAvailableDays(UUID userUid) {
        ServiceProvider serviceProvider = serviceProviderRepository.findByOwner_Uid(userUid)
                .orElseThrow(() -> new RuntimeException("Service Provider not found"));

        List<ServiceProviderDays> days = serviceProviderDaysRepository
                .findByServiceProvider_Uid(serviceProvider.getUid());

        List<ServiceProviderDayResponseDto> responseDays = days.stream()
                .map(d -> new ServiceProviderDayResponseDto(
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

    public List<SlotResponseDtoForServiceProvider> getAvailableSlotsForDate(UUID serviceProviderUserUid, 
                                                                             String dateString) {
        LocalDate requestedDate;
        try {
            requestedDate = LocalDate.parse(dateString);
        } catch (Exception e) {
            throw new RuntimeException("Invalid date format. Expected format: YYYY-MM-DD");
        }

        DayOfWeek dayOfWeek = convertJavaDayToDayOfWeek(requestedDate.getDayOfWeek());

        ServiceProvider serviceProvider = serviceProviderRepository.findByOwner_Uid(serviceProviderUserUid)
                .orElseThrow(() -> new RuntimeException("Service Provider not found"));

        List<ServiceProviderDays> serviceProviderDays = serviceProviderDaysRepository
                .findByServiceProvider_UidAndDayOfWeek(serviceProvider.getUid(), dayOfWeek);

        if (serviceProviderDays.isEmpty()) {
            throw new RuntimeException("Service Provider is not available on " + dayOfWeek);
        }

        List<ServiceProviderSlots> availableSlots = serviceProviderSlotsRepository
                .findAvailableSlotsByServiceProviderUidAndDay(serviceProvider.getUid(), dayOfWeek);

        return availableSlots.stream()
                .map(slot -> new SlotResponseDtoForServiceProvider(
                        slot.getId(),
                        slot.getUid(),
                        slot.getStartTime(),
                        slot.getEndTime(),
                        slot.getSlotIdForJson(),
                        slot.getServiceProviderDayIdForJson()
                ))
                .collect(Collectors.toList());
    }

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