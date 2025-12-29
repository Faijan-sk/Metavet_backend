package com.example.demo.Dto;

public class UserResponseDto {
	
	
	

    private String otp;
    private String token;

    public UserResponseDto() {}

    public UserResponseDto(String otp, String token) {
        this.otp = otp;
        this.token = token;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

	
	
	
	
}
