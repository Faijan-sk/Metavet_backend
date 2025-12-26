package com.example.demo.Entities;

import java.util.Optional;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class ServiceProvider extends BaseEntity {

	@OneToOne
	@JoinColumn(name = "user_uid", referencedColumnName = "uid")
	private UsersEntity owner;
	
    @Enumerated(EnumType.STRING)
	public ServiceType  serviceType ;	
	
	public enum ServiceType {	
		Pet_Walker,
		Pet_Groomer ,
		Pet_Behaviourist 
	}

	public UsersEntity getOwner() {
		return owner;
	}

	public void setOwner(UsersEntity user) {
	    this.owner = user;
	}


	public ServiceType getServiceType() {
		return serviceType;
	}

	public void setServiceType(ServiceType serviceType) {
		this.serviceType = serviceType;
	}

	
	


	
	
	
	
	
	
}
