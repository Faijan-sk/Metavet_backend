package com.example.demo.Dto;

import java.time.LocalTime;
import java.util.UUID;

import com.example.demo.Entities.DoctorDays;

import jakarta.persistence.Column;

public class SlotResponseDto {
	
	
	private UUID doctorDayUid;
	
	private String day;
	
	private long doctorDayId;
	
	
	private LocalTime startTime;
	
	
	private LocalTime endTime;
	
	private UUID slotUid;
	
	private long slotId ;
	
	

	public UUID getDoctorDayUid() {
		return doctorDayUid;
	}

	public void setDoctorDayUid(UUID doctorDayUid) {
		this.doctorDayUid = doctorDayUid;
	}

	public String Day() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public long getDoctorDayId() {
		return doctorDayId;
	}

	public void setDoctorDayId(long doctorDayId) {
		this.doctorDayId = doctorDayId;
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

	public UUID getSlotUid() {
		return slotUid;
	}

	public void setSlotUid(UUID slotUid) {
		this.slotUid = slotUid;
	}

	public long getSlotId() {
		return slotId;
	}

	public void setSlotId(long slotId) {
		this.slotId = slotId;
	}
	
	
	
	
	

}
