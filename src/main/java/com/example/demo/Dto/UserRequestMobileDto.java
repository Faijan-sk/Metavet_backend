package com.example.demo.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class UserRequestMobileDto {
	
	
	@Email(message = "Please provide a valid email")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Country code is required")
    @Pattern(
        regexp = "^\\+[1-9]\\d{0,3}$",
        message = "Country code must start with + and contain 1-4 digits"
    )
    private String countryCode;

    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^[0-9]{10}$",
        message = "Phone number must be exactly 10 digits"
    )
    private String phoneNumber;

    @NotBlank(message = "First Name is required")
    @Pattern(
        regexp = "^[A-Za-z ]{3,30}$",
        message = "First name must contain only letters and be 3 to 30 characters long"
    )
    private String firstName;

    @NotBlank(message = "Last Name is required")
    @Pattern(
        regexp = "^[A-Za-z ]{3,30}$",
        message = "Last name must contain only letters and be 3 to 30 characters long"
    )
    private String lastName;

    @NotNull(message = "User type is required")
    @Min(value = 1, message = "User type must be 1 (Client), 2 (Doctor), or 3 (Service Provider)")
    @Max(value = 3, message = "User type must be 1 (Client), 2 (Doctor), or 3 (Service Provider)")
    private Integer userType;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

  
    
    
    
    

}
