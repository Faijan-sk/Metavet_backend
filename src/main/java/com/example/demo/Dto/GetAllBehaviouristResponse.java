package com.example.demo.Dto;

import java.util.List;

import com.example.demo.Entities.BehaviouristKyc.ServiceOffered;
import com.example.demo.Entities.BehaviouristKyc.Specialization;

public class GetAllBehaviouristResponse {
	
	// ===== User =====
    private String uid;
    private String email;
    private String phoneNumber;
    private String fullName;
    
    
 // ===== Service Provider =====
    
    private String serviceType;
    
    //==== Kyc Detail===========
    
    private String address;
    
    private String serviceArea;

    private String yearsExperience;
    
    private List<String> servicesOffered;
    
    private String servicesOtherText;
    
    private List<String> specializations;
    
    private String specializationOtherText;
    
    private String serviceRadius;
    
    private Double distanceKm;

    
    //==========getter setter ===========

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
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

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getServiceArea() {
		return serviceArea;
	}

	public void setServiceArea(String serviceArea) {
		this.serviceArea = serviceArea;
	}

	public String getYearsExperience() {
		return yearsExperience;
	}

	public void setYearsExperience(String yearsExperience) {
		this.yearsExperience = yearsExperience;
	}

	public List<String> getServicesOffered() {
		return servicesOffered;
	}

	public void setServicesOffered(List<String> servicesOffered) {
		this.servicesOffered = servicesOffered;
	}

	public String getServicesOtherText() {
		return servicesOtherText;
	}

	public void setServicesOtherText(String servicesOtherText) {
		this.servicesOtherText = servicesOtherText;
	}

	public List<String> getSpecializations() {
		return specializations;
	}

	public void setSpecializations(List<String> specializations) {
		this.specializations = specializations;
	}

	public String getSpecializationOtherText() {
		return specializationOtherText;
	}

	public void setSpecializationOtherText(String specializationOtherText) {
		this.specializationOtherText = specializationOtherText;
	}

	public String getServiceRadius() {
		return serviceRadius;
	}

	public void setServiceRadius(String serviceRadius) {
		this.serviceRadius = serviceRadius;
	}

	public Double getDistanceKm() {
		return distanceKm;
	}

	public void setDistanceKm(Double distanceKm) {
		this.distanceKm = distanceKm;
	}
    

    
    
    
    
}
