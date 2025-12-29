package com.example.demo.Service;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Entities.ServiceProvider;
import com.example.demo.Entities.UsersEntity;
import com.example.demo.Repository.ServiceProviderRepo;
import com.example.demo.Repository.UserRepo;

@Service
public class OtpAuthService {

    @Autowired
    private UserRepo userRepository;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private ServiceProviderRepo serviceProviderRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;  // ✅ Added for OTP verification

    /**
     * Verify OTP using the temporary Base64 token (format: phoneNumber:timestamp)
     * - Token validity: 5 minutes
     * - Compares encoded OTP in DB with provided plain OTP using PasswordEncoder
     * - On success: clears OTP from DB, marks profile completed,
     *   generates JWT access & refresh tokens and returns user data + tokens.
     *
     * Returns a Map with keys:
     * - status: "success" / "failed" / "error"
     * - message: human readable message
     * - userData: (on success) map with user fields
     * - accessToken / refreshToken: (on success) JWT tokens
     */
    public Map<String, Object> verifyOtpWithToken(String token, String otp) {
        
        Map<String, Object> response = new HashMap<>();

        try {
            // 1. Validate token
            if (token == null || token.trim().isEmpty()) {
                response.put("status", "failed");
                response.put("message", "Token is required");
                return response;
            }

            // 2. Validate OTP format
            if (otp == null || !otp.matches("^[0-9]{4}$")) {
                response.put("status", "failed");
                response.put("message", "OTP must be 4 digits");
                return response;
            }

            // 3. Base64 decode the token
            byte[] decodedBytes;
            try {
                decodedBytes = Base64.getDecoder().decode(token);
            } catch (IllegalArgumentException e) {
                response.put("status", "failed");
                response.put("message", "Invalid token encoding");
                return response;
            }

            String decodedString = new String(decodedBytes);

            // 4. Parse token format: "phoneNumber:timestamp"
            String[] parts = decodedString.split(":");
            if (parts.length != 2) {
                response.put("status", "failed");
                response.put("message", "Invalid token format");
                return response;
            }

            String phoneNumber = parts[0];
            long timestamp;
            try {
                timestamp = Long.parseLong(parts[1]);
            } catch (NumberFormatException e) {
                response.put("status", "failed");
                response.put("message", "Invalid token timestamp format");
                return response;
            }

            // 5. Check token expiry (5 minutes validity)
            long currentTime = System.currentTimeMillis();
            if (currentTime > (timestamp + 5 * 60 * 1000)) {
                response.put("status", "failed");
                response.put("message", "Token expired. Please request a new OTP.");
                return response;
            }

            // 6. Fetch user from database
            Optional<UsersEntity> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
            if (optionalUser.isEmpty()) {
                response.put("status", "failed");
                response.put("message", "User not found");
                return response;
            }
            UsersEntity user = optionalUser.get();

            // 7. Verify stored OTP exists
            if (user.getOtp() == null || user.getOtp().trim().isEmpty()) {
                response.put("status", "failed");
                response.put("message", "No OTP found for this user");
                return response;
            }

            // ✅ FIXED: Use passwordEncoder.matches() to compare encoded OTP
            if (!passwordEncoder.matches(otp, user.getOtp())) {
                response.put("status", "failed");
                response.put("message", "Invalid OTP");
                return response;
            }

            // 8. OTP is valid -> clear OTP and mark profile completed
            user.setOtp(null);
            user.setProfileCompleted(true);
            UsersEntity authenticatedUser = userRepository.save(user);

            // 9. Generate JWT tokens using jwtService
            String jwtAccessToken;
            String jwtRefreshToken;

            try {
                jwtAccessToken = jwtService.generateToken(authenticatedUser);
                jwtRefreshToken = jwtService.generateRefreshToken(authenticatedUser);
            } catch (Exception e) {
                response.put("status", "error");
                response.put("message", "Token generation failed: " + e.getMessage());
                return response;
            }

            // 10. Prepare success response - USER DATA
            Map<String, Object> userData = new HashMap<>();
            userData.put("firstName", authenticatedUser.getFirstName());
            userData.put("lastName", authenticatedUser.getLastName());
            userData.put("phoneNumber", authenticatedUser.getPhoneNumber());
            userData.put("countryCode", authenticatedUser.getCountryCode());
            userData.put("email", authenticatedUser.getEmail());
            userData.put("userType", authenticatedUser.getUserType());
            userData.put("userId", authenticatedUser.getUid());
            userData.put("id", authenticatedUser.getId());
            userData.put("isProfileCompleted", authenticatedUser.isProfileCompleted());
            
            // 11. If Service Provider, fetch and add service type
            if(authenticatedUser.getUserType() == 3) {
                try {
                    ServiceProvider serviceProvider = serviceProviderRepository.findByOwnerUid(authenticatedUser.getUid());
                    if (serviceProvider != null) {
                        userData.put("serviceType", serviceProvider.getServiceType());
                    } else {
                        userData.put("serviceType", null);
                    }
                } catch (Exception e) {
                    System.err.println("Error fetching service provider: " + e.getMessage());
                    userData.put("serviceType", null);
                }
            }

            // 12. Success response structure
            response.put("status", "success");
            response.put("message", "OTP verified successfully");
            response.put("phoneNumber", phoneNumber);
            response.put("userData", userData);
            response.put("accessToken", jwtAccessToken);
            response.put("refreshToken", jwtRefreshToken);

        } catch (Exception e) {
            // Generic fallback for unexpected errors
            response.put("status", "error");
            response.put("message", "Verification failed: " + e.getMessage());
            e.printStackTrace(); // Log for debugging
        }

        return response;
    }
}