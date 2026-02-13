package com.example.demo.Dto;

import java.time.LocalTime;

public class GroomerServiceResponseDto {
	
	
	private String serviceUid;
    private String serviceName;
    private Integer durationMinutes;
    private Double price;
    private String description;
    private LocalTime calculatedEndTime;
	public String getServiceUid() {
		return serviceUid;
	}
	public void setServiceUid(String serviceUid) {
		this.serviceUid = serviceUid;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public Integer getDurationMinutes() {
		return durationMinutes;
	}
	public void setDurationMinutes(Integer durationMinutes) {
		this.durationMinutes = durationMinutes;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public LocalTime getCalculatedEndTime() {
		return calculatedEndTime;
	}
	public void setCalculatedEndTime(LocalTime calculatedEndTime) {
		this.calculatedEndTime = calculatedEndTime;
	}

    
    
}
