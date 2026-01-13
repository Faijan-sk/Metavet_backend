package com.example.demo.Service;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Dto.ApiResponse;
import com.example.demo.Entities.UsersEntity;
import com.example.demo.Repository.UserRepo;

@Service
public class LoginAuthService {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Finds user by phone number, generates OTP & token, updates user record,
     * and returns response map.
     */
    public ApiResponse<Map<String, Object>> checkUser(String phoneNumber) {
        try {

            // ===== 1. Validation =====
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                Map<String, String> errors = new HashMap<>();
                errors.put("phone_number", "Phone number is required");
                return ApiResponse.validationError("Validation failed", errors);
            }

            // ===== 2. Find user =====
            Optional<UsersEntity> optionalUser =
                    userRepository.findByPhoneNumber(phoneNumber);

            if (optionalUser.isEmpty()) {
                return ApiResponse.notFound("User not found");
            }

            UsersEntity user = optionalUser.get();

            // ===== 3. BLOCKED / DELETED USER CHECK =====
            if (user.isDeleted()) {
                return ApiResponse.error(
                        "Your account is blocked. Please contact admin.",
                        403,
                        "ACCOUNT_BLOCKED"
                );
            }

            // ===== 4. Generate OTP =====
            String rawOtp = generateOtp();
            String encodedOtp = passwordEncoder.encode(rawOtp);

            user.setOtp(encodedOtp);
            userRepository.save(user);

            // ===== 5. Generate Token =====
            String token = generateToken(phoneNumber);

            // ===== 6. Response =====
            Map<String, Object> response = new HashMap<>();
            response.put("phone_number", phoneNumber);
            response.put("country_code", user.getCountryCode());
            response.put("otp", rawOtp); // ❌ REMOVE IN PRODUCTION
            response.put("token", token);

            return ApiResponse.success("OTP generated successfully", response);

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.serverError("Error while generating OTP");
        }
    }

    // ===== Generate 4-digit OTP =====
    private String generateOtp() {
        int otp = 1000 + (int) (Math.random() * 9000);
        return String.valueOf(otp);
    }

    // ===== Generate temporary token =====
    private String generateToken(String phoneNumber) {
        String rawData = phoneNumber + ":" + System.currentTimeMillis();
        return Base64.getEncoder().encodeToString(rawData.getBytes());
    }
}
