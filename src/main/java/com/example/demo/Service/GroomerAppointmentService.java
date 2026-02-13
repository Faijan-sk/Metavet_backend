package com.example.demo.Service;

import com.example.demo.Config.SpringSecurityAuditorAware;
import com.example.demo.Dto.*;
import com.example.demo.Entities.*;
import com.example.demo.Repository.*;

import jakarta.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroomerAppointmentService {


    @Autowired
    private ServiceProviderRepo serviceProviderRepo;

    @Autowired
    private GroomerAvailableDayRepo groomerAvailableDayRepo;

    @Autowired
    private GroomerServiceRepo groomerServiceRepo;

    @Autowired
    private GroomerAppointmentRepos groomerAppointmentRepos;

    @Autowired
    private UserRepo usersEntityRepository;
    
    @Autowired
    private SpringSecurityAuditorAware auditorAware;
    

    // ==================== GROOMER SETUP ====================

    @Transactional
    public Map<String, Object> setupGroomerSchedule(List<GroomerAvailableDayRequestDto> schedules) {
    	
    	UsersEntity owner = auditorAware.getCurrentAuditor()
    	        .orElseThrow(() -> new ValidationException("User not found"));

    	ServiceProvider groomer = serviceProviderRepo
    	        .findByOwner_Uid(owner.getUid())
    	        .orElseThrow(() -> new ValidationException("Only groomer can create days"));

    	System.out.println("______________________> groomer " + groomer.getServiceType());

    	// use groomer directly
    	ServiceProvider provider = groomer;


        List<GroomerAvailableDay> savedSchedules = new ArrayList<>();

        for (GroomerAvailableDayRequestDto dto : schedules) {
            if (dto.getStartTime().isAfter(dto.getEndTime())) {
                throw new RuntimeException("Start time cannot be after end time for day " + dto.getDayOfWeek());
            }

            Optional<GroomerAvailableDay> existing = groomerAvailableDayRepo
                    .findByServiceProvider_UidAndDayOfWeekAndIsActiveTrue(provider.getUid(), dto.getDayOfWeek()); // ✅ UUID

            GroomerAvailableDay schedule;
            if (existing.isPresent()) {
                schedule = existing.get();
                schedule.setStartTime(dto.getStartTime());
                schedule.setEndTime(dto.getEndTime());
            } else {
                schedule = new GroomerAvailableDay();
                schedule.setServiceProvider(provider);
                schedule.setDayOfWeek(dto.getDayOfWeek());
                schedule.setStartTime(dto.getStartTime());
                schedule.setEndTime(dto.getEndTime());
            }

            savedSchedules.add(groomerAvailableDayRepo.save(schedule));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Schedule setup successful");
        response.put("schedules", savedSchedules.stream().map(this::mapScheduleToDTO).collect(Collectors.toList()));
        return response;
    }

    @Transactional
    public Map<String, Object> setupGroomerServices( List<GroomerServiceRequestDto> services) {
    	
    	
    	UsersEntity owner = auditorAware.getCurrentAuditor()
    	        .orElseThrow(() -> new ValidationException("User not found"));

    	ServiceProvider groomer = serviceProviderRepo
    	        .findByOwner_Uid(owner.getUid())
    	        .orElseThrow(() -> new ValidationException("Only groomer can create days"));

    	

    	

    	
    	
    	
    	
    	
    	
    	
     // ✅ Convert to UUID
        
        ServiceProvider provider = serviceProviderRepo.findByUid(groomer.getUid())
                .orElseThrow(() -> new RuntimeException("Service provider not found"));

        List<GroomerServices> savedServices = new ArrayList<>();

        for (GroomerServiceRequestDto dto : services) {
            GroomerServices service = new GroomerServices();
            service.setServiceProvider(provider);
            service.setServiceName(dto.getServiceName());
            service.setDurationMinutes(dto.getDurationMinutes());
            service.setPrice(dto.getPrice());
            service.setDescription(dto.getDescription());
            service.setIsActive(true);

            savedServices.add(groomerServiceRepo.save(service));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Services setup successful");
        response.put("services", savedServices.stream().map(this::mapServiceToDTO).collect(Collectors.toList()));
        return response;
    }

    // ==================== CLIENT SCENARIOS ====================

    public List<GroomerInfoDTO> getAvailableGroomersByDate(LocalDate date) {
        int dayOfWeek = date.getDayOfWeek().getValue();

        List<UUID> providerUids = groomerAvailableDayRepo.findServiceProviderUidsByDayOfWeek(dayOfWeek); // ✅ Returns UUID

        return providerUids.stream()
                .map(uid -> {
                    ServiceProvider provider = serviceProviderRepo.findByUid(uid).orElse(null);
                    if (provider == null) return null;

                    GroomerAvailableDay schedule = groomerAvailableDayRepo
                            .findByServiceProvider_UidAndDayOfWeekAndIsActiveTrue(uid, dayOfWeek) // ✅ UUID
                            .orElse(null);

                    if (schedule == null) return null;

                    GroomerInfoDTO dto = new GroomerInfoDTO();
                    dto.setGroomerUid(provider.getUid().toString()); // ✅ Convert UUID to String for response
                    dto.setGroomerName(provider.getOwner().getFirstName() + " " + provider.getOwner().getLastName());
                    dto.setEmail(provider.getOwner().getEmail());
                    dto.setPhone(provider.getOwner().getFullPhoneNumber());

                    GroomerInfoDTO.ScheduleDTO scheduleDTO = new GroomerInfoDTO.ScheduleDTO();
                    scheduleDTO.setDayOfWeek(schedule.getDayOfWeek());
                    scheduleDTO.setDayName(getDayName(schedule.getDayOfWeek()));
                    scheduleDTO.setStartTime(schedule.getStartTime());
                    scheduleDTO.setEndTime(schedule.getEndTime());

                    dto.setScheduleForDay(scheduleDTO);
                    return dto;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<GroomerServiceResponseDto> getGroomerServices(String groomerUid) {
        UUID groomerUUID = UUID.fromString(groomerUid); // ✅ Convert to UUID
        
        List<GroomerServices> services = groomerServiceRepo.findByServiceProvider_UidAndIsActiveTrue(groomerUUID);
        return services.stream().map(this::mapServiceToDTO).collect(Collectors.toList());
    }

    public GroomerAvailabilityResponseDto getAvailableSlotsWithServices(String groomerUid, LocalDate date) {
        UUID groomerUUID = UUID.fromString(groomerUid);
        int dayOfWeek = date.getDayOfWeek().getValue();

        GroomerAvailableDay schedule = groomerAvailableDayRepo
                .findByServiceProvider_UidAndDayOfWeekAndIsActiveTrue(groomerUUID, dayOfWeek)
                .orElseThrow(() -> new RuntimeException("Groomer not available on this day"));

        List<GroomerAppointment> bookedAppointments = groomerAppointmentRepos
                .findBookedAppointments(groomerUUID, date);

        List<TimeSlot> availableSlots = calculateAvailableSlots(
                schedule.getStartTime(),
                schedule.getEndTime(),
                bookedAppointments
        );

        List<GroomerServices> services = groomerServiceRepo.findByServiceProvider_UidAndIsActiveTrue(groomerUUID);

        GroomerAvailabilityResponseDto response = new GroomerAvailabilityResponseDto();
        ServiceProvider provider = serviceProviderRepo.findByUid(groomerUUID).orElseThrow();

        response.setGroomerUid(groomerUid);
        response.setGroomerName(provider.getOwner().getFirstName() + " " + provider.getOwner().getLastName());
        response.setDate(date);
        response.setDayName(getDayName(dayOfWeek));

        GroomerAvailabilityResponseDto.WorkingHoursDTO workingHours = new GroomerAvailabilityResponseDto.WorkingHoursDTO();
        workingHours.setStartTime(schedule.getStartTime());
        workingHours.setEndTime(schedule.getEndTime());
        response.setWorkingHours(workingHours);

        List<AvailableSlotDto> slotDTOs = availableSlots.stream().map(slot -> {
            AvailableSlotDto slotDTO = new AvailableSlotDto();
            slotDTO.setSlotStartTime(slot.getStartTime());
            slotDTO.setSlotEndTime(slot.getEndTime());
            slotDTO.setAvailableDurationMinutes(slot.getDurationMinutes());

            List<GroomerServices> compatibleServices = services.stream()
                    .filter(service -> service.getDurationMinutes() <= slot.getDurationMinutes())
                    .collect(Collectors.toList());

            // Convert GroomerServices to GroomerServiceResDto
            List<GroomerServiceResDto> serviceDtos = compatibleServices.stream()
                    .map(this::convertToServiceResDto)
                    .collect(Collectors.toList());

            slotDTO.setCompatibleServices(serviceDtos);
            return slotDTO;
        }).collect(Collectors.toList());

        response.setAvailableSlots(slotDTOs);
        return response;
    }

    public Map<String, Object> getGroomerAvailableDays(String groomerUid) {
        UUID groomerUUID = UUID.fromString(groomerUid); // ✅ Convert to UUID
        
        List<GroomerAvailableDay> schedules = groomerAvailableDayRepo
                .findByServiceProvider_UidAndIsActiveTrue(groomerUUID);

        ServiceProvider provider = serviceProviderRepo.findByUid(groomerUUID)
                .orElseThrow(() -> new RuntimeException("Groomer not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("groomerUid", groomerUid);
        response.put("groomerName", provider.getOwner().getFirstName() + " " + provider.getOwner().getLastName());
        response.put("availableDays", schedules.stream()
                .map(schedule -> {
                    GroomerAvailableDayResponseDto dto = new GroomerAvailableDayResponseDto();
                    dto.setDayOfWeek(schedule.getDayOfWeek());
                    dto.setDayName(getDayName(schedule.getDayOfWeek()));
                    dto.setStartTime(schedule.getStartTime());
                    dto.setEndTime(schedule.getEndTime());
                    return dto;
                })
                .collect(Collectors.toList()));

        return response;
    }

    // ==================== BOOKING ====================

    @Transactional
    public Map<String, Object> bookAppointment(GroomerAppointmentBookingRequestDto request) {

        UUID providerUUID = UUID.fromString(request.getServiceProviderUid());
        UUID serviceUUID = UUID.fromString(request.getServiceUid());

        UsersEntity owner = auditorAware.getCurrentAuditor()
                .orElseThrow(() -> new ValidationException("User not found"));

        UsersEntity client = usersEntityRepository.findByUid(owner.getUid())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        ServiceProvider provider = serviceProviderRepo.findByUid(providerUUID)
                .orElseThrow(() -> new RuntimeException("Service provider not found"));

        GroomerServices service = groomerServiceRepo.findByUid(serviceUUID)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        LocalTime endTime = request.getStartTime().plusMinutes(service.getDurationMinutes());
        int dayOfWeek = request.getAppointmentDate().getDayOfWeek().getValue();

        GroomerAvailableDay schedule = groomerAvailableDayRepo
                .findByServiceProvider_UidAndDayOfWeekAndIsActiveTrue(providerUUID, dayOfWeek)
                .orElseThrow(() -> new RuntimeException("Groomer is not available on " + DayOfWeek.of(dayOfWeek).name()));

        // Check if time is within working hours
        if (request.getStartTime().isBefore(schedule.getStartTime()) ||
                endTime.isAfter(schedule.getEndTime())) {
            throw new RuntimeException(
                    String.format("Booking failed! Groomer's working hours are %s to %s. Your requested time %s to %s is outside working hours.",
                            schedule.getStartTime(),
                            schedule.getEndTime(),
                            request.getStartTime(),
                            endTime));
        }

        // Check for conflicts
        boolean hasConflict = groomerAppointmentRepos.hasConflict(
                providerUUID,
                request.getAppointmentDate(),
                request.getStartTime(),
                endTime
        );

        if (hasConflict) {
            // Get available slots to suggest
            List<GroomerAppointment> bookedAppointments = groomerAppointmentRepos
                    .findBookedAppointments(providerUUID, request.getAppointmentDate());

            List<TimeSlot> availableSlots = calculateAvailableSlots(
                    schedule.getStartTime(),
                    schedule.getEndTime(),
                    bookedAppointments
            );

            // Filter slots that can accommodate this service
            List<String> suggestedSlots = availableSlots.stream()
                    .filter(slot -> slot.getDurationMinutes() >= service.getDurationMinutes())
                    .map(slot -> slot.getStartTime() + " to " + slot.getEndTime())
                    .collect(Collectors.toList());

            String suggestion = suggestedSlots.isEmpty()
                    ? "No available slots for this service today. Please choose another date."
                    : "Available time slots: " + String.join(", ", suggestedSlots);

            throw new RuntimeException(
                    String.format("Time slot %s to %s is already booked. %s",
                            request.getStartTime(),
                            endTime,
                            suggestion));
        }

        // Create appointment
        GroomerAppointment appointment = new GroomerAppointment();
        appointment.setServiceProvider(provider);
        appointment.setClient(client);
        appointment.setService(service);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(endTime);
        appointment.setNotes(request.getNotes());
        appointment.setStatus(GroomerAppointment.AppointmentStatus.PENDING);

        GroomerAppointment saved = groomerAppointmentRepos.save(appointment);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Appointment booked successfully");
        response.put("appointmentUid", saved.getUid());
        response.put("groomerName", provider.getOwner().getFirstName() + " " + provider.getOwner().getLastName());
        response.put("serviceName", service.getServiceName());
        response.put("date", saved.getAppointmentDate());
        response.put("startTime", saved.getStartTime());
        response.put("endTime", saved.getEndTime());
        response.put("price", service.getPrice());

        return response;
    }

    // ==================== HELPER METHODS ====================

    private List<TimeSlot> calculateAvailableSlots(LocalTime workStart, LocalTime workEnd,
                                                    List<GroomerAppointment> bookedAppointments) {
        List<TimeSlot> availableSlots = new ArrayList<>();

        if (bookedAppointments.isEmpty()) {
            long duration = ChronoUnit.MINUTES.between(workStart, workEnd);
            availableSlots.add(new TimeSlot(workStart, workEnd, (int) duration));
            return availableSlots;
        }

        bookedAppointments.sort(Comparator.comparing(GroomerAppointment::getStartTime));

        LocalTime currentTime = workStart;

        for (GroomerAppointment appointment : bookedAppointments) {
            if (currentTime.isBefore(appointment.getStartTime())) {
                long duration = ChronoUnit.MINUTES.between(currentTime, appointment.getStartTime());
                availableSlots.add(new TimeSlot(currentTime, appointment.getStartTime(), (int) duration));
            }
            currentTime = appointment.getEndTime();
        }

        if (currentTime.isBefore(workEnd)) {
            long duration = ChronoUnit.MINUTES.between(currentTime, workEnd);
            availableSlots.add(new TimeSlot(currentTime, workEnd, (int) duration));
        }

        return availableSlots;
    }

    private String getDayName(int dayOfWeek) {
        return DayOfWeek.of(dayOfWeek).name();
    }

    private Map<String, Object> mapScheduleToDTO(GroomerAvailableDay schedule) {
        Map<String, Object> map = new HashMap<>();
        map.put("dayOfWeek", schedule.getDayOfWeek());
        map.put("dayName", getDayName(schedule.getDayOfWeek()));
        map.put("startTime", schedule.getStartTime());
        map.put("endTime", schedule.getEndTime());
        return map;
    }

    private GroomerServiceResponseDto mapServiceToDTO(GroomerServices service) {
        GroomerServiceResponseDto dto = new GroomerServiceResponseDto();
        dto.setServiceUid(service.getUid().toString());
        dto.setServiceName(service.getServiceName());
        dto.setDurationMinutes(service.getDurationMinutes());
        dto.setPrice(service.getPrice());
        dto.setDescription(service.getDescription());
        return dto;
    }

    // Helper method to convert GroomerServices to GroomerServiceResDto
    private GroomerServiceResDto convertToServiceResDto(GroomerServices service) {
        GroomerServiceResDto dto = new GroomerServiceResDto();
        dto.setUid(service.getUid().toString());
        dto.setServiceName(service.getServiceName());
        dto.setGroomerKyc(service.getGroomerKyc() != null ? service.getGroomerKyc().getUid().toString() : null);
        dto.setDurationMinutes(service.getDurationMinutes());
        dto.setPrice(service.getPrice());
        dto.setDescription(service.getDescription());
        dto.setIsActive(service.getIsActive());
        return dto;
    }

    private static class TimeSlot {
        private LocalTime startTime;
        private LocalTime endTime;
        private Integer durationMinutes;

        public TimeSlot(LocalTime startTime, LocalTime endTime, Integer durationMinutes) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.durationMinutes = durationMinutes;
        }

        public LocalTime getStartTime() {
            return startTime;
        }

        public LocalTime getEndTime() {
            return endTime;
        }

        public Integer getDurationMinutes() {
            return durationMinutes;
        }
    }
}