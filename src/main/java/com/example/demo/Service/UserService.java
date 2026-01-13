package com.example.demo.Service;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.ServiceProviderRepo;
import com.example.demo.Repository.UserRepo;

import jakarta.transaction.Transactional;

import com.example.demo.Dto.UserRequestDto;
import com.example.demo.Dto.UserRequestMobileDto;
import com.example.demo.Dto.UserResponseDto;
import com.example.demo.Entities.ServiceProvider;
import com.example.demo.Entities.UsersEntity;

@Service
public class UserService {
    
    @Autowired
    private UserRepo userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private ServiceProviderRepo serviceProviderRepo ;
    
    /**
     * Register new user with validation
     * @param request - User registration request
     * @return UsersEntity - Saved user with OTP and token, null if validation fails
     */
     /**
     * Register new user with validation
     * @Transactional ensures rollback if ServiceProvider creation fails
     */
   @Transactional  
public UserResponseDto registerUser(UserRequestDto request) {

    // --- Validations ---
	   	Optional<UsersEntity> existedUser = userRepository.findByEmail(request.getEmail());
	   
    if (userRepository.existsByEmail(request.getEmail())) {
        throw new RuntimeException("Email already exists");
    }
    
    
    
  

    if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
        throw new RuntimeException("Phone number already exists");
    }

    if (request.getUserType() == null || request.getUserType() < 1 || request.getUserType() > 3) {
        throw new RuntimeException("Invalid user type");
    }

    // --- Create User ---
    UsersEntity user = new UsersEntity();
    user.setEmail(request.getEmail());
    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setCountryCode(request.getCountryCode());
    user.setPhoneNumber(request.getPhoneNumber());
    user.setUserType(request.getUserType());

  
    String rawOtp = generateOtp();

  
    String EncodedOtp = passwordEncoder.encode(rawOtp);
    user.setOtp(EncodedOtp);

    UsersEntity savedUser = userRepository.save(user);

    // --- If Service Provider ---
    if (request.getUserType() == 3) {

        if (request.getServiceType() == null || request.getServiceType().trim().isEmpty()) {
            throw new RuntimeException("Service type is required for Service Provider");
        }

        ServiceProvider serviceProvider = new ServiceProvider();

        try {
            ServiceProvider.ServiceType type =
                    ServiceProvider.ServiceType.valueOf(request.getServiceType());
            serviceProvider.setServiceType(type);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid serviceType: " + request.getServiceType() +
                    ". Valid types are: Pet_Walker, Pet_Groomer, Pet_Behaviourist");
        }

        serviceProvider.setOwner(savedUser);
        serviceProviderRepo.save(serviceProvider);
    }

    // --- Prepare Response ONLY (not saved in DB) ---
     
    UserResponseDto responseDto = new UserResponseDto();
    
    
    responseDto.setOtp(rawOtp);
    responseDto.setToken(generateToken(request.getPhoneNumber()));
    
