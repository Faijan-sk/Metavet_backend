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

import com.example.demo.Dto.DoctorDayRequest;
import com.example.demo.Dto.DoctorDayResponseDto;
import com.example.demo.Dto.SlotResponseDtoForDoctor;
import com.example.demo.Entities.DoctorDays;
import com.example.demo.Entities.DoctorSlots;
import com.example.demo.Entities.DoctorsEntity;
import com.example.demo.Enum.DayOfWeek;
import com.example.demo.Repository.DoctorDaysRepo;
import com.example.demo.Repository.DoctorRepo;
import com.example.demo.Repository.DoctorSlotRepo;

@Service
public class DoctorDaysService {

    @Autowired
    private DoctorDaysRepo doctorDaysRepository;

    @Autowired
    private DoctorRepo doctorRepository;

    @Autowired
    private DoctorSlotRepo doctorSlotsRepository;

    @Transactional
    public List<DoctorDays> createDaysForDoctor(long doctorId, List<DoctorDayRequest> dayRequests) {
        DoctorsEntity doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));

        if (dayRequests == null || dayRequests.isEmpty()) {
            throw new RuntimeException("Day requests cannot be empty");
        }

        // Updated repository method name: findByDoctor_Id
        List<DoctorDays> existingDays = doctorDaysRepository.findByDoctor_Id(doctorId);
        List<DoctorDays> createdDays = new ArrayList<>();

        for (DoctorDayRequest request : dayRequests) {
            validateDayRequest(request);

            boolean dayExists = existingDays.stream()
                    .anyMatch(day -> day.getDayOfWeek() == request.getDayOfWeek());

            if (dayExists) {
                throw new RuntimeException("Day " + request.getDayOfWeek() + " is already assigned to doctor with id: " + doctorId);
            }

            validateTimeRange(request.getStartTime(), request.getEndTime(), request.getDayOfWeek());

            DoctorDays doctorDay = new DoctorDays(
                    request.getDayOfWeek(),
                    doctor,
                    request.getStartTime(),
                    request.getEndTime(),
                    request.getSlotDurationMinutes());

            DoctorDays savedDay = doctorDaysRepository.save(doctorDay);

            List<DoctorSlots> slots = createTimeSlots(savedDay);
            doctorSlotsRepository.saveAll(slots);

            createdDays.add(savedDay);
        }

        return createdDays;
    }

    private List<DoctorSlots> createTimeSlots(DoctorDays doctorDay) {
        List<DoctorSlots> slots = new ArrayList<>();

        LocalTime currentTime = doctorDay.getStartTime();
        LocalTime endTime = doctorDay.getEndTime();
        int durationMinutes = doctorDay.getSlotDurationMinutes();

        while (currentTime.isBefore(endTime)) {
            LocalTime slotEnd = currentTime.plusMinutes(durationMinutes);

            if (slotEnd.isAfter(endTime)) {
                break;
            }

            DoctorSlots slot = new DoctorSlots(doctorDay, currentTime, slotEnd);
            slots.add(slot);

            currentTime = slotEnd;
        }

        return slots;
    }

    public List<DoctorDays> getDoctorDaysFromDoctor(long doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new RuntimeException("Doctor not found with id: " + doctorId);
        }
        return doctorDaysRepository.findByDoctor_Id(doctorId);
    }
    
    
    public List<DoctorDays> getDoctorDaysFromDoctorUUID(UUID uid) {
    	
    	long doctorid= doctorRepository.findDoctorIdByUserUid(uid);
    	
        if (!doctorRepository.existsById(doctorid)) {
            throw new RuntimeException("Doctor not found with id: " + uid);
        }
        return doctorDaysRepository.findByDoctor_Id(doctorid);
    }
    
    

    public List<DoctorDays> getDoctorDaysByDay(DayOfWeek day) {
        if (day == null) {
            throw new RuntimeException("Day cannot be null");
        }
        return doctorDaysRepository.findByDayOfWeek(day);
    }

    public List<DoctorsEntity> getDoctorsByDay(DayOfWeek day) {
        if (day == null) {
            throw new RuntimeException("Day cannot be null");
        }
        return doctorDaysRepository.findDoctorsByDay(day);
    }

    public List<DoctorsEntity> getDoctorsByDayAndSpecialization(DayOfWeek day, String specialization) {
        if (day == null) {
            throw new RuntimeException("Day cannot be null");
        }
        if (specialization == null || specialization.trim().isEmpty()) {
            throw new RuntimeException("Specialization cannot be empty");
        }

        List<DoctorsEntity> doctors = doctorDaysRepository.findDoctorsByDay(day);
        return doctors.stream()
                .filter(doctor -> doctor.getSpecialization() != null &&
                        doctor.getSpecialization().equalsIgnoreCase(specialization.trim()))
                .collect(Collectors.toList());
    }

    public List<String> getSpecializationsByDay(DayOfWeek day) {
        if (day == null) {
            throw new RuntimeException("Day cannot be null");
        }

        List<DoctorsEntity> doctors = doctorDaysRepository.findDoctorsByDay(day);
        return doctors.stream()
                .map(DoctorsEntity::getSpecialization)
                .filter(spec -> spec != null && !spec.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<DoctorSlots> getDoctorSlots(long doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new RuntimeException("Doctor not found with id: " + doctorId);
        }
        return doctorSlotsRepository.findByDoctorId(doctorId);
    }

    public List<DoctorSlots> getSlotsForDoctorDay(long doctorDayId) {
        if (!doctorDaysRepository.existsById(doctorDayId)) {
            throw new RuntimeException("Doctor day not found with id: " + doctorDayId);
        }
        return doctorSlotsRepository.findByDoctorDay_Id(doctorDayId);
    }

    public Map<String, Long> getDoctorDayIdByDoctorAndDay(long doctorId, DayOfWeek day) {
        if (day == null) {
            throw new RuntimeException("Day cannot be null");
        }
        if (!doctorRepository.existsById(doctorId)) {
            throw new RuntimeException("Doctor not found with id: " + doctorId);
        }

        DoctorDays doctorDay = doctorDaysRepository
                .findFirstByDoctor_IdAndDayOfWeek(doctorId, day)
                .orElseThrow(() -> new RuntimeException(
                        "No schedule found for doctor ID: " + doctorId + " on " + day));

        return Map.of("doctorDayId", doctorDay.getId());
    }

    private void validateDayRequest(DoctorDayRequest request) {
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
    
    
    
    public Map<String, Object> getDoctorAvailableDays(UUID uid) {

        long doctorId = doctorRepository.findDoctorIdByUserUid(uid);

        if (!doctorRepository.existsById(doctorId)) {
            throw new RuntimeException("Doctor not found");
        }

        List<DoctorDays> days = doctorDaysRepository.findByDoctor_Id(doctorId);

        List<DoctorDayResponseDto> responseDays = days.stream()
                .map(d -> new DoctorDayResponseDto(
                        d.getId(),        // doctorDayId
                        d.getUid(),       // doctorDayUid (BaseEntity se)
                        d.getDayOfWeek().name()
                ))
                .distinct()
                .toList();

        return Map.of(
                "doctorId", doctorId,
                "days", responseDays
        );
    }


    
    
    public List<SlotResponseDtoForDoctor> getAvailableSlotsForDate(UUID doctorUserUid, String dateString) {
        // Parse the date string
        LocalDate requestedDate;
        try {
            requestedDate = LocalDate.parse(dateString); // Expects format: YYYY-MM-DD
        } catch (Exception e) {
            throw new RuntimeException("Invalid date format. Expected format: YYYY-MM-DD");
        }
        
        // Get day of week from date
        DayOfWeek dayOfWeek = convertJavaDayToDayOfWeek(requestedDate.getDayOfWeek());
        
        // Get doctor ID from UUID
        long doctorId = doctorRepository.findDoctorIdByUserUid(doctorUserUid);
        
        if (!doctorRepository.existsById(doctorId)) {
            throw new RuntimeException("Doctor not found");
        }
        
        // Check if doctor has availability on this day
        List<DoctorDays> doctorDays = doctorDaysRepository.findByDoctor_IdAndDayOfWeek(doctorId, dayOfWeek);
        
        if (doctorDays.isEmpty()) {
            throw new RuntimeException("Doctor is not available on " + dayOfWeek);
        }
        
        // Get available slots (not booked)
        List<DoctorSlots> availableSlots = doctorSlotsRepository.findAvailableSlotsByDoctorIdAndDay(doctorId, dayOfWeek);
        
        // Convert to DTO
        return availableSlots.stream()
                .map(slot -> new SlotResponseDtoForDoctor(
                        slot.getId(),
                        slot.getUid(),
                        slot.getStartTime(),
                        slot.getEndTime(),
                        slot.getSlotIdForJson(),
                        slot.getDoctorDayIdForJson()
                ))
                .collect(Collectors.toList());
    }
    
    // Helper method to convert java.time.DayOfWeek to your custom DayOfWeek enum
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