package com.example.demo.Dto;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import com.example.demo.Entities.DoctorSlots;
import com.example.demo.Enum.DayOfWeek;

public class DoctorDaysResponseDto {
	
	
	private UUID doctorDayUid;
	
	private long doctorDayId;
		
	 private DayOfWeek dayOfWeek;
	 
	 private LocalTime startTime;
	 
	 private LocalTime endTime;
	 
	 private Integer slotDurationMinutes;

	 public UUID getDoctorDayUid() {
		 return doctorDayUid;
	 }

	 public void setDoctorDayUid(UUID doctorDayUid) {
		 this.doctorDayUid = doctorDayUid;
	 }

	 public long getDoctorDayId() {
		 return doctorDayId;
	 }

	 public void setDoctorDayId(long doctorDayId) {
		 this.doctorDayId = doctorDayId;
	 }

	 public DayOfWeek getDayOfWeek() {
		 return dayOfWeek;
	 }

	 public void setDayOfWeek(DayOfWeek dayOfWeek) {
		 this.dayOfWeek = dayOfWeek;
	 }

	 public LocalTime getStartTime() {
		 return startTime;
	 }

	 public void setStartTime(LocalTime startTime) {
		 this.startTime = startTime;
	 }

	 public LocalTime getEndTime() {
		 return endTime;
	 }

	 public void setEndTime(LocalTime endTime) {
		 this.endTime = endTime;
	 }

	 public Integer getSlotDurationMinutes() {
		 return slotDurationMinutes;
	 }

	 public void setSlotDurationMinutes(Integer slotDurationMinutes) {
		 this.slotDurationMinutes = slotDurationMinutes;
	 }
	 
	 
	 
	 
	 
	 

}
