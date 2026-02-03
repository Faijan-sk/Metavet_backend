package com.example.demo.Dto;

public class MetavetChargesRequestDto {
	private String userType; 
    private String feesType;
    private Double feesValue;

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getFeesType() {
        return feesType;
    }

    public void setFeesType(String feesType) {
        this.feesType = feesType;
    }

	public Double getFeesValue() {
		return feesValue;
	}

	public void setFeesValue(Double feesValue) {
		this.feesValue = feesValue;
	}

   
	
	
	
	

}
