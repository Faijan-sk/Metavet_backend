package com.example.demo.Dto;


import java.time.LocalDate;

import com.example.demo.Enum.EmploymentType;
import com.example.demo.Enum.Gender;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class DoctorRequestDto {

    @NotNull(message = "User ID is required")
    private String userId;

    @NotNull(message = "Experience is required")
    @Min(value = 0, message = "Experience cannot be negative")
    @Max(value = 50, message = "Experience cannot exceed 50 years")
    private Integer experienceYears;

    @NotBlank(message = "Hospital/Clinic name is required")
    @Size(min = 3, max = 150, message = "Hospital/Clinic name must be between 3 and 150 characters")
    private String hospitalClinicName;

    @NotBlank(message = "Hospital/Clinic address is required")
    @Size(min = 10, max = 300, message = "Address must be between 10 and 300 characters")
    private String hospitalClinicAddress;

    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Pincode must be exactly 6 digits")
    private String pincode;

    @NotBlank(message = "Residential address is required")
    @Size(min = 10, max = 200, message = "Address must be between 10 and 200 characters")
    private String address;

    @NotBlank(message = "Country is required")
    @Size(max = 50, message = "Country name cannot exceed 50 characters")
    private String country;

    @NotBlank(message = "City is required")
    @Pattern(regexp = "^[A-Za-z\\s]{2,50}$", message = "City must contain only letters and spaces")
    private String city;

    @NotBlank(message = "State is required")
    @Pattern(regexp = "^[A-Za-z\\s]{2,50}$", message = "State must contain only letters and spaces")
    private String state;

    @NotBlank(message = "Bio is required")
    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    private String bio;

    @NotNull(message = "Consultation fee is required")
    @Min(value = 0, message = "Consultation fee cannot be negative")
    @Max(value = 50000, message = "Consultation fee cannot exceed 50000")
    private Double consultationFee;

    @NotNull(message = "Gender is required")
    private String gender; // Frontend sends as string (MALE/FEMALE/OTHER)

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @NotBlank(message = "License number is required")
    @Pattern(regexp = "^[A-Z0-9]{6,20}$", message = "License number must be 6-20 alphanumeric characters")
    private String licenseNumber;

    @NotNull(message = "License issue date is required")
    private LocalDate licenseIssueDate;

    private LocalDate licenseExpiryDate;

    @NotBlank(message = "Qualification is required")
    @Size(min = 2, max = 200, message = "Qualification must be between 2 and 200 characters")
    private String qualification;

    @NotBlank(message = "Specialization is required")
    @Size(min = 3, max = 100, message = "Specialization must be between 3 and 100 characters")
    private String specialization;

    @Size(max = 100, message = "Previous workplace cannot exceed 100 characters")
    private String previousWorkplace;

    @NotNull(message = "Joining date is required")
    private LocalDate joiningDate;

    @NotNull(message = "Employment type is required")
    private String employmentType; // Frontend sends as string (FULL_TIME/PART_TIME/CONSULTANT)

    @NotNull(message = "Active status is required")
    private Boolean isActive;

    @NotBlank(message = "Emergency contact number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Emergency contact must be exactly 10 digits")
    private String emergencyContactNumber;

    @Pattern(
        regexp = "^(-?(?:[0-8]?[0-9]|90)(?:\\.[0-9]+)?)$",
        message = "Invalid latitude format"
    )
    private String latitude;

    @Pattern(
        regexp = "^(-?(?:1[0-7][0-9]|[0-9]?[0-9]|180)(?:\\.[0-9]+)?)$",
        message = "Invalid longitude format"
    )
    private String longitude;

    // Constructors
    public DoctorRequestDto() {
    }

    public DoctorRequestDto(String userId, Integer experienceYears, String hospitalClinicName,
                           String hospitalClinicAddress, String pincode, String address, String country,
                           String city, String state, String bio, Double consultationFee, String gender,
                           LocalDate dateOfBirth, String licenseNumber, LocalDate licenseIssueDate,
                           LocalDate licenseExpiryDate, String qualification, String specialization,
                           String previousWorkplace, LocalDate joiningDate, String employmentType,
                           Boolean isActive, String emergencyContactNumber, String latitude, String longitude) {
        this.userId = userId;
        this.experienceYears = experienceYears;
        this.hospitalClinicName = hospitalClinicName;
        this.hospitalClinicAddress = hospitalClinicAddress;
        this.pincode = pincode;
        this.address = address;
        this.country = country;
        this.city = city;
        this.state = state;
        this.bio = bio;
        this.consultationFee = consultationFee;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.licenseNumber = licenseNumber;
        this.licenseIssueDate = licenseIssueDate;
        this.licenseExpiryDate = licenseExpiryDate;
        this.qualification = qualification;
        this.specialization = specialization;
        this.previousWorkplace = previousWorkplace;
        this.joiningDate = joiningDate;
        this.employmentType = employmentType;
        this.isActive = isActive;
        this.emergencyContactNumber = emergencyContactNumber;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getHospitalClinicName() {
        return hospitalClinicName;
    }

    public void setHospitalClinicName(String hospitalClinicName) {
        this.hospitalClinicName = hospitalClinicName;
    }

    public String getHospitalClinicAddress() {
        return hospitalClinicAddress;
    }

    public void setHospitalClinicAddress(String hospitalClinicAddress) {
        this.hospitalClinicAddress = hospitalClinicAddress;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(Double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public LocalDate getLicenseIssueDate() {
        return licenseIssueDate;
    }

    public void setLicenseIssueDate(LocalDate licenseIssueDate) {
        this.licenseIssueDate = licenseIssueDate;
    }

    public LocalDate getLicenseExpiryDate() {
        return licenseExpiryDate;
    }

    public void setLicenseExpiryDate(LocalDate licenseExpiryDate) {
        this.licenseExpiryDate = licenseExpiryDate;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getPreviousWorkplace() {
        return previousWorkplace;
    }

    public void setPreviousWorkplace(String previousWorkplace) {
        this.previousWorkplace = previousWorkplace;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getEmergencyContactNumber() {
        return emergencyContactNumber;
    }

    public void setEmergencyContactNumber(String emergencyContactNumber) {
        this.emergencyContactNumber = emergencyContactNumber;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}