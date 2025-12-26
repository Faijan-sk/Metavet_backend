package com.example.demo.Dto;

import com.example.demo.Entities.ServiceProvider.ServiceType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class ServiceProviderRequestDto {
	
	@Enumerated(EnumType.STRING)
	private ServiceType serviceType ;
	
	
	private String uid;

	public ServiceType getServiceType() {
		return serviceType;
	}

	public void setServiceType(ServiceType serviceType) {
		this.serviceType = serviceType;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	
	
	
	
	
	

}