//    savedUser.setOtp(rawOtp);             
//    savedUser.setToken(generateToken(request.getPhoneNumber()));

    return responseDto;
}
   
   
   
   
   
   
   
   
   public UserResponseDto registerUserForMobile(UserRequestMobileDto request) {
	   
	   
	   
	   if (userRepository.existsByEmail(request.getEmail())) {
	        throw new RuntimeException("Email already exists");
	    }

	    if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
	        throw new RuntimeException("Phone number already exists");
	    }

	    if (request.getUserType() == null || request.getUserType() < 1 || request.getUserType() > 3) {
	        throw new RuntimeException("Invalid user type");
	    }
	    
	    // --- Create User ---
	    UsersEntity user = new UsersEntity();
	    user.setEmail(request.getEmail());
	    user.setFirstName(request.getFirstName());
	    user.setLastName(request.getLastName());
	    user.setCountryCode(request.getCountryCode());
	    user.setPhoneNumber(request.getPhoneNumber());
	    user.setUserType(request.getUserType());

	    String rawOtp = generateOtp();

	    
	    String EncodedOtp = passwordEncoder.encode(rawOtp);
	    user.setOtp(EncodedOtp);

	    UsersEntity savedUser = userRepository.save(user);
	    
	   
	 // --- Prepare Response ONLY (not saved in DB) ---
	     
	    UserResponseDto responseDto = new UserResponseDto();
	    
	    responseDto.setOtp(rawOtp);
	    responseDto.setToken(generateToken(request.getPhoneNumber()));
	    
	    
	   
	   
	   return responseDto;
   }
   
   
   
   
   

    
    private String generateOtp() {
        int otp = 1000 + (int) (Math.random() * 9000);
        return String.valueOf(otp);
    }
    
    private String generateToken(String phoneNumber) {
        String rawData = phoneNumber + ":" + System.currentTimeMillis();
        return Base64.getEncoder().encodeToString(rawData.getBytes());
    }
    
    
   
    /**
     * Verify OTP for user authentication
     * @param phoneNumber - User's phone number
     * @param otp - OTP to verify
     * @return boolean - true if OTP is valid
     */
    public boolean verifyOtp(String phoneNumber, String otp) {
        Optional<UsersEntity> userOptional = userRepository.findByPhoneNumber(phoneNumber);
        if (userOptional.isPresent()) {
            UsersEntity user = userOptional.get();
            if (user.getOtp() != null) {
                return passwordEncoder.matches(otp, user.getOtp());
            }
        }
        return false;
    }
    
    /**
     * Find user by email
     * @param email - User email
     * @return UsersEntity - Found user or null
     */
    public UsersEntity findByEmail(String email) {
        Optional<UsersEntity> userOptional = userRepository.findByEmail(email);
        return userOptional.orElse(null);
    }
    
    /**
     * Find user by phone number
     * @param phoneNumber - User phone number
     * @return UsersEntity - Found user or null
     */
    public UsersEntity findByPhoneNumber(String phoneNumber) {
        Optional<UsersEntity> userOptional = userRepository.findByPhoneNumber(phoneNumber);
        return userOptional.orElse(null);
    }
    
    // ============ NEW METHODS FOR APPOINTMENT FUNCTIONALITY ============
    
    /**
     * Find user by ID - Required for AppointmentService
     * @param userId - User ID
     * @return Optional<UsersEntity> - Found user wrapped in Optional
     */
    public Optional<UsersEntity> getUserById(Long userId) {
        return userRepository.findById(userId);
    }
    
    public Optional<UsersEntity> getUserByUuid(UUID uid){
    	return userRepository.findByUid(uid);
    }
    
    /**
     * Get all users - For admin purposes
     * @return List<UsersEntity> - List of all users
     */
    public List<UsersEntity> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Get users by user type - Filter clients, doctors, admins
     * @param userType - User type (1=Client, 2=Doctor, 3=Admin)
     * @return List<UsersEntity> - List of users by type
     */
    public List<UsersEntity> getUsersByType(Integer userType) {
        return userRepository.findByUserType(userType);
    }
    
    /**
     * Get all clients (userType = 1) - For appointment booking
     * @return List<UsersEntity> - List of client users
     */
    public List<UsersEntity> getAllClients() {
        return userRepository.findByUserType(1);
    }
    
    /**
     * Check if user exists by ID
     * @param userId - User ID
     * @return boolean - true if user exists
     */
    public boolean userExists(Long userId) {
        return userRepository.existsById(userId);
    }
    
    /**
     * Update user profile - For profile management
     * @param userId - User ID
     * @param updatedUser - Updated user information
     * @return UsersEntity - Updated user or null if not found
     */
    public UsersEntity updateUserProfile(Long userId, UsersEntity updatedUser) {
        Optional<UsersEntity> existingUser = userRepository.findById(userId);
        if (existingUser.isPresent()) {
            UsersEntity user = existingUser.get();
            
            // Update only allowed fields (not email, phone, userType)
            if (updatedUser.getFirstName() != null) {
                user.setFirstName(updatedUser.getFirstName());
            }
            if (updatedUser.getLastName() != null) {
                user.setLastName(updatedUser.getLastName());
            }
            
            // BaseEntity automatically updates updatedAt timestamp via @PreUpdate
            return userRepository.save(user);
        }
        return null;
    }
    
    /**
     * Get user's full name - Utility method
     * @param userId - User ID
     * @return String - Full name or "Unknown User" if not found
     */
    public String getUserFullName(Long userId) {
        Optional<UsersEntity> user = userRepository.findById(userId);
        if (user.isPresent()) {
            UsersEntity u = user.get();
            return (u.getFirstName() != null ? u.getFirstName() : "") + 
                   " " + (u.getLastName() != null ? u.getLastName() : "");
        }
        return "Unknown User";
    }
    
    /**
     * Search users by name - For admin/search functionality
     * @param searchTerm - Search term to match in first or last name
     * @return List<UsersEntity> - List of matching users
     */
    public List<UsersEntity> searchUsersByName(String searchTerm) {
        return userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            searchTerm, searchTerm);
    }
    
    /**
     * Get users with valid OTP - For debugging/admin purposes
     * @return List<UsersEntity> - List of users with pending OTP verification
     */
    public List<UsersEntity> getUsersWithPendingVerification() {
        return userRepository.findByOtpIsNotNull();
    }
    
    /**
     * Clear user OTP after successful verification
     * @param userId - User ID
     * @return boolean - true if OTP cleared successfully
     */
    public boolean clearUserOtp(Long userId) {
        Optional<UsersEntity> user = userRepository.findById(userId);
        if (user.isPresent()) {
            UsersEntity u = user.get();
            u.setOtp(null);
            userRepository.save(u);
            return true;
        }
        return false;
    }
    
    /**
     * Activate/Deactivate user account - For admin purposes
     * @param userId - User ID
     * @param isActive - Active status
     * @return boolean - true if status updated successfully
     */
    public boolean updateUserActiveStatus(Long userId, boolean isActive) {
        Optional<UsersEntity> user = userRepository.findById(userId);
        if (user.isPresent()) {
            UsersEntity u = user.get();
            // Assuming there's an isActive field in UsersEntity
            // u.setActive(isActive);
            userRepository.save(u);
            return true;
        }
        return false;
    }
    
    /**
     * Count total users by type - For dashboard statistics
     * @param userType - User type
     * @return long - Count of users
     */
    public long countUsersByType(Integer userType) {
        return userRepository.countByUserType(userType);
    }
    
    /**
     * Get recently registered users - For admin dashboard
     * @param limit - Number of recent users to fetch
     * @return List<UsersEntity> - List of recently registered users
     */
    public List<UsersEntity> getRecentlyRegisteredUsers(int limit) {
        // Now createdAt is available from BaseEntity
        return userRepository.findTopRecentUsers(limit);
    }
    
    // ------------------------------------
    /**
     * Get user by ID
     * @param userId - User ID to search
     * @return UsersEntity - User object if found, null if not found
     */
    public UsersEntity getUserByIdEntity(Long userId) {
        try {
            if (userId == null || userId <= 0) {
                return null;
            }
            
            Optional<UsersEntity> userOptional = userRepository.findById(userId);
            return userOptional.orElse(null);
            
        } catch (Exception e) {
            System.err.println("Error fetching user by ID: " + userId + ". Error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get user by ID with exception handling
     * @param userId - User ID to search
     * @return UsersEntity - User object
     * @throws IllegalArgumentException if user not found
     */
    public UsersEntity getUserByIdWithException(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID: " + userId);
        }
        
        Optional<UsersEntity> userOptional = userRepository.findById(userId);
        
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        
        return userOptional.get();
    }

    /**
     * Check if user exists by ID
     * @param userId - User ID to check
     * @return boolean - true if exists, false otherwise
     */
    public boolean existsById(Long userId) {
        try {
            if (userId == null || userId <= 0) {
                return false;
            }
            return userRepository.existsById(userId);
        } catch (Exception e) {
            System.err.println("Error checking user existence: " + userId + ". Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get user by ID with detailed validation
     * @param userId - User ID to search
     * @return UsersEntity - User object with validation
     * @throws IllegalArgumentException for various validation errors
     */
    public UsersEntity getUserByIdWithValidation(Long userId) {
        // Basic validation
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive number");
        }
        
        // Check if user exists
        Optional<UsersEntity> userOptional = userRepository.findById(userId);
        
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        
        UsersEntity user = userOptional.get();
        
        return user;
    }
   
    
    public ResponseEntity<?> deleteClientByUid(UUID uid) {

        Optional<UsersEntity> optionalUser = userRepository.findByUid(uid);

        // ❌ User not found
        if (optionalUser.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        // ✅ Delete user
        userRepository.delete(optionalUser.get());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Client deleted successfully");
    }
    
    
    
}