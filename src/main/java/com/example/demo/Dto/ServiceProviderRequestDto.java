package com.example.demo.Dto;

import com.example.demo.Entities.ServiceProvider.ServiceType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class ServiceProviderRequestDto {
	
	@Enumerated(EnumType.STRING)
	private ServiceType serviceType ;
	


	public ServiceType getServiceType() {
		return serviceType;
	}

	public void setServiceType(ServiceType serviceType) {
		this.serviceType = serviceType;
	}


	
	
	
	
	
	

}
