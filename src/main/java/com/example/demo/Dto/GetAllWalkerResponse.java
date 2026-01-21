package com.example.demo.Dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.example.demo.Entities.WalkerKyc.PreferredCommunication;

public class GetAllWalkerResponse {
	
	
	// ===== User =====
    private String uid;
    private String email;
    private String phoneNumber;
    private String fullName;
    
    
 // ===== Service Provider =====
    
    private String serviceType;
    
   //========= KYC Detail ==========
    
    
    private String address;
    private String serviceArea;
    private Integer yearsExperience;
    private String walkRadius;
    private Integer maxPetsPerWalk;
    private List<String> preferredCommunication;
    private Double distanceKm;
    
    
    //==========gettter and setter ============
    
	public String getUid() {
		return uid;
	}
	public Double getDistanceKm() {
		return distanceKm;
	}
	public void setDistanceKm(Double distanceKm) {
		this.distanceKm = distanceKm;
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
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
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
	public Integer getYearsExperience() {
		return yearsExperience;
	}
	public void setYearsExperience(Integer yearsExperience) {
		this.yearsExperience = yearsExperience;
	}
	public String getWalkRadius() {
		return walkRadius;
	}
	public void setWalkRadius(String walkRadius) {
		this.walkRadius = walkRadius;
	}
	public Integer getMaxPetsPerWalk() {
		return maxPetsPerWalk;
	}
	public void setMaxPetsPerWalk(Integer maxPetsPerWalk) {
		this.maxPetsPerWalk = maxPetsPerWalk;
	}
	public List<String> getPreferredCommunication() {
		return preferredCommunication;
	}
	public void setPreferredCommunication(List<String> preferredCommunication) {
		this.preferredCommunication = preferredCommunication;
	}
    
    
    
    
	

}
