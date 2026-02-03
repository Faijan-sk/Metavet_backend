package com.example.demo.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
public class MetavetCharges extends BaseEntity {
	
	
	@Enumerated(EnumType.STRING)
	private  UserType userType;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private FeesType feesType ;
	
	@Column(name = "fees", nullable = false)
	private Double feesValue ;
	
	public enum FeesType {
		flat , 
		percent
	}
	
	public enum UserType {
		Doctor ,
		Pet_Walker,
		Pet_Groomer ,
		Pet_Behaviourist 
		
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public FeesType getFeesType() {
		return feesType;
	}

	public void setFeesType(FeesType feesType) {
		this.feesType = feesType;
	}

	public Double getFeesValue() {
		return feesValue;
	}

	public void setFeesValue(Double feesValue) {
		this.feesValue = feesValue;
	}
	
	
	
	

}
