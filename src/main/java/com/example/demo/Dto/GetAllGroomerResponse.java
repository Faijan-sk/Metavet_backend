package com.example.demo.Dto;

import java.util.List;

import com.example.demo.Entities.GroomerKyc.ServiceLocationType;

public class GetAllGroomerResponse {
	
	
	 // ===== User =====
    private String uid;
    private String fullName;
    private String email;
    private String phoneNumber;

    // ===== Service Provider =====
    
    private String serviceType;

    // ===== KYC =====
	private String fullLegalName;
	private String businessName;
	private String serviceLocationType;
    private Integer yearsExperience;
    private List<String> servicesOffered;
    private Double distanceKm;
    
    
    //========== getter and setter ===========
    
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getFullLegalName() {
		return fullLegalName;
	}
	public void setFullLegalName(String fullLegalName) {
		this.fullLegalName = fullLegalName;
	}
	public String getBusinessName() {
		return businessName;
	}
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	public String getServiceLocationType() {
		return serviceLocationType;
	}
	public void setServiceLocationType(String serviceLocationType) {
		this.serviceLocationType = serviceLocationType;
	}
	public Integer getYearsExperience() {
		return yearsExperience;
	}
	public void setYearsExperience(Integer yearsExperience) {
		this.yearsExperience = yearsExperience;
	}
	public List<String> getServicesOffered() {
		return servicesOffered;
	}
	public void setServicesOffered(List<String> servicesOffered) {
		this.servicesOffered = servicesOffered;
	}
	public Double getDistanceKm() {
		return distanceKm;
	}
	public void setDistanceKm(Double distanceKm) {
		this.distanceKm = distanceKm;
	}
    
    
    
    

}
