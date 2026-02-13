package com.example.demo.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "groomer_services")
public class GroomerServices extends BaseEntity {
	
	@ManyToOne
	@JoinColumn(name = "service_provider_id", nullable = false)
	private ServiceProvider serviceProvider;


    @NotBlank(message = "Service name is required")
    @Column(nullable = false, length = 100)
    private String serviceName;

    @ManyToOne
    @JoinColumn(name = "kyc_id", referencedColumnName = "id", nullable = true) 
    private GroomerKyc groomerKyc;
    
    @NotNull(message = "Duration is required")
    @Min(value = 15, message = "Duration must be at least 15 minutes")
    @Column(nullable = false)
    private Integer durationMinutes;

    @NotNull(message = "Price is required")
    @Column(nullable = false)
    private Double price;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Boolean isActive = true;

	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
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

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public GroomerKyc getGroomerKyc() {
		return groomerKyc;
	}

	public void setGroomerKyc(GroomerKyc groomerKyc) {
		this.groomerKyc = groomerKyc;
	}
    
    
    


}
